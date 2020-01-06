package cz.cas.lib.vzb;

import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.card.*;
import cz.cas.lib.vzb.card.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.card.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.card.attribute.AttributeStore;
import cz.cas.lib.vzb.card.attribute.AttributeTemplateStore;
import cz.cas.lib.vzb.card.attribute.AttributeType;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.dto.CreateCardDto;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.dto.UpdateCardDto;
import cz.cas.lib.vzb.card.dto.UploadAttachmentFileDto;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.card.template.CardTemplateStore;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.init.TestDataFiller;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import helper.ApiTest;
import io.florianlopes.spring.test.web.servlet.request.MockMvcRequestBuilderUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardApiTest extends ApiTest {

    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject private TestDataFiller testDataFiller;
    @Inject private LabelStore labelStore;
    @Inject private CategoryStore categoryStore;
    @Inject private CardContentStore cardContentStore;
    @Inject private CardTemplateStore cardTemplateStore;
    @Inject private AttributeTemplateStore attributeTemplateStore;
    @Inject private AttributeStore attributeStore;
    @Inject private AttachmentFileStore attachmentFileStore;

    private User user = User.builder().password("password").email("mail").allowed(false).build();

    @Before
    public void before() {
        user.setId("user");
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void findOne() throws Exception {
        Card c1 = new Card();
        c1.setPid(1);
        c1.setName("c");
        c1.setOwner(user);

        Label l1 = new Label(null, Color.GREEN, user);
        Label l2 = new Label(null, Color.RED, user);
        Category cat1 = new Category("fst", 0, null, user);
        Category cat2 = new Category("snd", 0, cat1, user);
        Category cat3 = new Category("third", 1, null, user);

        transactionTemplate.execute((t) -> {
            cardStore.save(c1);
            labelStore.save(asSet(l1, l2));
            categoryStore.save(asSet(cat1, cat3));
            categoryStore.save(cat2);
            return null;
        });

        c1.setLabels(asSet(l1, l2));
        c1.setCategories(asSet(cat1, cat2, cat3));
        transactionTemplate.execute(t -> cardStore.save(c1));

        securedMvc().perform(get("/api/card/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.categories", hasSize(3)))
                .andExpect(jsonPath("$.labels", hasSize(2)))
        ;


        // find should also return correctly soft-deleted cards
        transactionTemplate.execute((t) -> {
            cardStore.delete(c1);
            return null;
        });
        securedMvc().perform(get("/api/card/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.deleted", is(notNullValue())))
                .andExpect(jsonPath("$.categories", hasSize(3)))
                .andExpect(jsonPath("$.labels", hasSize(2)));

    }

    @Test
    public void list() throws Exception {
        Card c1 = new Card();
        c1.setPid(1);
        c1.setName("Holá karta");
        c1.setOwner(user);
        Card c2 = new Card();
        c2.setPid(2);
        c2.setName("Karta číslo 1 prvního uživatele, druh");
        c2.setOwner(user);
        Card c3 = new Card();
        c3.setPid(3);
        c3.setName("Karta číslo 2 prvního uživatele, první verze");
        c3.setOwner(user);

        transactionTemplate.execute((t) -> {
            cardStore.saveAndIndex(asSet(c1, c2, c3));
            return null;
        });

        securedMvc().perform(get("/api/card")
                .param("sort", "name")
                .param("order", "DESC")
                .param("page", "0")
                .param("pageSize", "2")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(c3.getId())))
                .andExpect(jsonPath("$.items[1].id", is(c2.getId())))
        ;

        securedMvc().perform(get("/api/card")
                .param("sorting[0].sort", "name")
                .param("sorting[0].order", "ASC")
                .param("page", "0")
                .param("pageSize", "2")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(c1.getId())))
                .andExpect(jsonPath("$.items[1].id", is(c2.getId())))
        ;
    }

    @Test
    public void createCardThenUpdateContentThenCreateNewContent() throws Exception {
        Label l1 = new Label("black", Color.BLACK, user);
        Category cat1 = new Category("fst", 0, null, null);
        Category cat2 = new Category("snd", 0, cat1, null);
        Category cat3 = new Category("third", 1, null, null);

        transactionTemplate.execute((t) -> {
            labelStore.save(l1);
            categoryStore.save(asSet(cat1, cat3));
            categoryStore.save(cat2);
            return null;
        });
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setLabels(asList(l1.getId()));
        createCardDto.setCategories(asList(cat1.getId(), cat2.getId()));
        createCardDto.setNote("blah");
        createCardDto.setName("nejm");
        createCardDto.setId(UUID.randomUUID().toString());
        Attribute a1 = new Attribute(null, 1, "blah", AttributeType.STRING, "oj", null);
        Attribute a2 = new Attribute(null, 2, "blah", AttributeType.DOUBLE, 1.5, null);
        createCardDto.setAttributes(asList(a1, a2));
        UploadAttachmentFileDto uploadAttachmentFileDto = new UploadAttachmentFileDto();
        uploadAttachmentFileDto.setId(UUID.randomUUID().toString());
        uploadAttachmentFileDto.setLink("link");
        uploadAttachmentFileDto.setName("smthing");
        uploadAttachmentFileDto.setProviderId("providerid");
        uploadAttachmentFileDto.setProviderType(AttachmentFileProviderType.GOOGLE_DRIVE);
        uploadAttachmentFileDto.setCardId(createCardDto.getId());
//        createCardDto.setFiles(asList(uploadAttachmentFileDto));


        //create new card with first content
        String firstContentJson = securedMvc().perform(MockMvcRequestBuilderUtils
                .postForm("/api/card", createCardDto)
                .with(mockedUser(user.getId(), Roles.USER))
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(2)))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
                .andExpect(jsonPath("$.card.labels", hasSize(1)))
                .andExpect(jsonPath("$.card.categories", hasSize(2)))
