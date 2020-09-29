package cz.cas.lib.vzb;

import core.domain.DatedObject;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.attachment.UrlAttachmentFile;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.init.builders.*;
import cz.cas.lib.vzb.reference.marc.record.*;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import helper.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static cz.cas.lib.vzb.util.IndihuMindUtils.AUTHOR_NAME_ENCODING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CitationApiTest extends ApiTest {

    private static final String RECORD_API_URL = "/api/record/";

    private final List<Datafield> TEST_DATAFIELDS = initDatafields();

    @Inject private CitationStore citationStore;
    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject private LabelStore labelStore;
    @Inject private CategoryStore categoryStore;
    @Inject private AttachmentFileStore documentStore;

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(true).build();
    private Card card1, card2, card3;

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedCitation.class);
    }

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
        initCards();
    }


    @Test
    public void createSimple() throws Exception {
        createRecordWithApiFromDto(new CreateCitationDto(), Collections.emptyList(), CitationType.MARC);
    }

    @Test
    public void createSimpleBrief() throws Exception {
        createRecordWithApiFromDto(new CreateCitationDto(), Collections.emptyList(), CitationType.BRIEF);
    }

    @Test
    public void createWithCards() throws Exception {
        createRecordWithApiFromDto(new CreateCitationDto(), asList(card1, card2, card3), CitationType.MARC);
    }

    @Test
    public void createWithExistingName() throws Exception {
        String nameForRecord = "MarcRecord name must be unique";

        CreateCitationDto firstDto = new CreateCitationDto();
        firstDto.setName(nameForRecord);
        Citation firstRecord = createRecordWithApiFromDto(firstDto, Collections.emptyList(), CitationType.MARC);

        CreateCitationDto secondDto = new CreateCitationDto();
        secondDto.setType(CitationType.MARC);
        secondDto.setName(nameForRecord); // same name as first MarcRecord

        securedMvc().perform(
                post(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isConflict());

        Citation firstDtoFromDb = citationStore.find(firstRecord.getId());
        assertThat(firstDtoFromDb).isNotNull();
        assertThat(firstDtoFromDb.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    public void createWithExistingNameDifferentOwner() throws Exception {
        String nameForRecord = "MarcRecord name must be unique";

        CreateCitationDto firstDto = new CreateCitationDto();
        firstDto.setName(nameForRecord);
        Citation firstRecord = createRecordWithApiFromDto(firstDto, Collections.emptyList(), CitationType.MARC);

        User otherUser = UserBuilder.builder().password("other").email("other").allowed(false).build();
        transactionTemplate.execute(status -> userService.create(otherUser));

        CreateCitationDto secondDto = new CreateCitationDto();
        secondDto.setType(CitationType.MARC);
        secondDto.setName(nameForRecord); // same name as first MarcRecord

        String contentAsString = securedMvc().perform(
                post(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondDto))
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        MarcRecord secondRecord = objectMapper.readValue(contentAsString, MarcRecord.class);

        Citation ofUser = citationStore.find(firstRecord.getId());
        Citation ofOtherUser = citationStore.find(secondRecord.getId());
        assertThat(ofUser).isNotNull();
        assertThat(ofOtherUser).isNotNull();

        assertThat(ofUser.getName()).isEqualTo(nameForRecord);
        assertThat(ofUser.getOwner().getId()).isEqualTo(user.getId());

        assertThat(ofOtherUser.getName()).isEqualTo(nameForRecord);
        assertThat(ofOtherUser.getOwner().getId()).isEqualTo(otherUser.getId());
    }

    @Test
    public void updateSimple() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, Collections.emptyList(), CitationType.MARC);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(Collections.emptyList());
        newNameAndDatafields(updateDto);

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getLinkedCards()).isEmpty();
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);
        assertThat(recordFromDb.getDocuments()).isEmpty();

        List<AttachmentFile> oldFilesFromDb = documentStore.findAllInList(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        assertThat(oldFilesFromDb).hasSize(1);
        oldFilesFromDb.forEach(oldDoc -> assertThat(oldDoc.getRecords()).doesNotContain(recordWithApiFromDto));
    }

    @Test
    public void updateSimpleBrief() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, Collections.emptyList(), CitationType.BRIEF);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        updateDto.setContent("New content");
        updateDto.setName("This is new original and unique name");

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        BriefRecord recordFromDb = (BriefRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getLinkedCards()).isEmpty();
        assertThat(recordFromDb.getContent()).isEqualTo(updateDto.getContent());
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());
    }

    @Test
    public void updateFromZeroCardsToThreeCards() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, Collections.emptyList(), CitationType.MARC);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        updateDto.setLinkedCards(asList(card1.getId(), card2.getId(), card3.getId()));
        newNameAndDatafields(updateDto);

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getLinkedCards()).containsExactlyInAnyOrder(card1, card2, card3);
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());

        assertThat(cardStore.find(card1.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card2.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card3.getId()).getRecords()).contains(recordFromDb);
    }

    @Test
    public void updateFromTwoCardsToThreeCards() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, asList(card1, card2), CitationType.MARC);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        updateDto.setLinkedCards(asList(card1.getId(), card2.getId(), card3.getId()));
        newNameAndDatafields(updateDto);

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());

        assertThat(recordFromDb.getLinkedCards()).containsExactlyInAnyOrder(card1, card2, card3);

        assertThat(cardStore.find(card1.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card2.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card3.getId()).getRecords()).contains(recordFromDb);
    }

    @Test
    public void updateFromThreeCardsToOneCard() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, asList(card1, card2, card3), CitationType.MARC);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        updateDto.setLinkedCards(asList(card1.getId()));
        newNameAndDatafields(updateDto);

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());

        assertThat(recordFromDb.getLinkedCards()).containsExactlyInAnyOrder(card1);

        assertThat(cardStore.find(card1.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card2.getId()).getRecords()).doesNotContain(recordFromDb);
        assertThat(cardStore.find(card3.getId()).getRecords()).doesNotContain(recordFromDb);
    }

    @Test
    public void updateCardsAndDocuments() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, asList(card2), CitationType.MARC);

        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder().owner(user).cards(card2, card3).name("new url file").contentType(MediaType.IMAGE_JPEG_VALUE).link("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf").size(42L).type("pdf").build();
        UrlAttachmentFile url2File = UrlAttachmentBuilder.builder().owner(user).cards().name("new url file2").contentType(MediaType.IMAGE_JPEG_VALUE).link("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf").size(42L).type("pdf").build();
        transactionTemplate.execute(s -> documentStore.save(asList(urlFile,url2File)));

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(asList(urlFile.getId(), url2File.getId()));
        updateDto.setLinkedCards(asList(card1.getId(), card3.getId()));
        newNameAndDatafields(updateDto);

        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);

        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());
        assertThat(recordFromDb.getLinkedCards()).containsExactlyInAnyOrder(card1, card3);

        List<AttachmentFile> oldFilesFromDb = documentStore.findAllInList(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        assertThat(oldFilesFromDb).hasSize(1);
        oldFilesFromDb.forEach(oldDoc -> assertThat(oldDoc.getRecords()).doesNotContain(recordFromDb));

        assertThat(cardStore.find(card1.getId()).getRecords()).contains(recordFromDb);
        assertThat(cardStore.find(card2.getId()).getRecords()).doesNotContain(recordFromDb);
        assertThat(cardStore.find(card3.getId()).getRecords()).contains(recordFromDb);
    }

    @Test
    public void updateFromTwoCardsToZeroCards() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, asList(card1, card2), CitationType.MARC);

        UpdateCitationDto updateDto = new UpdateCitationDto();
        updateDto.setId(recordWithApiFromDto.getId());
        updateDto.setDocuments(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        updateDto.setLinkedCards(Collections.emptyList());
        newNameAndDatafields(updateDto);


        securedMvc().perform(
                put(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        MarcRecord recordFromDb = (MarcRecord) citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getName()).isEqualTo("This is new original and unique name");
        assertThat(recordFromDb.getDataFields()).hasSize(TEST_DATAFIELDS.size() + 1);
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(updateDto.getDocuments());

        assertThat(recordFromDb.getLinkedCards()).isEmpty();

        assertThat(cardStore.find(card1.getId()).getRecords()).doesNotContain(recordFromDb);
        assertThat(cardStore.find(card2.getId()).getRecords()).doesNotContain(recordFromDb);
        assertThat(cardStore.find(card3.getId()).getRecords()).doesNotContain(recordFromDb);
    }

    @Test
    public void deleteRecordWithoutCards() throws Exception {
        CreateCitationDto recordDto = new CreateCitationDto();
        Citation recordWithApiFromDto = createRecordWithApiFromDto(recordDto, Collections.emptyList(), CitationType.MARC);

        securedMvc().perform(
                delete(RECORD_API_URL + recordWithApiFromDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        Citation recordFromDb = citationStore.find(recordWithApiFromDto.getId());
        assertThat(recordFromDb).isNull();

        List<AttachmentFile> oldFilesFromDb = documentStore.findAllInList(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        assertThat(oldFilesFromDb).hasSize(1);
        oldFilesFromDb.forEach(oldDoc -> assertThat(oldDoc.getRecords()).doesNotContain(recordWithApiFromDto));
    }

    @Test
    public void deleteRecordWithCards() throws Exception {
        Citation recordWithApiFromDto = createRecordWithApiFromDto(new CreateCitationDto(), asList(card1, card2, card3), CitationType.MARC);

        securedMvc().perform(
                delete(RECORD_API_URL + recordWithApiFromDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        Citation deletedRecord = citationStore.find(recordWithApiFromDto.getId());
        assertThat(deletedRecord).isNull();
        for (Card card : asList(card1, card2, card3)) {
            assertThat(card.getRecords()).doesNotContain(recordWithApiFromDto);
        }

        List<AttachmentFile> oldFilesFromDb = documentStore.findAllInList(TestUtils.extractIds(recordWithApiFromDto.getDocuments()));
        assertThat(oldFilesFromDb).hasSize(1);
        oldFilesFromDb.forEach(oldDoc -> assertThat(oldDoc.getRecords()).doesNotContain(recordWithApiFromDto));
    }

    /**
     * Deleted object (attribute {@link DatedObject#getDeleted()} is not null)
     * should not bee obtainable with {@link CitationApi#find}
     */
    @Test
    public void findDeletedRecord() throws Exception {
        MarcRecord record = new MarcRecord();
        record.setId("2ad11b80-2442-42a3-8f77-c72097a5ba5c");
        record.setOwner(user);
        record.setName("ein Recordoriginalname");
        record.setDeleted(Instant.now().plus(30, ChronoUnit.SECONDS));

        transactionTemplate.execute(t -> {
            citationStore.save(record);
            return null;
        });

        securedMvc().perform(
                get(RECORD_API_URL + record.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(record))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isNotFound());

        Citation deletedRecord = citationStore.find(record.getId());
        assertThat(deletedRecord).isNull();
    }


    private void initCards() {
        Label label1 = LabelBuilder.builder().name("first").color(Color.BLACK).owner(user).build();
        Label label2 = LabelBuilder.builder().name("second").color(Color.WHITE).owner(user).build();
        Category category = CategoryBuilder.builder().name("category").ordinalNumber(1).parent(null).owner(user).build();

        transactionTemplate.execute(t -> {
            labelStore.save(asSet(label1, label2));
            categoryStore.save(category);
            return null;
        });

        card1 = CardBuilder.builder().pid(1).name("card 1").owner(user).categories(category).labels(label1, label2).build();
        card2 = CardBuilder.builder().pid(2).name("card 2").owner(user).categories(category).labels(label1).build();
        card3 = CardBuilder.builder().pid(3).name("card 3").owner(user).categories(category).labels(label2).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card1, card2, card3)));
    }

    private Citation createRecordWithApiFromDto(CreateCitationDto dto, List<Card> withCards, CitationType citationType) throws Exception {
        if (dto.getName() == null || dto.getName().isEmpty())
            dto.setName("name must be original and unique for user!");
        dto.setType(citationType);
        if (citationType == CitationType.MARC) {
            dto.setDataFields(TEST_DATAFIELDS);
        } else dto.setContent("Testing content");

        if (!withCards.isEmpty())
            dto.setLinkedCards(withCards.stream().map(Card::getId).collect(Collectors.toList()));

        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder().owner(user).cards(card1).name("this is elephant url file").contentType(MediaType.IMAGE_JPEG_VALUE).link("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf").size(42L).type("pdf").build();
        transactionTemplate.execute(s -> documentStore.save(urlFile));

        dto.setDocuments(asList(urlFile.getId()));

        String contentAsString = securedMvc().perform(
                post(RECORD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();
        Citation savedRecord = objectMapper.readValue(contentAsString, Citation.class);

        Citation recordFromDb = citationStore.find(savedRecord.getId());
        assertThat(recordFromDb).isNotNull();
        assertThat(recordFromDb.getOwner().getId()).isEqualTo(user.getId());
        assertThat(recordFromDb.getName()).isEqualTo(dto.getName());
        assertThat(recordFromDb.getDocuments()).extracting("id").containsExactlyInAnyOrderElementsOf(dto.getDocuments());

        if (citationType == CitationType.MARC) {
            assertThat(((MarcRecord) recordFromDb).getDataFields()).hasSize(TEST_DATAFIELDS.size());
        } else {
            assertThat(((BriefRecord) recordFromDb).getContent()).isEqualTo(dto.getContent());
        }

        for (Card card : withCards) {
            assertThat(recordFromDb.getLinkedCards()).contains(card);
            assertThat(cardStore.find(card.getId()).getRecords()).contains(recordFromDb);
        }

        return savedRecord;
    }


    // linkedCards attribute is not set in this method
    private void newNameAndDatafields(UpdateCitationDto dto) {
        dto.setName("This is new original and unique name");

        List<Datafield> tmp = new ArrayList<>(TEST_DATAFIELDS);
        tmp.add(new Datafield("264", '#', '#',
                asList(
                        new Subfield('a', "Bratislava"),
                        new Subfield('b', "Jazykovedný ústav Ľudovíta Štúra SAV"),
                        new Subfield('c', "2020")
                )));
        dto.setDataFields(tmp);
    }

    private List<Datafield> initDatafields() {

        Datafield df1 = new Datafield("020", 'a', "0786808772");

        Datafield df2 = new Datafield("100", 'a', "Chabon" + AUTHOR_NAME_ENCODING + "Mišo");
        Datafield df3 = new Datafield("245", 'a', "Summerland");

        Datafield df4 = new Datafield("250", 'a', "1st ed.");

        Datafield df5 = new Datafield("264", '#', '#',
                asList(
                        new Subfield('a', "New York"),
                        new Subfield('b', "Miramax Knižky/Hyperion Books for Children"),
                        new Subfield('c', "2002")
                ));

        Datafield df6 = new Datafield("300", '#', '#',
                asList(
                        new Subfield('a', "500"),
                        new Subfield('c', "22 cm")
                ));

        return asList(df1, df2, df3, df4, df5, df6);
    }

}