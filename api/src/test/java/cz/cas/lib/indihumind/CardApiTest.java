package cz.cas.lib.indihumind;

import com.fasterxml.jackson.core.type.TypeReference;
import core.index.dto.*;
import cz.cas.lib.indihumind.card.Card;
import cz.cas.lib.indihumind.card.CardStore;
import cz.cas.lib.indihumind.card.IndexedCard;
import cz.cas.lib.indihumind.card.dto.CreateCardDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardContentDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardDto;
import cz.cas.lib.indihumind.cardattribute.Attribute;
import cz.cas.lib.indihumind.cardattribute.AttributeStore;
import cz.cas.lib.indihumind.cardattribute.AttributeType;
import cz.cas.lib.indihumind.cardcategory.Category;
import cz.cas.lib.indihumind.cardcategory.CategoryStore;
import cz.cas.lib.indihumind.cardcommnet.CardComment;
import cz.cas.lib.indihumind.cardcommnet.CardCommentStore;
import cz.cas.lib.indihumind.cardcontent.CardContent;
import cz.cas.lib.indihumind.cardcontent.CardContentStore;
import cz.cas.lib.indihumind.cardlabel.Label;
import cz.cas.lib.indihumind.cardlabel.LabelStore;
import cz.cas.lib.indihumind.document.*;
import cz.cas.lib.indihumind.init.builders.*;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserService;
import cz.cas.lib.indihumind.security.user.UserStore;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
            labelStore.save(List.of(labelGreen, labelRed));
            categoryStore.save(List.of(categoryFirst, categorySecond, categoryThird));
            cardStore.save(card);
            return null;
        });

        securedMvc().perform(get(CARD_API_URL + card.getId())
                .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pid", is(1)))
                .andExpect(jsonPath("$.categories", hasSize(3)))
                .andExpect(jsonPath("$.labels", hasSize(2)));
    }

    @Test
    public void list() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("Holá karta").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("Karta číslo 1 prvního uživatele, druh").owner(user).build();
        Card card3 = CardBuilder.builder().pid(3).name("Karta číslo 2 prvního uživatele, první verze").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(List.of(card1, card2, card3)));

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
            categoryStore.save(List.of(categoryFirst, categorySecond, categoryThird));
            return null;
        });

        Attribute attributeString = AttributeBuilder.builder().cardContent(null).ordinalNumber(1).name("blah").type(AttributeType.STRING).value("oj").jsonValue(null).build();
        Attribute attributeDouble = AttributeBuilder.builder().cardContent(null).ordinalNumber(2).name("blah").type(AttributeType.DOUBLE).value(1.5).jsonValue(null).build();
        CreateCardDto createCardDto = new CreateCardDto();
        createCardDto.setId(UUID.randomUUID().toString());
        createCardDto.setName("nejm");
        createCardDto.setNote("blah");
        createCardDto.setAttributes(List.of(attributeString, attributeDouble));
        createCardDto.setLabels(List.of(label.getId()));
        createCardDto.setCategories(List.of(categoryFirst.getId(), categorySecond.getId()));

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
        params.setFilter(List.of(new Filter(IndexedCard.LABEL_NAMES, FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter(IndexedCard.CATEGORY_NAMES, FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter(IndexedCard.CATEGORY_IDS, FilterOperation.EQ, categoryFirst.getId(), null)));
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

        params.setFilter(List.of(new Filter("labels", FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter("categories", FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter("category_ids", FilterOperation.EQ, categoryFirst.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);

        //update card, not content
        UpdateCardDto updateCardDto = new UpdateCardDto();
        updateCardDto.setCategories(List.of(categoryFirst.getId(), categorySecond.getId(), categoryThird.getId()));
        updateCardDto.setLabels(List.of());
        updateCardDto.setLinkedCards(List.of(contentCard.getId())); // should ignore reference to itself
        updateCardDto.setNote(contentCard.getStructuredNote().getData());
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
                .andExpect(jsonPath("$.card.linkedCards", hasSize(0)))
                .andExpect(jsonPath("$.card.linkingCards", hasSize(0)));

        params.setFilter(List.of(new Filter("labels", FilterOperation.CONTAINS, label.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(0L);
        params.setFilter(List.of(new Filter("categories", FilterOperation.CONTAINS, categoryFirst.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter("category_ids", FilterOperation.EQ, categoryFirst.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter("categories", FilterOperation.CONTAINS, categoryThird.getName(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);
        params.setFilter(List.of(new Filter("category_ids", FilterOperation.EQ, categoryThird.getId(), null)));
        assertThat(cardStore.findAll(params).getCount()).isEqualTo(1L);

        //update content and create it as new version
        updateCardContentDto.setNewVersion(true);
        updateCardContentDto.setAttributes(List.of(attributeDouble));

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
            cardStore.save(nonDeletedCard);
            return null;
        });

        transactionTemplate.execute(t -> {
            cardStore.save(List.of(card1));
            cardContentStore.save(cardContent1);
            attributeStore.save(attributeString);
            return null;
        });

        transactionTemplate.execute(t -> {
            nonDeletedCard.setLinkedCards(asSet(card1.toReference()));
            cardStore.save(List.of(nonDeletedCard));
            return null;
        });

        Result<Card> fromIndex = cardStore.findAll(new Params());
        assertThat(fromIndex.getCount()).isEqualTo(2L);

        // other user deleting card of another user
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // without TRASHED flag
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // put card in trash bin
        transactionTemplate.execute(t -> {
            card1.setStatus(Card.CardStatus.TRASHED);
            cardStore.save(card1);
            return null;
        });
        // empty trash bin
        securedMvc().perform(
                delete(CARD_API_URL + card1.getId())
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        // list cards from trash bin
        securedMvc().perform(
                post(CARD_API_URL + "trash-bin")
                        .content(objectMapper.writeValueAsString(new Params()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(0)))
                .andExpect(jsonPath("$.items", hasSize(0)));

        // list other cards
        securedMvc().perform(
                post(CARD_API_URL + "parametrized")
                        .content(objectMapper.writeValueAsString(new Params()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(nonDeletedCard.getId())));
    }


    @Test
    public void switchSoftDeleteFlag() throws Exception {
        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card card3 = CardBuilder.builder().pid(3).name("card3").owner(user).build();
        User otherUser = new User();

        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(List.of(card1, card2, card3));
            return null;
        });

        IndihuMindUtils.IdList cardsDto = new IndihuMindUtils.IdList();
        cardsDto.setIds(List.of(card1.getId(), card2.getId(), card3.getId()));

        // other user attempting to put card which he does not own in trash bin
        securedMvc().perform(
                post(CARD_API_URL + "status")
                        .content(objectMapper.writeValueAsString(cardsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(otherUser.getId(), Roles.USER)))
                .andExpect(status().isForbidden());

        // put in trash bin
        securedMvc().perform(
                post(CARD_API_URL + "status")
                        .content(objectMapper.writeValueAsString(cardsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(cardStore.find(card1.getId()).getStatus()).isEqualTo(Card.CardStatus.TRASHED);
        assertThat(cardStore.find(card2.getId()).getStatus()).isEqualTo(Card.CardStatus.TRASHED);
        assertThat(cardStore.find(card3.getId()).getStatus()).isEqualTo(Card.CardStatus.TRASHED);

        // restore from trash bin
        cardsDto.setIds(List.of(card1.getId(), card3.getId()));
        securedMvc().perform(
                post(CARD_API_URL + "status")
                        .content(objectMapper.writeValueAsString(cardsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        assertThat(cardStore.find(card1.getId()).getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
        assertThat(cardStore.find(card2.getId()).getStatus()).isEqualTo(Card.CardStatus.TRASHED);
        assertThat(cardStore.find(card3.getId()).getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
    }

    @Test
    public void deleteZeroCardsInTrashBin() throws Exception {
        User otherUser = new User();
        Card otherUserTrashedCard = CardBuilder.builder().pid(1).name("other user card").status(Card.CardStatus.TRASHED).owner(otherUser).build();

        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).build();
        Card card3 = CardBuilder.builder().pid(3).name("card3").owner(user).build();

        // normal card and 2 deleted assert2
        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(List.of(card1, card2, card3, otherUserTrashedCard));
            return null;
        });

        // remove cards from trash bin (but there is 0 cards)
        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "trash-bin")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Result<Card> result = objectMapper.readValue(contentAsString, new TypeReference<>() {});

        assertThat(result.getCount()).isEqualTo(0L);
        assertThat(result.getItems()).isEmpty();

        Card fromDb1 = cardStore.find(card1.getId());
        assertThat(fromDb1).isNotNull();
        assertThat(fromDb1.getDeleted()).isNull();
        assertThat(fromDb1.getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
        Card fromDb2 = cardStore.find(card2.getId());
        assertThat(fromDb2).isNotNull();
        assertThat(fromDb2.getDeleted()).isNull();
        assertThat(fromDb2.getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
        Card fromDb3 = cardStore.find(card3.getId());
        assertThat(fromDb3).isNotNull();
        assertThat(fromDb3.getDeleted()).isNull();
        assertThat(fromDb3.getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
        Card fromDbOtherUser = cardStore.find(otherUserTrashedCard.getId());
        assertThat(fromDbOtherUser).isNotNull();
        assertThat(fromDbOtherUser.getDeleted()).isNull();
        assertThat(fromDbOtherUser.getStatus()).isEqualTo(Card.CardStatus.TRASHED);
    }

    @Test
    public void delete2CardsInTrashBin() throws Exception {
        User otherUser = new User();
        Card otherUserTrashedCard = CardBuilder.builder().pid(1).name("other user card").owner(otherUser).status(Card.CardStatus.TRASHED).build();

        Card card1 = CardBuilder.builder().pid(1).name("card1").owner(user).build();
        Card card2 = CardBuilder.builder().pid(2).name("card2").owner(user).status(Card.CardStatus.TRASHED).build();
        Card card3 = CardBuilder.builder().pid(3).name("card3").owner(user).status(Card.CardStatus.TRASHED).build();

        transactionTemplate.execute(t -> {
            userStore.save(otherUser);
            cardStore.save(List.of(card1, card2, card3, otherUserTrashedCard));
            return null;
        });

        // remove from trash bin 2 cards, do not remove other user's card from their trash bin
        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "trash-bin")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Result<Card> result = objectMapper.readValue(contentAsString, new TypeReference<>() {});

        assertThat(result.getCount()).isEqualTo(2L);
        assertThat(result.getItems()).isEmpty();

        Card fromDb1 = cardStore.find(card1.getId());
        assertThat(fromDb1).isNotNull();
        assertThat(fromDb1.getDeleted()).isNull();
        assertThat(fromDb1.getStatus()).isEqualTo(Card.CardStatus.AVAILABLE);
        Card fromDb2 = cardStore.find(card2.getId());
        assertThat(fromDb2).isNull();
        Card fromDb3 = cardStore.find(card3.getId());
        assertThat(fromDb3).isNull();
        Card fromDbOtherUser = cardStore.find(otherUserTrashedCard.getId());
        assertThat(fromDbOtherUser).isNotNull();
        assertThat(fromDbOtherUser.getDeleted()).isNull();
        assertThat(fromDbOtherUser.getStatus()).isEqualTo(Card.CardStatus.TRASHED);
    }

    @Test
    public void listCardsInTrashBin() throws Exception {
        Card trashed1 = CardBuilder.builder().pid(1).name("A1,Trashed Card").owner(user).status(Card.CardStatus.TRASHED).build();
        Card trashed2 = CardBuilder.builder().pid(3).name("A3,Trashed Card").owner(user).status(Card.CardStatus.TRASHED).build();
        Card trashed3 = CardBuilder.builder().pid(5).name("A5,Trashed Card").owner(user).status(Card.CardStatus.TRASHED).build();

        Card card4 = CardBuilder.builder().pid(2).name("A2, NormalCard").owner(user).build();
        Card card5 = CardBuilder.builder().pid(4).name("A4, NormalCard").owner(user).build();

        User otherUser = UserBuilder.builder().password("password").email("otherUser@mail.cz").allowed(true).build();
        Card trashedOther = CardBuilder.builder().pid(1).name("A3 Other user deleted card").owner(otherUser).status(Card.CardStatus.TRASHED).build();

        transactionTemplate.execute((t) -> {
            userService.create(otherUser);
            cardStore.save(List.of(trashed1, trashed2, trashed3, card4, card5, card5, trashedOther));
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
                post(CARD_API_URL + "trash-bin")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(trashed1.getId())))
                .andExpect(jsonPath("$.items[1].id", is(trashed2.getId())));

        params = new Params();
        params.setSorting(List.of(new SortSpecification("name", Order.DESC)));
        params.setPage(0);
        params.setPageSize(2);

        securedMvc().perform(
                post(CARD_API_URL + "trash-bin")
                        .content(objectMapper.writeValueAsString(params))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(3)))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.items[0].id", is(trashed3.getId())))
                .andExpect(jsonPath("$.items[1].id", is(trashed2.getId())));

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

        LocalAttachmentFile localFile = LocalAttachmentBuilder.builder().id(UUID.randomUUID().toString()).provider(AttachmentFileProviderType.LOCAL).owner(user).name("filename").size(1024L).contentType(MediaType.APPLICATION_PDF_VALUE).type("pdf").build();
        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder().id(UUID.randomUUID().toString()).owner(user).provider(AttachmentFileProviderType.URL).link("https://upload.wikimedia.org/wikipedia/commons/f/f0/LogoMUNI-2018.png").name("url file").size(1024L).contentType(MediaType.IMAGE_PNG_VALUE).type("png").build();
        transactionTemplate.execute(t -> fileStore.save(List.of(localFile, urlFile)));

        //update card, not content
        UpdateCardDto updateCardDto = new UpdateCardDto();
        updateCardDto.setFiles(List.of(localFile.getId()));
        updateCardDto.setNote("{ Updated note }");
        updateCardDto.setRawNote("Raw updated text");
        updateCardDto.setName("New updated Name");

        securedMvc().perform(
                put(CARD_API_URL + card.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(updateCardDto.getName())))
                .andExpect(jsonPath("$.card.rawNote", is(updateCardDto.getRawNote())))
                .andExpect(jsonPath("$.card.documents", hasSize(1)));


        AttachmentFile fromDbLocal = fileStore.find(localFile.getId());
        assertThat(fromDbLocal).isNotNull();
        assertThat(fromDbLocal.getLinkedCards()).containsExactly(card.toReference());
        AttachmentFile fromDbUrl = fileStore.find(urlFile.getId());
        assertThat(fromDbUrl).isNotNull();
        assertThat(fromDbUrl.getLinkedCards()).isEmpty();


        updateCardDto.setFiles(List.of(urlFile.getId()));

        securedMvc().perform(
                put(CARD_API_URL + card.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(updateCardDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(updateCardDto.getName())))
                .andExpect(jsonPath("$.card.rawNote", is(updateCardDto.getRawNote())))
                .andExpect(jsonPath("$.card.documents", hasSize(1)));

        fromDbLocal = fileStore.find(localFile.getId());
        assertThat(fromDbLocal).isNotNull();
        assertThat(fromDbLocal.getLinkedCards()).isEmpty();
        fromDbUrl = fileStore.find(urlFile.getId());
        assertThat(fromDbUrl).isNotNull();
        assertThat(fromDbUrl.getLinkedCards()).containsExactly(card.toReference());
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

        IndihuMindUtils.IdList cardsDto = new IndihuMindUtils.IdList();
        cardsDto.setIds(List.of(card.getId()));

        securedMvc().perform(
                post(CARD_API_URL + "status")
                        .content(objectMapper.writeValueAsString(cardsDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk());

        String contentAsString = securedMvc().perform(
                delete(CARD_API_URL + "trash-bin")
                        .with(mockedUser(user.getId(), Roles.USER)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Result<Card> result = objectMapper.readValue(contentAsString, new TypeReference<>() {});

        assertThat(result.getCount()).isEqualTo(1L);
        assertThat(result.getItems()).isEmpty();

        Card fromDb = cardStore.find(card.getId());
        assertThat(fromDb).isNull();
        CardComment deletedComment = commentStore.find(comment.getId());
        assertThat(deletedComment).isNull();
    }

    @Test
    public void updateCardLinkingItself() throws Exception {
        Card cardA = CardBuilder.builder().pid(1).name("A").owner(user).build();
        Card cardB = CardBuilder.builder().pid(2).name("B").owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(List.of(cardA, cardB)));
        CardContent content = new CardContent();
        content.setLastVersion(true);
        content.setCard(cardA);
        CardContent contentB = new CardContent();
        contentB.setLastVersion(true);
        contentB.setCard(cardB);
        transactionTemplate.execute(t -> cardContentStore.save(List.of(content, contentB)));

        //update card, not content
        UpdateCardDto dtoUpdateCardB = new UpdateCardDto();
        dtoUpdateCardB.setNote("{ Updated note }");
        dtoUpdateCardB.setRawNote("Raw updated text");
        dtoUpdateCardB.setName("New updated Name");
        dtoUpdateCardB.setLinkedCards(List.of(cardA.getId()));
        dtoUpdateCardB.setLinkingCards(List.of(cardA.getId()));

        securedMvc().perform(
                put(CARD_API_URL + cardB.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(dtoUpdateCardB))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(dtoUpdateCardB.getName())))
                .andExpect(jsonPath("$.card.rawNote", is(dtoUpdateCardB.getRawNote())))
                .andExpect(jsonPath("$.card.linkedCards", hasSize(1)))
                .andExpect(jsonPath("$.card.linkingCards", hasSize(1)));


        Card cardAFromDb = cardStore.find(cardA.getId());
        assertThat(cardAFromDb).isNotNull();
        assertThat(cardAFromDb.getLinkedCards()).containsExactly(cardB.toReference());
        assertThat(cardAFromDb.getLinkingCards()).containsExactly(cardB.toReference());
        Card cardBFromDb = cardStore.find(cardB.getId());
        assertThat(cardBFromDb).isNotNull();
        assertThat(cardBFromDb.getLinkedCards()).containsExactly(cardA.toReference());
        assertThat(cardBFromDb.getLinkingCards()).containsExactly(cardA.toReference());

        // setting links to make card B reference itself
        dtoUpdateCardB.setLinkedCards(List.of(cardA.getId(), cardB.getId()));
        dtoUpdateCardB.setLinkingCards(List.of(cardA.getId(), cardB.getId()));

        securedMvc().perform(
                put(CARD_API_URL + cardB.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(dtoUpdateCardB))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.card", notNullValue()))
                .andExpect(jsonPath("$.card.name", is(dtoUpdateCardB.getName())))
                .andExpect(jsonPath("$.card.rawNote", is(dtoUpdateCardB.getRawNote())))
                .andExpect(jsonPath("$.card.linkedCards", hasSize(1)))
                .andExpect(jsonPath("$.card.linkingCards", hasSize(1)));

        cardAFromDb = cardStore.find(cardA.getId());
        assertThat(cardAFromDb).isNotNull();
        assertThat(cardAFromDb.getLinkedCards()).containsExactly(cardB.toReference());
        assertThat(cardAFromDb.getLinkingCards()).containsExactly(cardB.toReference());
        cardBFromDb = cardStore.find(cardB.getId());
        assertThat(cardBFromDb).isNotNull();
        assertThat(cardBFromDb.getLinkedCards()).containsExactly(cardA.toReference());
        assertThat(cardBFromDb.getLinkingCards()).containsExactly(cardA.toReference());
    }

    @Test
    public void updateCardInTrashBin() throws Exception {
        Card cardA = CardBuilder.builder().pid(1).name("A").status(Card.CardStatus.TRASHED).owner(user).build();
        transactionTemplate.execute(t -> cardStore.save(List.of(cardA)));
        CardContent content = new CardContent();
        content.setLastVersion(true);
        content.setCard(cardA);

        //update card
        UpdateCardDto dto = new UpdateCardDto();
        dto.setNote("{ Updated note }");
        dto.setRawNote("Raw updated text");
        dto.setName("New updated Name");

        securedMvc().perform(
                put(CARD_API_URL + cardA.getId())
                        .with(mockedUser(user.getId(), Roles.USER))
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
