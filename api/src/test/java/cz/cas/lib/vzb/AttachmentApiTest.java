package cz.cas.lib.vzb;

import cz.cas.lib.vzb.attachment.*;
import cz.cas.lib.vzb.card.Card;
import cz.cas.lib.vzb.card.CardStore;
import cz.cas.lib.vzb.card.IndexedCard;
import cz.cas.lib.vzb.init.builders.*;
import cz.cas.lib.vzb.reference.marc.record.Citation;
import cz.cas.lib.vzb.reference.marc.record.CitationStore;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Official test showcase for mocking multipart/form-data
 * https://github.com/spring-projects/spring-framework/blob/master/spring-test/src/test/java/org/springframework/test/web/servlet/samples/standalone/MultipartControllerTests.java
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AttachmentApiTest extends ApiTest {

    private static Path TEST_FILES_DIRECTORY;
    private static final String ATTACHMENT_API_URL = "/api/attachment-file/";

    @Inject private CardStore cardStore;
    @Inject private CitationStore citationStore;
    @Inject private AttachmentFileStore fileStore;
    @Inject private AttachmentFileService fileService;
    @Inject private UserService userService;

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(false).build();

    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return asSet(IndexedAttachmentFile.class, IndexedCard.class);
    }

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @BeforeClass  // create directory for test files
    public static void beforeClass() throws IOException {
        Properties props = new Properties();
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        TEST_FILES_DIRECTORY = Paths.get(props.getProperty("vzb.attachment.local.file.path"));

        if (!Files.isDirectory(TEST_FILES_DIRECTORY) && Files.exists(TEST_FILES_DIRECTORY)) {
            throw new RuntimeException("There already exist a file with name meant for test files directory!");
        } else if (!Files.isDirectory(TEST_FILES_DIRECTORY)) {
            Files.createDirectory(TEST_FILES_DIRECTORY);
        }
    }

    @AfterClass // delete directory for test files recursively
    public static void after() throws IOException {
        if (!Files.isDirectory(TEST_FILES_DIRECTORY))
            throw new RuntimeException("Test files directory does not exist and cannot be deleted!");
        try (Stream<Path> walk = Files.walk(TEST_FILES_DIRECTORY)) {
            walk.sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    @Test
    public void saveLocalFile() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card, card2)));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card, card2).build();
        transactionTemplate.execute(t -> citationStore.save(record));

        Path fileInStorage = Files.createFile(TEST_FILES_DIRECTORY.resolve("filename.pdf"));
        RandomAccessFile raf = new RandomAccessFile(fileInStorage.toFile(), "rw");
        raf.setLength(1024); //1KB

        CreateAttachmentDto createDto = new CreateAttachmentDto();
        createDto.setLinkedCards(asList(card.getId(), card2.getId()));
        createDto.setRecords(asList(record.getId()));
        createDto.setName("originalName");
        createDto.setProviderType(AttachmentFileProviderType.LOCAL);
        createDto.setType("pdf");

        byte[] fileContent = Files.readAllBytes(fileInStorage);
        MockMultipartFile filePart = new MockMultipartFile("file", "filename.pdf", null, fileContent);
        byte[] jsonContent = objectMapper.writeValueAsBytes(createDto);
        MockMultipartFile jsonPart = new MockMultipartFile("dto", "dto", "application/json", jsonContent);

        securedMvc().perform(
                multipart(ATTACHMENT_API_URL)
                        .file(filePart)
                        .file(jsonPart)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Set<AttachmentFile> cardFiles = cardStore.find(card.getId()).getDocuments();
        assertThat(cardFiles).hasSize(1);
        Optional<AttachmentFile> optional = cardFiles.stream().findFirst();
        assertThat(optional).isPresent();
        AttachmentFile fromDb = optional.get();
        assertThat(fromDb.getProviderType()).isEqualTo(AttachmentFileProviderType.LOCAL);
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card, card2);
        assertThat(fromDb.getRecords()).containsExactlyInAnyOrder(record);

        Citation recordFromDb = citationStore.find(record.getId());
        assertThat(recordFromDb.getDocuments()).isNotNull();
        assertThat(recordFromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);
    }

    @Test
    public void saveExternalFile() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(card));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card).build();
        Citation record2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card).build();
        transactionTemplate.execute(t -> citationStore.save(asList(record, record2)));

        CreateAttachmentDto createDto = new CreateAttachmentDto();
        createDto.setLinkedCards(asList(card.getId()));
        createDto.setRecords(asList(record.getId(), record2.getId()));
        createDto.setName("husky.gif");
        createDto.setLink("https://media.giphy.com/media/VA4lWBPRhUlUI/source.gif");
        createDto.setProviderId(UUID.randomUUID().toString());
        createDto.setProviderType(AttachmentFileProviderType.DROPBOX);
        createDto.setType("gif");

        byte[] jsonContent = objectMapper.writeValueAsBytes(createDto);
        MockMultipartFile jsonPart = new MockMultipartFile("dto", "dto", "application/json", jsonContent);

        securedMvc().perform(
                multipart(ATTACHMENT_API_URL)
                        .file(jsonPart)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Set<AttachmentFile> cardFiles = cardStore.find(card.getId()).getDocuments();
        assertThat(cardFiles).hasSize(1);
        Optional<AttachmentFile> optional = cardFiles.stream().findFirst();
        assertThat(optional).isPresent();
        AttachmentFile fromDb = cardFiles.stream().findFirst().get();
        assertThat(fromDb.getProviderType()).isEqualTo(AttachmentFileProviderType.DROPBOX);
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card);
        assertThat(fromDb.getRecords()).containsExactlyInAnyOrder(record, record2);

        Citation recordFromDb = citationStore.find(record.getId());
        assertThat(recordFromDb.getDocuments()).isNotNull();
        assertThat(recordFromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Citation record2FromDb = citationStore.find(record2.getId());
        assertThat(record2FromDb.getDocuments()).isNotNull();
        assertThat(record2FromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);
    }

    @Test
    public void saveUrlFileWeb() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card, card2)));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card).build();
        Citation record2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card2).build();
        transactionTemplate.execute(t -> citationStore.save(asList(record, record2)));

        CreateAttachmentDto createDto = new CreateAttachmentDto();
        createDto.setLocation(UrlAttachmentFile.UrlDocumentLocation.WEB);
        createDto.setLinkedCards(asList(card.getId(), card2.getId()));
        createDto.setRecords(asList(record.getId(), record2.getId()));
        createDto.setName("husky.gif");
        createDto.setProviderType(AttachmentFileProviderType.URL);
        createDto.setType("gif");
        createDto.setLink("https://media1.giphy.com/media/VA4lWBPRhUlUI/giphy_s.gif");

        byte[] jsonContent = objectMapper.writeValueAsBytes(createDto);
        MockMultipartFile jsonPart = new MockMultipartFile("dto", "dto", "application/json", jsonContent);

        securedMvc().perform(
                multipart(ATTACHMENT_API_URL)
                        .file(jsonPart)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Set<AttachmentFile> cardFiles = cardStore.find(card.getId()).getDocuments();
        assertThat(cardFiles).hasSize(1);
        Optional<AttachmentFile> optional = cardFiles.stream().findFirst();
        assertThat(optional).isPresent();
        AttachmentFile fromDb = cardFiles.stream().findFirst().get();
        assertThat(fromDb.getProviderType()).isEqualTo(AttachmentFileProviderType.URL);
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card, card2);
        assertThat(fromDb.getRecords()).containsExactlyInAnyOrder(record, record2);

        Citation recordFromDb = citationStore.find(record.getId());
        assertThat(recordFromDb.getDocuments()).isNotNull();
        assertThat(recordFromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Citation record2FromDb = citationStore.find(record2.getId());
        assertThat(record2FromDb.getDocuments()).isNotNull();
        assertThat(record2FromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Path downloadedFile = TEST_FILES_DIRECTORY.resolve(fromDb.getId());
        assertThat(downloadedFile).doesNotExist();
    }

    @Test
    public void saveUrlFileServer() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card, card2)));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card).build();
        Citation record2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card2).build();
        transactionTemplate.execute(t -> citationStore.save(asList(record, record2)));

        CreateAttachmentDto createDto = new CreateAttachmentDto();
        createDto.setLocation(UrlAttachmentFile.UrlDocumentLocation.SERVER);
        createDto.setLinkedCards(asList(card.getId(), card2.getId()));
        createDto.setRecords(asList(record.getId(), record2.getId()));
        createDto.setName("husky.gif");
        createDto.setProviderType(AttachmentFileProviderType.URL);
        createDto.setType("gif");
        createDto.setLink("https://upload.wikimedia.org/wikipedia/commons/f/f0/LogoMUNI-2018.png");

        byte[] jsonContent = objectMapper.writeValueAsBytes(createDto);
        MockMultipartFile jsonPart = new MockMultipartFile("dto", "dto", "application/json", jsonContent);

        securedMvc().perform(
                multipart(ATTACHMENT_API_URL)
                        .file(jsonPart)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Set<AttachmentFile> cardFiles = cardStore.find(card.getId()).getDocuments();
        assertThat(cardFiles).hasSize(1);
        Optional<AttachmentFile> docuemntFromDb = cardFiles.stream().findFirst();
        assertThat(docuemntFromDb).isPresent();
        AttachmentFile fromDb = docuemntFromDb.get();
        assertThat(fromDb.getProviderType()).isEqualTo(AttachmentFileProviderType.URL);
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card, card2);
        assertThat(fromDb.getRecords()).containsExactlyInAnyOrder(record, record2);

        Citation recordFromDb = citationStore.find(record.getId());
        assertThat(recordFromDb.getDocuments()).isNotNull();
        assertThat(recordFromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Citation record2FromDb = citationStore.find(record2.getId());
        assertThat(record2FromDb.getDocuments()).isNotNull();
        assertThat(record2FromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Path downloadedFile = TEST_FILES_DIRECTORY.resolve(fromDb.getId());
        assertThat(downloadedFile).exists();
        assertThat(downloadedFile).isRegularFile();
    }

    @Test
    public void updateNameKeepCards() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(card));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(card).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            card.setDocuments(asSet(file));
            cardStore.save(card);
            return null;
        });

        UpdateAttachmentDto updateDto = new UpdateAttachmentDto();
        updateDto.setName("New Updated Attachment Name");
        updateDto.setLinkedCards(asList(card.getId()));

        securedMvc().perform(
                put(ATTACHMENT_API_URL + uuid)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(uuid);

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(fromDb.getLinkedCards()).extracting("id").containsExactly(card.getId());

        Card fromDbCard = cardStore.find(card.getId());
        assertThat(fromDbCard).isNotNull();
        assertThat(fromDbCard.getDocuments()).extracting("id").containsExactly(file.getId());

    }

    @Test
    public void updateWithAddedCards() throws Exception {
        Card cardForCreate = CardBuilder.builder().pid(1).name("card").owner(user).build();
        Card cardForUpdate1 = CardBuilder.builder().pid(2).name("card").owner(user).build();
        Card cardForUpdate2 = CardBuilder.builder().pid(3).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(cardForCreate, cardForUpdate1, cardForUpdate2)));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(cardForCreate).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            cardForCreate.setDocuments(asSet(file));
            cardStore.save(cardForCreate);
            return null;
        });

        UpdateAttachmentDto updateDto = new UpdateAttachmentDto();
        updateDto.setName("Updated Name");
        updateDto.setLinkedCards(asList(cardForUpdate1.getId(), cardForUpdate2.getId()));

        securedMvc().perform(
                put(ATTACHMENT_API_URL + uuid)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(uuid);

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(fromDb.getLinkedCards())
                .extracting("id")
                .containsExactlyInAnyOrder(cardForUpdate1.getId(), cardForUpdate2.getId());

        Card fromDbCard1 = cardStore.find(cardForCreate.getId());
        assertThat(fromDbCard1).isNotNull();
        assertThat(fromDbCard1.getDocuments()).isEmpty();

        Card fromDbCard2 = cardStore.find(cardForUpdate1.getId());
        assertThat(fromDbCard2).isNotNull();
        assertThat(fromDbCard2.getDocuments())
                .extracting("id")
                .containsExactly(file.getId());

        Card fromDbCard3 = cardStore.find(cardForUpdate2.getId());
        assertThat(fromDbCard3).isNotNull();
        assertThat(fromDbCard3.getDocuments())
                .extracting("id")
                .containsExactly(file.getId());
    }

    @Test
    public void updateKeepCardAddNew() throws Exception {
        Card cardExisting = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card cardAfterUpdate1 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card cardAfterUpdate2 = CardBuilder.builder().pid(3).name("card3").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(cardExisting, cardAfterUpdate1, cardAfterUpdate2)));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(cardExisting).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            cardExisting.setDocuments(asSet(file));
            cardStore.save(cardExisting);
            return null;
        });

        UpdateAttachmentDto updateDto = new UpdateAttachmentDto();
        updateDto.setName("Updated Name");
        updateDto.setLinkedCards(asList(cardExisting.getId(), cardAfterUpdate1.getId(), cardAfterUpdate2.getId()));

        securedMvc().perform(
                put(ATTACHMENT_API_URL + uuid)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(uuid);

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(fromDb.getLinkedCards())
                .extracting("id")
                .containsExactlyInAnyOrder(cardExisting.getId(), cardAfterUpdate1.getId(), cardAfterUpdate2.getId());

        Card fromDbCard1 = cardStore.find(cardExisting.getId());
        assertThat(fromDbCard1).isNotNull();
        assertThat(fromDbCard1.getDocuments())
                .extracting("id")
                .containsExactly(file.getId());

        Card fromDbCard2 = cardStore.find(cardAfterUpdate1.getId());
        assertThat(fromDbCard2).isNotNull();
        assertThat(fromDbCard2.getDocuments())
                .extracting("id")
                .containsExactly(file.getId());

        Card fromDbCard3 = cardStore.find(cardAfterUpdate2.getId());
        assertThat(fromDbCard3).isNotNull();
        assertThat(fromDbCard3.getDocuments())
                .extracting("id")
                .containsExactly(file.getId());
    }

    @Test
    public void updateFromZeroToTwoRecords() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card)));
        Citation recordAfterUpdate = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card).build();
        Citation recordAfterUpdate2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card).build();
        transactionTemplate.execute(t -> citationStore.save(asList(recordAfterUpdate, recordAfterUpdate2)));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(card).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            card.setDocuments(asSet(file));
            cardStore.save(card);
            return null;
        });

        UpdateAttachmentDto updateDto = new UpdateAttachmentDto();
        updateDto.setName("Updated Name");
        updateDto.setLinkedCards(asList(card.getId()));
        updateDto.setRecords(asList(recordAfterUpdate.getId(), recordAfterUpdate2.getId()));

        securedMvc().perform(
                put(ATTACHMENT_API_URL + uuid)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(uuid);

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card);
        assertThat(fromDb.getRecords()).containsExactlyInAnyOrder(recordAfterUpdate, recordAfterUpdate2);

        Card fromDbCard1 = cardStore.find(card.getId());
        assertThat(fromDbCard1).isNotNull();
        assertThat(fromDbCard1.getDocuments()).containsExactly(file);

        Citation recordFromDb = citationStore.find(recordAfterUpdate.getId());
        assertThat(recordFromDb.getDocuments()).isNotNull();
        assertThat(recordFromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);

        Citation record2FromDb = citationStore.find(recordAfterUpdate2.getId());
        assertThat(record2FromDb.getDocuments()).isNotNull();
        assertThat(record2FromDb.getDocuments()).containsExactlyInAnyOrder(fromDb);
    }

    @Test
    public void updateFromTwoToZeroRecords() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card)));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card).build();
        Citation record2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card).build();
        transactionTemplate.execute(t -> citationStore.save(asList(record, record2)));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(card).records(record, record2).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            card.setDocuments(asSet(file));
            cardStore.save(card);
            record.setDocuments(asSet(file));
            record2.setDocuments(asSet(file));
            citationStore.save(asSet(record, record2));
            return null;
        });

        UpdateAttachmentDto updateDto = new UpdateAttachmentDto();
        updateDto.setName("Updated Name");
        updateDto.setLinkedCards(asList(card.getId()));
        updateDto.setRecords(Collections.emptyList());

        securedMvc().perform(
                put(ATTACHMENT_API_URL + uuid)
                        .with(mockedUser(user.getId(), Roles.USER))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(uuid);

        assertThat(fromDb).isNotNull();
        assertThat(fromDb.getName()).isEqualTo(updateDto.getName());
        assertThat(fromDb.getLinkedCards()).containsExactlyInAnyOrder(card);
        assertThat(fromDb.getRecords()).isEmpty();

        Card fromDbCard1 = cardStore.find(card.getId());
        assertThat(fromDbCard1).isNotNull();
        assertThat(fromDbCard1.getDocuments()).containsExactly(file);

        Citation recordFromDb = citationStore.find(record.getId());
        assertThat(recordFromDb.getDocuments()).isEmpty();

        Citation record2FromDb = citationStore.find(record2.getId());
        assertThat(record2FromDb.getDocuments()).isEmpty();
    }

    @Test
    public void downloadFileFromStorage() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(card));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).owner(user).cards(card).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> fileStore.save(file));

        Path fileInStorage = Files.createFile(TEST_FILES_DIRECTORY.resolve(uuid));
        RandomAccessFile raf = new RandomAccessFile(fileInStorage.toFile(), "rw");
        raf.setLength(1024); //1KB

        securedMvc().perform(
                get(ATTACHMENT_API_URL + file.getId() + "/download")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(file.getId());
        assertThat(fromDb).isNotNull();
        assertThat(fileInStorage).exists();
    }

    @Test
    public void deleteFile() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card1, card2)));
        Citation record = CitationBuilder.builder().name("record ein").owner(user).linkedCards(card1, card2).build();
        Citation record2 = CitationBuilder.builder().name("record two").owner(user).linkedCards(card2).build();
        transactionTemplate.execute(t -> citationStore.save(asList(record, record2)));

        final String uuid = UUID.randomUUID().toString();
        LocalAttachmentFile file = LocalAttachmentBuilder.builder().id(uuid).cards(card1, card2).records(record, record2).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        transactionTemplate.execute(t -> {
            fileStore.save(file);
            card1.setDocuments(asSet(file));
            cardStore.save(card1);
            record.setDocuments(asSet(file));
            record2.setDocuments(asSet(file));
            citationStore.save(asSet(record, record2));
            return null;
        });

        Path fileInStorage = Files.createFile(TEST_FILES_DIRECTORY.resolve(uuid));
        RandomAccessFile raf = new RandomAccessFile(fileInStorage.toFile(), "rw");
        raf.setLength(1024); //1KB

        securedMvc().perform(
                delete(ATTACHMENT_API_URL + file.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        AttachmentFile fromDb = fileStore.find(file.getId());
        Card card1FromDb = cardStore.find(card1.getId());
        Card card2FromDb = cardStore.find(card2.getId());
        Citation recordFromDb = citationStore.find(record.getId());
        Citation record2FromDb = citationStore.find(record.getId());

        assertThat(fromDb).isNull();
        assertThat(card1FromDb.getDocuments()).isEmpty();
        assertThat(card2FromDb.getDocuments()).isEmpty();
        assertThat(recordFromDb.getDocuments()).isEmpty();
        assertThat(record2FromDb.getDocuments()).isEmpty();
        assertThat(fileInStorage).exists();

        fileService.attachmentFileStorageCleanup();
        assertThat(fileInStorage).doesNotExist();
    }

    @Test
    public void searchFilesWithName() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card1, card2)));

        LocalAttachmentFile localFile = LocalAttachmentBuilder.builder().owner(user).cards(card1).name("file").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        ExternalAttachmentFile extFile = ExternalAttachmentBuilder.builder().owner(user).cards(card2).name("strong Elephant").type("jpeg").providerId(UUID.randomUUID().toString()).provider(AttachmentFileProviderType.DROPBOX).link("https://upload.wikimedia.org/wikipedia/commons/a/a9/African_Bush_Elephants.jpg").build();
        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder().owner(user).cards(card1).name("this is elephant url file").contentType(MediaType.IMAGE_JPEG_VALUE).link("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf").size(42L).type("pdf").build();
        transactionTemplate.execute(t -> fileStore.save(asList(localFile, extFile, urlFile)));

        securedMvc().perform(
                get(ATTACHMENT_API_URL + "search")
                        .with(mockedUser(user.getId(), Roles.USER))
                        .param("q", "file")
                        .param("pageSize", "5")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].name", is(urlFile.getName())))
                .andExpect(jsonPath("$.items[0].id", is(urlFile.getId())))
                .andExpect(jsonPath("$.items[1].name", is(localFile.getName())))
                .andExpect(jsonPath("$.items[1].id", is(localFile.getId())));

        securedMvc().perform(
                get(ATTACHMENT_API_URL + "search")
                        .with(mockedUser(user.getId(), Roles.USER))
                        .param("q", "elephant")
                        .param("pageSize", "5")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].name", is(urlFile.getName())))
                .andExpect(jsonPath("$.items[0].id", is(urlFile.getId())))
                .andExpect(jsonPath("$.items[1].name", is(extFile.getName())))
                .andExpect(jsonPath("$.items[1].id", is(extFile.getId())));
    }

}
