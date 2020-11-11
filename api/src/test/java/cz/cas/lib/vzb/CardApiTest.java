package cz.cas.lib.vzb;

import core.index.dto.*;
import cz.cas.lib.vzb.attachment.AttachmentFile;
import cz.cas.lib.vzb.attachment.AttachmentFileStore;
import cz.cas.lib.vzb.attachment.LocalAttachmentFile;
import cz.cas.lib.vzb.attachment.UrlAttachmentFile;
import cz.cas.lib.vzb.card.*;
import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.card.attribute.AttributeStore;
import cz.cas.lib.vzb.card.attribute.AttributeType;
import cz.cas.lib.vzb.card.category.Category;
import cz.cas.lib.vzb.card.category.CategoryStore;
import cz.cas.lib.vzb.card.comment.CardComment;
import cz.cas.lib.vzb.card.comment.CardCommentStore;
import cz.cas.lib.vzb.card.dto.CreateCardDto;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.dto.UpdateCardDto;
import cz.cas.lib.vzb.card.label.Label;
import cz.cas.lib.vzb.card.label.LabelStore;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.init.builders.*;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import cz.cas.lib.vzb.security.user.UserStore;
import helper.ApiTest;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.*;

import static core.util.Utils.asList;
import static core.util.Utils.asSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CardApiTest extends ApiTest {

    private static final String CARD_API_URL = "/api/card/";

    @Inject private CardStore cardStore;
    @Inject private UserService userService;
    @Inject private UserStore userStore;
    @Inject private LabelStore labelStore;
    @Inject private CategoryStore categoryStore;
    @Inject private CardContentStore cardContentStore;
    @Inject private AttributeStore attributeStore;
    @Inject private AttachmentFileStore fileStore;
    @Inject private CardCommentStore commentStore;

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(false).build();


    @Override
    public Set<Class<?>> getIndexedClassesForSolrAnnotationModification() {
        return Collections.singleton(IndexedCard.class);
    }

    @Before
    public void before() {
        transactionTemplate.execute((t) -> userService.create(user));
    }

    @Test
    public void findOne() throws Exception {
        Label labelGreen = LabelBuilder.builder().name("green-ish").color(Color.GREEN).owner(user).build();
        Label labelRed = LabelBuilder.builder().name("red-ish").color(Color.RED).owner(user).build();
        Category categoryFirst = CategoryBuilder.builder().name("fst").ordinalNumber(0).parent(null).owner(user).build();
        Category categorySecond = CategoryBuilder.builder().name("snd").ordinalNumber(0).parent(categoryFirst).owner(user).build();
        Category categoryThird = CategoryBuilder.builder().name("third").ordinalNumber(1).parent(null).owner(user).build();
        Card card = CardBuilder.builder().pid(1).name("c").owner(user).labels(labelGreen, labelRed).categories(categoryFirst, categorySecond, categoryThird).build();

        transactionTemplate.execute(t -> {
            labelStore.save(asList(labelGreen, labelRed));
            categoryStore.save(asList(categoryFirst, categorySecond, categoryThird));
            cardStore.save(card);
            return null;
        });

        securedMvc().perform(get(CARD_API_URL + card.getId())
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.categories", hasSize(3)))
                .andExpect(jsonPath("$.labels", hasSize(2)));


        // CardApi.find() returns soft-deleted cards as well
        transactionTemplate.execute(t -> {
            cardStore.delete(card);
            return null;
        });

        securedMvc().perform(get(CARD_API_URL + card.getId())
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.deleted", is(notNullValue())))
                .andExpect(jsonPath("$.categories", hasSize(3)))
                .andExpect(jsonPath("$.labels", hasSize(2)));
    }

    @Test
    public void list() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("Holá karta").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("Karta číslo 1 prvního uživatele, druh").owner(user).build();
        Card card3 = CardBuilder.builder().pid(3).name("Karta číslo 2 prvního uživatele, první verze").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(asList(card1, card2, card3)));

        Params paramsNameDesc = new Params();
        paramsNameDesc.setSort("name");
        paramsNameDesc.setOrder(Order.DESC);
        paramsNameDesc.setPage(0);
        paramsNameDesc.setPageSize(2);

        securedMvc().perform(
                post(CARD_API_URL + "parametrized")
                        .content(objectMapper.writeValueAsString(paramsNameDesc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(card3.getId())))
                .andExpect(jsonPath("$.items[1].id", is(card2.getId())));

        Params paramsNameAsc = new Params();
        paramsNameAsc.setSort("name");
        paramsNameAsc.setOrder(Order.ASC);
        paramsNameAsc.setPage(0);
        paramsNameAsc.setPageSize(2);

        securedMvc().perform(
                post(CARD_API_URL + "parametrized")
                        .content(objectMapper.writeValueAsString(paramsNameAsc))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(card1.getId())))
                .andExpect(jsonPath("$.items[1].id", is(card2.getId())));
    }

    @Test
    public void createCardThenUpdateContentThenCreateNewContent() throws Exception {
        Label label = LabelBuilder.builder().name("black-ish").color(Color.BLACK).owner(user).build();
        Category categoryFirst = CategoryBuilder.builder().name("fst").ordinalNumber(0).parent(null).owner(null).build();
        Category categorySecond = CategoryBuilder.builder().name("snd").ordinalNumber(0).parent(categoryFirst).owner(null).build();
        Category categoryThird = CategoryBuilder.builder().name("third").ordinalNumber(1).parent(null).owner(null).build();

        transactionTemplate.execute(t -> {
            labelStore.save(label);
            categoryStore.save(asList(categoryFirst, categorySecond, categoryThird));
            return null;
        });

        Attribute attributeString = AttributeBuilder.builder().cardContent(null).ordinalNumber(1).name("blah").type(AttributeType.STRING).value("oj").jsonValue(null).build();
        Attribute attributeDouble = AttributeBuilder.builder().cardContent(null).ordinalNumber(2).name("blah").type(AttributeType.DOUBLE).value(1.5).jsonValue(null).build();
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setId(UUID.randomUUID().toString());
        createCardDto.setName("nejm");
        createCardDto.setNote("blah");
        createCardDto.setAttributes(asList(attributeString, attributeDouble));
        createCardDto.setLabels(asList(label.getId()));
        createCardDto.setCategories(asList(categoryFirst.getId(), categorySecond.getId()));

        //create new card with first content
        String firstContentJson = securedMvc().perform(
                post(CARD_API_URL)
                        .content(objectMapper.writeValueAsString(createCardDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(2)))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
                .andExpect(jsonPath("$.card.labels", hasSize(1)))
                .andExpect(jsonPath("$.card.categories", hasSize(2)))
                .andReturn().getResponse().getContentAsString();

        Params params = new Params();
        params.setFilter(asList(new Filter(IndexedCard.LABELS, FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter(IndexedCard.CATEGORIES, FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter(IndexedCard.CATEGORY_IDS, FilterOperation.EQ, categoryFirst.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);

        CardContent content = objectMapper.readValue(firstContentJson, CardContent.class);
        Card contentCard = content.getCard();
        //update the content, not card
        content.getAttributes().remove(attributeDouble);
        content.getAttributes().iterator().next().setValue("blah");
        UpdateCardContentDto updateCardContentDto = new UpdateCardContentDto();
        updateCardContentDto.setAttributes(new ArrayList<>(content.getAttributes()));

        securedMvc().perform(
                put(CARD_API_URL + createCardDto.getId() + "/content")
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardContentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0].value", is("blah")))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)))
                .andExpect(jsonPath("$.card.labels", hasSize(1)));

        params.setFilter(asList(new Filter("labels", FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, categoryFirst.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);

        //update card, not content
        UpdateCardDto updateCardDto = new UpdateCardDto();
        updateCardDto.setCategories(asList(categoryFirst.getId(), categorySecond.getId(), categoryThird.getId()));
        updateCardDto.setLabels(asList());
        updateCardDto.setLinkedCards(asList(contentCard.getId()));
        updateCardDto.setNote(contentCard.getNote());
        updateCardDto.setName(contentCard.getName());

        securedMvc().perform(
                put(CARD_API_URL + contentCard.getId())
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
                .andExpect(jsonPath("$.card.linkingCards", hasSize(1)));

        params.setFilter(asList(new Filter("labels", FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(0L);
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, categoryFirst.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter("categories", FilterOperation.CONTAINS, categoryThird.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(asList(new Filter("category_ids", FilterOperation.EQ, categoryThird.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);

        //update content and create it as new version
        updateCardContentDto.setNewVersion(true);
        updateCardContentDto.setAttributes(asList(attributeDouble));

        securedMvc().perform(
                put(CARD_API_URL + contentCard.getId() + "/content")
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardContentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.attributes", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0].value", is(attributeDouble.getValue())))
                .andExpect(jsonPath("$.attributes[0].id", not(is(attributeDouble.getId()))))
                .andExpect(jsonPath("$.lastVersion", is(true)))
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.pid", is(1)));

        CardContent oldContentFromDb = cardContentStore.find(content.getId());
        assertThat(oldContentFromDb.getAttributes()).hasSize(1);
        assertThat(oldContentFromDb.getAttributes()).hasOnlyOneElementSatisfying(attribute -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(attribute.getValue()).isEqualTo("blah");
            softly.assertThat(attribute.getId()).isEqualTo(attributeString.getId());
            softly.assertAll();
        });
    }

    @Test
    public void deleteCardInTrashBin() throws Exception {
        Category category = CategoryBuilder.builder().name("cat").ordinalNumber(1).parent(null).owner(user).build();
        Label label = LabelBuilder.builder().name("lab").color(Color.BLACK).owner(user).build();
        Card nonDeletedCard = CardBuilder.builder().pid(2).name("c").note(null).labels(label).owner(user).build();
        Card card1 = CardBuilder.builder().pid(1).name("c").note(null).linkedCards(nonDeletedCard).owner(user).build();
        CardContent cardContent1 = CardContentBuilder.builder().origin(null).lastVersion(true).card(card1).build();
        Attribute attributeString = AttributeBuilder.builder().cardContent(cardContent1).ordinalNumber(1).name("blah").type(AttributeType.STRING).value("s").jsonValue(null).build();

        User otherUser = new User();

        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            labelStore.save(label);
            categoryStore.save(category);
            cardStore.save(asList(nonDeletedCard, card1));
            cardContentStore.save(cardContent1);
            attributeStore.save(attributeString);
            nonDeletedCard.setLinkedCards(asSet(card1));
            cardStore.save(asList(nonDeletedCard, card1));
            return null;
        });

        Result<Card> fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount()).isEqualTo(2L);

        // other user deleting card of another user
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // without soft-delete flag
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // with soft-delete flag
        transactionTemplate.execute(t -> {
            cardStore.delete(card1);
            return null;
        });
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());


        Collection<Card> all = cardStore.findAll();
        assertThat(all).hasSize(1);
        assertThat(all).hasOnlyOneElementSatisfying(card -> {
            SoftAssertions softly = new SoftAssertions();
            softly.assertThat(card.getLinkedCards()).isEmpty();
            softly.assertThat(card.getLinkingCards()).isEmpty();
            softly.assertThat(card.getLabels()).containsExactlyInAnyOrder(label);
            softly.assertAll();
        });

        assertThat(categoryStore.find(category.getId())).isNotNull();
        assertThat(attributeStore.findAll()).isEmpty();
        assertThat(cardContentStore.findAll()).isEmpty();

        fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount()).isEqualTo(1L);
        assertThat(fromIndex.getItems()).containsExactlyInAnyOrder(nonDeletedCard);
    }


    @Test
    public void switchSoftDeleteFlag() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card card3 = CardBuilder.builder().pid(3).name("card3").owner(user).build();
        User otherUser = new User();

        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(asList(card1, card2, card3));
            return null;
        });

        BulkFlagSetDto cardsDto = new BulkFlagSetDto();
        cardsDto.setIds(asList(card1.getId(), card2.getId(), card3.getId()));
        cardsDto.setValue(Boolean.TRUE);
        String cardsDtoJson = objectMapper.writeValueAsString(cardsDto);

        // other user soft-deleting card of another user
        securedMvc().perform(
                post(CARD_API_URL + "set-softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // set soft-delete flag
        securedMvc().perform(
                post(CARD_API_URL + "set-softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        assertThat(cardStore.find(card1.getId()).getDeleted()).isNotNull();
        assertThat(cardStore.find(card2.getId()).getDeleted()).isNotNull();
        assertThat(cardStore.find(card3.getId()).getDeleted()).isNotNull();

        // unset soft-delete flag
        cardsDto.setIds(asList(card1.getId(), card3.getId()));
        cardsDto.setValue(Boolean.FALSE);
        cardsDtoJson = objectMapper.writeValueAsString(cardsDto);
        securedMvc().perform(
                post(CARD_API_URL + "set-softdelete")
                        .content(cardsDtoJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful());

        assertThat(cardStore.find(card1.getId()).getDeleted()).isNull();
        assertThat(cardStore.find(card2.getId()).getDeleted()).isNotNull();
        assertThat(cardStore.find(card3.getId()).getDeleted()).isNull();
    }

    @Test
    public void deleteZeroCardsInTrashBin() throws Exception {
        User otherUser = new User();
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card softDeleted1 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card softDeleted2 = CardBuilder.builder().pid(3).name("card3").owner(user).build();
        Card softDeletedOtherUser = CardBuilder.builder().pid(3).name("card3").owner(otherUser).build();

        // normal card and 2 deleted assert2
        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(asList(card1, softDeleted1, softDeleted2, softDeletedOtherUser));
            return null;
        });

        // no cards in trash
        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "soft-deleted")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        assertThat(Long.parseLong(contentAsString)).isEqualTo(0L);
        Card fromDb1 = cardStore.find(card1.getId());
        assertThat(fromDb1).isNotNull();
        assertThat(fromDb1.getDeleted()).isNull();
        Card fromDb2 = cardStore.find(softDeleted1.getId());
        assertThat(fromDb2).isNotNull();
        assertThat(fromDb2.getDeleted()).isNull();
        Card fromDb3 = cardStore.find(softDeleted2.getId());
        assertThat(fromDb3).isNotNull();
        assertThat(fromDb3.getDeleted()).isNull();
        Card fromDbOtherUser = cardStore.find(softDeletedOtherUser.getId());
        assertThat(fromDbOtherUser).isNotNull();
        assertThat(fromDbOtherUser.getDeleted()).isNull();
    }

    @Test
    public void delete2CardsInTrashBin() throws Exception {
        User otherUser = new User();
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card softDeleted1 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card softDeleted2 = CardBuilder.builder().pid(3).name("card3").owner(user).build();
        Card softDeletedOtherUser = CardBuilder.builder().pid(3).name("card3").owner(otherUser).build();

        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(asList(card1, softDeleted1, softDeleted2, softDeletedOtherUser));
            cardStore.delete(softDeleted1);
            cardStore.delete(softDeleted2);
            cardStore.delete(softDeletedOtherUser);
            return null;
        });

        // soft-deleted 2 of user, 1 of other user -> card of other user is untouched
        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "soft-deleted")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().is2xxSuccessful())
                .andReturn().getResponse().getContentAsString();

        assertThat(Long.parseLong(contentAsString)).isEqualTo(2L);
        Card fromDb1 = cardStore.find(card1.getId());
        assertThat(fromDb1).isNotNull();
        assertThat(fromDb1.getDeleted()).isNull();
        Card fromDb2 = cardStore.find(softDeleted1.getId());
        assertThat(fromDb2).isNull();
        Card fromDb3 = cardStore.find(softDeleted2.getId());
        assertThat(fromDb3).isNull();
        Card fromDbOtherUser = cardStore.find(softDeletedOtherUser.getId());
        assertThat(fromDbOtherUser).isNotNull();
        assertThat(fromDbOtherUser.getDeleted()).isNotNull();
    }

    @Test
    public void listSoftDeleted() throws Exception {
        Card delCard1 = CardBuilder.builder().pid(1).name("A1, Soft Deleted Card").owner(user).build();
        Card delCard2 = CardBuilder.builder().pid(4).name("A4, Soft Deleted Card").owner(user).build();
        Card delCard3 = CardBuilder.builder().pid(6).name("A6, Soft Deleted Card").owner(user).build();

        Card normalCard1 = CardBuilder.builder().pid(2).name("A2, NormalCard").owner(user).build();
        Card normalCard2 = CardBuilder.builder().pid(5).name("A5, NormalCard").owner(user).build();

        User otherUser = UserBuilder.builder().password("password").email("otherUser@mail.cz").allowed(true).build();
        Card delCardForOtherUser = CardBuilder.builder().pid(3).name("A3 Other user deleted card").owner(otherUser).build();

        transactionTemplate.execute((t) -> {
            userService.create(otherUser);
            cardStore.save(asList(delCard1, normalCard1, delCardForOtherUser, delCard2, normalCard2, delCard3));
            cardStore.delete(delCard1);
            cardStore.delete(delCard2);
            cardStore.delete(delCard3);
            cardStore.delete(delCardForOtherUser);
            return null;
        });

        Result<Card> fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount()).isEqualTo(6L);

        Params params = new Params();
        params.setSort("name");
        params.setOrder(Order.ASC);
        params.setPage(0);
        params.setPageSize(2);

        securedMvc().perform(
                post(CARD_API_URL + "deleted")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(delCard1.getId())))
                .andExpect(jsonPath("$.items[1].id", is(delCard2.getId())));

        params = new Params();
        params.setSorting(asList(new SortSpecification("name", Order.DESC)));
        params.setPage(0);
        params.setPageSize(2);

        securedMvc().perform(
                post(CARD_API_URL + "deleted")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(delCard3.getId())))
                .andExpect(jsonPath("$.items[1].id", is(delCard2.getId())));

        fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount()).isEqualTo(6L);
    }

    @Test
    public void updateCardFiles() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(card));
        CardContent content = new CardContent();
        content.setLastVersion(true);
        content.setCard(card);
        transactionTemplate.execute(t -> cardContentStore.save(content));

        LocalAttachmentFile localFile = LocalAttachmentBuilder.builder().id(UUID.randomUUID().toString()).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder().id(UUID.randomUUID().toString()).owner(user).link("https://upload.wikimedia.org/wikipedia/commons/f/f0/LogoMUNI-2018.png").name("url file").size(1024L).contentType(MediaType.IMAGE_PNG_VALUE).type("png").build();
        transactionTemplate.execute(t -> fileStore.save(asList(localFile, urlFile)));

        //update card, not content
        UpdateCardDto updateCardDto = new UpdateCardDto();
        updateCardDto.setFiles(asList(localFile.getId()));
        updateCardDto.setNote("Updated note");
        updateCardDto.setName("New updated Name");

        securedMvc().perform(
                put(CARD_API_URL + card.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(updateCardDto.getName())))
                .andExpect(jsonPath("$.card.note", is(updateCardDto.getNote())))
                .andExpect(jsonPath("$.card.documents", hasSize(1)));


        AttachmentFile fromDbLocal = fileStore.find(localFile.getId());
        assertThat(fromDbLocal).isNotNull();
        assertThat(fromDbLocal.getLinkedCards()).containsExactly(card);
        AttachmentFile fromDbUrl = fileStore.find(urlFile.getId());
        assertThat(fromDbUrl).isNotNull();
        assertThat(fromDbUrl.getLinkedCards()).isEmpty();


        updateCardDto.setFiles(asList(urlFile.getId()));

        securedMvc().perform(
                put(CARD_API_URL + card.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(updateCardDto.getName())))
                .andExpect(jsonPath("$.card.note", is(updateCardDto.getNote())))
                .andExpect(jsonPath("$.card.documents", hasSize(1)));

        fromDbLocal = fileStore.find(localFile.getId());
        assertThat(fromDbLocal).isNotNull();
        assertThat(fromDbLocal.getLinkedCards()).isEmpty();
        fromDbUrl = fileStore.find(urlFile.getId());
        assertThat(fromDbUrl).isNotNull();
        assertThat(fromDbUrl.getLinkedCards()).containsExactly(card);
    }

    @Test
    public void deleteCardWithComments() throws Exception {
        Card card = CardBuilder.builder().pid(1).name("card").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(card));
        CardComment comment = new CardComment("Text", 0, Instant.now(), card);
        transactionTemplate.execute(t -> commentStore.save(comment));
        assertThat(commentStore.find(comment.getId())).isNotNull();
        assertThat(cardStore.find(card.getId())).isNotNull();
        assertThat(cardStore.find(card.getId()).getComments()).containsExactlyInAnyOrder(comment);

        BulkFlagSetDto cardsDto = new BulkFlagSetDto();
        cardsDto.setIds(asList(card.getId()));
        cardsDto.setValue(Boolean.TRUE);

        securedMvc().perform(
                post(CARD_API_URL + "set-softdelete")
                        .content(objectMapper.writeValueAsString(cardsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "soft-deleted")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(Long.parseLong(contentAsString)).isEqualTo(1L);

        Card fromDb = cardStore.find(card.getId());
        assertThat(fromDb).isNull();
        CardComment deletedComment = commentStore.find(comment.getId());
        assertThat(deletedComment).isNull();
    }

}
