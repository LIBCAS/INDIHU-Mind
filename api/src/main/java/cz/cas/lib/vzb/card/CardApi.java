package cz.cas.lib.vzb.card;

import core.exception.ForbiddenObject;
import core.exception.MissingObject;
import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.card.dto.CardSearchResultDto;
import cz.cas.lib.vzb.card.dto.CreateCardDto;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.dto.UpdateCardDto;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.exception.ForbiddenFileExtensionException;
import cz.cas.lib.vzb.exception.UserQuotaReachedException;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

import static core.util.Utils.*;

@Slf4j
@RestController
@RequestMapping("/api/card")
@RolesAllowed(Roles.USER)
public class CardApi {

    @Getter
    private CardService service;
    private UserDelegate userDelegate;
    private CardContentStore cardContentStore;

    @ApiOperation("Creates new card")
    @ApiResponses({
            @ApiResponse(message = "first version of card content with link to the new card", code = 200),
            @ApiResponse(message = "USER_QUOTA_REACHED, FILE_TOO_BIG, FILE_EXTENSION_FORBIDDEN or simply not authorized - see response body to distinguish between these 3", code = 403)
    })
    @RequestMapping(method = RequestMethod.POST)
    @Transactional
    public CardContent create(
            @ApiParam(value = "DTO.. attributes does not has to have ID or relation to card content") @Valid @ModelAttribute CreateCardDto createCardDto
    ) throws UserQuotaReachedException, ForbiddenFileExtensionException {
        return service.createCard(createCardDto);
    }

    @ApiOperation(value = "Updates the latest content of the card.", notes = "If newVersion is set to true, new version of content is created, otherwise the old one is updated.")
    @ApiResponses({
            @ApiResponse(message = "the updated content with relation to card", code = 200)
    })
    @RequestMapping(method = RequestMethod.PUT, path = "/{id}/content")
    @Transactional
    public CardContent updateContent(
            @ApiParam(value = "card id") @PathVariable("id") String cardId,
            @ApiParam(value = "dto") @RequestBody @NotNull UpdateCardContentDto updateCardDto
    ) {
        return service.updateCardContent(cardId, updateCardDto);
    }

    @ApiOperation(value = "Updates card or/and one particular content.", notes = "One of the two can be null (card/card content) or both may be filled. If newVersion is set to true, new version of content is created, otherwise the old one is updated.")
    @ApiResponses({
            @ApiResponse(message = "the updated content with relation to card", code = 200)
    })
    @RequestMapping(method = RequestMethod.PUT, path = "/{id}")
    @Transactional
    public CardContent update(
            @ApiParam(value = "card id") @PathVariable("id") String cardId,
            @ApiParam(value = "dto") @RequestBody @Valid UpdateCardDto updateCardDto
    ) {
        return service.updateCard(cardId, updateCardDto);
    }

    @ApiOperation(value = "Deletes card and all its content versions, card must have set soft-delete flag")
    @RequestMapping(method = RequestMethod.DELETE, path = "/{id}")
    @Transactional
    public void delete(
            @ApiParam(value = "card id", required = true) @PathVariable("id") String id
    ) {
        Card card = service.find(id);
        notNull(card, () -> new MissingObject(Card.class, id));
        eq(card.getOwner(), userDelegate.getUser(), () -> new ForbiddenObject(card));
        service.hardDelete(card);
    }

    // TODO: Test
    @ApiOperation(value = "Hard delete for all soft-deleted cards (and their contents) of calling user")
    @RequestMapping(method = RequestMethod.DELETE, path = "/soft_deleted")
    @ApiResponse(message = "Number of deleted cards for user", code = 200)
    @Transactional
    public long deleteSoftDeletedCardsOfUser() {
        return service.deleteSoftDeletedCardsOfUser();
    }

    @ApiOperation(value = "Operation to set soft-delete status for card")
    @RequestMapping(method = RequestMethod.POST, path = "/set_softdelete")
    @Transactional
    public void setSoftDelete(
            @ApiParam(value = "DTO with IDs of cards and value for set/unset soft-delete", required = true) @Valid @RequestBody BulkFlagSetDto dto) {
        service.setSoftDelete(dto);
    }

    @ApiOperation(value = "Searches cards and returns results relevant to the search query.")
    @RequestMapping(method = RequestMethod.GET, path = "/search")
    public Result<CardSearchResultDto> simpleSearch(
            @ApiParam(value = "query string", required = true) @RequestParam(value = "q") String q,
            @ApiParam(value = "page size, 0=disabled pagination") @RequestParam(value = "pageSize", required = false, defaultValue = "0") int pageSize,
            @ApiParam(value = "page number") @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber
    ) {
        return service.simpleSearch(q, userDelegate.getId(), pageSize, pageNumber);
    }

    @ApiOperation(value = "Gets all instances (that are not soft-deleted) that respect the selected parameters")
    @RequestMapping(method = RequestMethod.GET)
    public Result<Card> list(@ApiParam(value = "Parameters to comply with", required = true)
                             @ModelAttribute Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.DELETED, FilterOperation.IS_NULL, null, null));
        return service.findAll(params);
    }

    @ApiOperation(value = "Gets all instances (that are not soft-deleted) that respect the selected parameters",
            notes = "Returns sorted list of instances with total number. Same as the GET / method, " +
                    "but parameters are supplied in POST body.", response = Result.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful response", response = Result.class)})
    @RequestMapping(value = "/parametrized", method = RequestMethod.POST)
    @Transactional
    public Result<Card> listPost(@ApiParam(value = "Parameters to comply with", required = true)
                                 @RequestBody Params params) {
        return list(params);
    }

    @ApiOperation(value = "Gets all instances of Card that are soft-deleted and respect the selected parameters")
    @RequestMapping(method = RequestMethod.GET, path = "/deleted")
    public Result<Card> listDeleted(@ApiParam(value = "Parameters to comply with", required = true)
                                    @ModelAttribute Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.DELETED, FilterOperation.NOT_NULL, null, null));
        return service.findAll(params);
    }

    @ApiOperation(value = "Gets contents of the card.")
    @RequestMapping(method = RequestMethod.GET, path = "/{id}/content")
    public Collection<CardContent> findAllContents(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        return cardContentStore.findAllOfCard(id);
    }

    @ApiOperation(value = "Get card, with relations (categories, labels etc.) but without contents")
    @RequestMapping(method = RequestMethod.GET, path = "/{id}")
    public Card find(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        Card card = service.find(id);
        notNull(card, () -> new MissingObject(Card.class, id));
        eq(card.getOwner().getId(), userDelegate.getId(), () -> new ForbiddenObject(Card.class, id));
        return card;
    }

    @Inject
    public void setService(CardService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

    @Inject
    public void setCardContentStore(CardContentStore cardContentStore) {
        this.cardContentStore = cardContentStore;
    }
}