//                .andExpect(jsonPath("$.card.files", hasSize(1)))
                .andReturn().getResponse().getContentAsString();

        Params params = new Params();
        params.setFilter(asList(new Filter(IndexedCard.LABELS, FilterOperation.CONTAINS, l1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter(IndexedCard.CATEGORIES, FilterOperation.CONTAINS, cat1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter(IndexedCard.CATEGORY_IDS, FilterOperation.EQ, cat1.getId(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
//        params.setFilter(asList(new Filter(IndexedCard.ATTACHMENT_FILES, FilterOperation.EQ, "smthing", null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));

        CardContent content = objectMapper.readValue(firstContentJson, CardContent.class);
        Card c = content.getCard();
        //update the content, not card
        content.getAttributes().remove(a2);
        content.getAttributes().iterator().next().setValue("blah");
        UpdateCardContentDto updateCardContentDto = new UpdateCardContentDto();
        updateCardContentDto.setAttributes(new ArrayList<>(content.getAttributes()));
        securedMvc().perform(put("/api/card/{1}/content", createCardDto.getId())
                .with(mockedUser(user.getId(), Roles.USER))
                .content(objectMapper.writeValueAsString(updateCardContentDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0].value", is("blah")))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
                .andExpect(jsonPath("$.card.labels", hasSize(1)))
//                .andExpect(jsonPath("$.card.files", hasSize(1)))
        ;

        params.setFilter(asList(new Filter("labels", FilterOperation.CONTAINS, l1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, cat1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, cat1.getId(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));

        //update card, not content
        UpdateCardDto updateCardDto = new UpdateCardDto();
        updateCardDto.setCategories(asList(cat1.getId(), cat2.getId(), cat3.getId()));
        updateCardDto.setLabels(asList());
        updateCardDto.setLinkedCards(asList(c.getId()));
        updateCardDto.setNote(c.getNote());
        updateCardDto.setName(c.getName());

        securedMvc().perform(put("/api/card/{1}", c.getId())
                .with(mockedUser(user.getId(), Roles.USER))
                .content(objectMapper.writeValueAsString(updateCardDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0].value", is("blah")))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
                .andExpect(jsonPath("$.card.labels", hasSize(0)))
                .andExpect(jsonPath("$.card.categories", hasSize(3)))
                .andExpect(jsonPath("$.card.linkedCards", hasSize(1)))
                .andExpect(jsonPath("$.card.linkingCards", hasSize(1)))
//                .andExpect(jsonPath("$.card.files", hasSize(1)))
        ;

        params.setFilter(asList(new Filter("labels", FilterOperation.CONTAINS, l1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(0L));
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, cat1.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, cat1.getId(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, cat3.getName(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, cat3.getId(), null)));
        assertThat(cardStore.findAll(params).getCount(), is(1L));
        //attachment file name is not updatable for now
//        params.setFilter(asList(new Filter(IndexedCard.ATTACHMENT_FILES, FilterOperation.EQ, "smthing", null)));
//        assertThat(cardStore.findAll(params).getCount(), is(0L));
//        params.setFilter(asList(new Filter(IndexedCard.ATTACHMENT_FILES, FilterOperation.EQ, "otherName", null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));

        //update content and create it as new version
        updateCardContentDto.setNewVersion(true);
        updateCardContentDto.setAttributes(asList(a2));

        securedMvc().perform(put("/api/card/{1}/content", c.getId())
                .with(mockedUser(user.getId(), Roles.USER))
                .content(objectMapper.writeValueAsString(updateCardContentDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0].value", is(a2.getValue())))
                .andExpect(jsonPath("$.attributes[0].id", not(is(a2.getId()))))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
        ;
        CardContent oldContentFromDb = cardContentStore.find(content.getId());
        assertThat(oldContentFromDb.getAttributes(), hasSize(1));
        assertThat(oldContentFromDb.getAttributes().iterator().next().getValue(), is("blah"));
        assertThat(oldContentFromDb.getAttributes().iterator().next().getId(), is(a1.getId()));
    }

    @Test
    public void delete() throws Exception {
        Category cat1 = new Category("cat", 1, null, user);
        Label l1 = new Label("lab", Color.BLACK, user);
        Card c2 = new Card(2, "c", null, user, asSet(), asSet(l1), asSet(), asSet());
        Card c1 = new Card(1, "c", null, user, asSet(), asSet(), asSet(c2), asSet());
        CardContent cc1 = new CardContent();
        Attribute a1 = new Attribute(cc1, 1, "blah", AttributeType.STRING, "s", null);
        cc1.setCard(c1);
        cc1.setLastVersion(true);

        User otherUser = new User();

        transactionTemplate.execute(t -> {
            userService.save(otherUser);
            labelStore.save(l1);
            categoryStore.save(cat1);
            cardStore.save(c2);
            cardStore.save(c1);
            c2.setLinkedCards(asSet(c1));
            cardStore.saveAndIndex(c2);
            cardContentStore.save(cc1);
            attributeStore.save(a1);
            cardStore.saveAndIndex(c1);
            return null;
        });

        Result<Card> fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount(), is(2L));

        // other user deleting card of another user
        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/{1}", c1.getId()).with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden())
        ;

        // without soft-delete flag
        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden())
        ;

        // with soft-delete flag
        transactionTemplate.execute(t -> {
            cardStore.delete(c1);
            return null;
        });
        securedMvc().perform(
                MockMvcRequestBuilders.delete("/api/card/{1}", c1.getId()).with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());


        Collection<Card> all = cardStore.findAll();
        assertThat(all, hasSize(1));
        Card otherCard = all.iterator().next();
        assertThat(otherCard.getLinkedCards(), empty());
        assertThat(otherCard.getLinkingCards(), empty());
        assertThat(otherCard.getLabels(), containsInAnyOrder(l1));
        assertThat(categoryStore.find(cat1.getId()), notNullValue());
        assertThat(attributeStore.findAll(), empty());
        assertThat(cardContentStore.findAll(), empty());

        fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount(), is(1L));
        assertThat(fromIndex.getItems(), containsInAnyOrder(c2));
    }


    @Test
    public void switchSoftDelete() throws Exception {
        Card card1 = new Card(1, "card1", null, user, asSet(), asSet(), asSet(), asSet());
        Card card2 = new Card(2, "card2", null, user, asSet(), asSet(), asSet(), asSet());
        Card card3 = new Card(3, "card3", null, user, asSet(), asSet(), asSet(), asSet());

        User otherUser = new User();

        transactionTemplate.execute(t -> {
            userService.save(otherUser);
            cardStore.saveAndIndex(card1);
            cardStore.saveAndIndex(card2);
            cardStore.saveAndIndex(card3);
            return null;
        });

        BulkFlagSetDto cardsDto = new BulkFlagSetDto();
        cardsDto.setIds(asList(card1.getId(), card2.getId(), card3.getId()));
        cardsDto.setValue(Boolean.TRUE);
        String cardsDtoJson = objectMapper.writeValueAsString(cardsDto);

        // other user soft-deleting card of another user
        securedMvc().perform(
                MockMvcRequestBuilders.post("/api/card/set_softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // set soft-delete flag
        securedMvc().perform(
                MockMvcRequestBuilders.post("/api/card/set_softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());
        assertThat(cardStore.find(card1.getId()).getDeleted(), notNullValue());
        assertThat(cardStore.find(card2.getId()).getDeleted(), notNullValue());
        assertThat(cardStore.find(card3.getId()).getDeleted(), notNullValue());


        // unset soft-delete flag
        cardsDto.setIds(asList(card1.getId(), card3.getId()));
        cardsDto.setValue(Boolean.FALSE);
        cardsDtoJson = objectMapper.writeValueAsString(cardsDto);
        securedMvc().perform(
                MockMvcRequestBuilders.post("/api/card/set_softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());
        assertThat(cardStore.find(card1.getId()).getDeleted(), nullValue());
        assertThat(cardStore.find(card2.getId()).getDeleted(), notNullValue());
        assertThat(cardStore.find(card3.getId()).getDeleted(), nullValue());
    }

    @Test
    public void listDeleted() throws Exception {
        Card delCard1 = new Card();
        delCard1.setPid(1);
        delCard1.setName("A1, Soft Deleted Card ");
        delCard1.setOwner(user);
        Card delCard2 = new Card();
        delCard2.setPid(4);
        delCard2.setName("A4, Soft Deleted Card ");
        delCard2.setOwner(user);
        Card delCard3 = new Card();
        delCard3.setPid(6);
        delCard3.setName("A6, Soft Deleted Card ");
        delCard3.setOwner(user);
        Card normalCard1 = new Card();
        normalCard1.setPid(2);
        normalCard1.setName("A2, NormalCard");
        normalCard1.setOwner(user);
        Card normalCard2 = new Card();
        normalCard2.setPid(5);
        normalCard2.setName("A5, NormalCard");
        normalCard2.setOwner(user);

        User otherUser = User.builder().password("password").email("otherUser@mail.cz").allowed(true).build();
        Card delCardForOtherUser = new Card();
        delCardForOtherUser.setPid(3);
        delCardForOtherUser.setName("A3 Other user deleted card");
        delCardForOtherUser.setOwner(otherUser);


        transactionTemplate.execute((t) -> {
            userService.create(otherUser);
            cardStore.saveAndIndex(asSet(delCard1, normalCard1, delCardForOtherUser, delCard2, normalCard2, delCard3));
            cardStore.delete(delCard1);
            cardStore.delete(delCard2);
            cardStore.delete(delCard3);
            cardStore.delete(delCardForOtherUser);
            return null;
        });

        Result<Card> fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount(), is(6L));

        securedMvc().perform(get("/api/card/deleted")
                .param("sort", "name")
                .param("order", "ASC")
                .param("page", "0")
                .param("pageSize", "2")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(delCard1.getId())))
                .andExpect(jsonPath("$.items[1].id", is(delCard2.getId())));


        securedMvc().perform(get("/api/card/deleted")
                .param("sorting[0].sort", "name")
                .param("sorting[0].order", "DESC")
                .param("page", "0")
                .param("pageSize", "2")
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(delCard3.getId())))
                .andExpect(jsonPath("$.items[1].id", is(delCard2.getId())));

        fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount(), is(6L));
    }

    //TODO: test card creation with files
//    @Test
//    public void createCardWithLocalFile() throws Exception {
//        Label l1 = new Label("black", Color.BLACK, user);
//        Category cat1 = new Category("fst", 0, null, null);
//        Category cat2 = new Category("snd", 0, cat1, null);
//        Category cat3 = new Category("third", 1, null, null);
//        Attribute a1 = new Attribute(null, 1, "blah", AttributeType.STRING, "oj", null);
//        Attribute a2 = new Attribute(null, 2, "blah", AttributeType.DOUBLE, 1.5, null);
//        transactionTemplate.execute((t) -> {
//            labelStore.save(l1);
//            categoryStore.save(asSet(cat1, cat3));
//            categoryStore.save(cat2);
//            return null;
//        });
//
//        CreateCardDto createCardDto = new CreateCardDto();
//        createCardDto.setId(UUID.randomUUID().toString());
//        createCardDto.setLabels(asList(l1.getId()));
//        createCardDto.setCategories(asList(cat1.getId(), cat2.getId()));
//        createCardDto.setNote("blah");
//        createCardDto.setName("nejm");
//        createCardDto.setAttributes(asList(a1, a2));
//
//        UploadAttachmentFileDto attachmentGoogleDto = new UploadAttachmentFileDto();
//        attachmentGoogleDto.setId(UUID.randomUUID().toString());
//        attachmentGoogleDto.setCardId(createCardDto.getId());
//        attachmentGoogleDto.setProviderType(AttachmentFileProviderType.GOOGLE_DRIVE);
//        attachmentGoogleDto.setName("googleImg");
//        attachmentGoogleDto.setType("jpg");
//        attachmentGoogleDto.setOrdinalNumber(1);
//        attachmentGoogleDto.setLink("link");
//        attachmentGoogleDto.setProviderId("providerid");
//
//        UploadAttachmentFileDto attachmentLocalDto = new UploadAttachmentFileDto();
//        attachmentLocalDto.setId(UUID.randomUUID().toString());
//        attachmentLocalDto.setCardId(createCardDto.getId());
//        attachmentLocalDto.setProviderType(AttachmentFileProviderType.LOCAL);
//        attachmentLocalDto.setName("localImg");
//        attachmentLocalDto.setType("jpg");
//        attachmentLocalDto.setOrdinalNumber(2);
//        attachmentLocalDto.setContent(null);
//
////        Path filePath = Paths.get("src", "test", "resources", "nine_mb.jpg");
////        InputStream fileStream = Files.newInputStream(filePath);
////        MockMultipartFile mockFile = new MockMultipartFile(
////                "files[1].content",
////                "origName.txt",
////                ContentType.TEXT_PLAIN.getMimeType(),
////                fileStream);
//
//        createCardDto.setFiles(asList(attachmentLocalDto));
//
//        Path filePath = Paths.get("src", "test", "resources", "nine_mb.jpg");
//        InputStream fileStream = Files.newInputStream(filePath);
//        MockMultipartFile file = new MockMultipartFile("files[1].content", fileStream);
//
//        String firstContentJson = securedMvc()
//                .perform((RequestBuilder) MockMvcRequestBuilderUtils.postForm("/api/card",createCardDto)
//                        .with(mockedUser(user.getId(), Roles.USER))
//                        .merge(MockMvcRequestBuilders
//                        .multipart("/whatever,stejne se nepouzije")
//                        .file(file))
//                )
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.attributes", hasSize(2)))
//                .andExpect(jsonPath("$.lastVersion", is(true)))
//                .andExpect(jsonPath("$.card", notNullValue()))
//                .andExpect(jsonPath("$.card.pid", is(1)))
//                .andExpect(jsonPath("$.card.labels", hasSize(1)))
//                .andExpect(jsonPath("$.card.categories", hasSize(2)))
//                .andExpect(jsonPath("$.card.files", hasSize(1)))
//                .andDo(print())
//                .andReturn().getResponse().getContentAsString();

//        Params params = new Params();
//        params.setFilter(asList(new Filter(IndexedCard.LABELS, FilterOperation.EQ, l1.getName(), null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));
//        params.setFilter(asList(new Filter(IndexedCard.CATEGORIES, FilterOperation.EQ, cat1.getName(), null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));
//        params.setFilter(asList(new Filter(IndexedCard.CATEGORY_IDS, FilterOperation.EQ, cat1.getId(), null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));
//        params.setFilter(asList(new Filter(IndexedCard.ATTACHMENT_FILES, FilterOperation.EQ, "googleImg", null)));
//        assertThat(cardStore.findAll(params).getCount(), is(1L));
//    }
}
