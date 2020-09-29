package cz.cas.lib.vzb.card;

import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.vzb.card.dto.CardSearchResultDto;
import cz.cas.lib.vzb.card.dto.CreateCardDto;
import cz.cas.lib.vzb.card.dto.UpdateCardContentDto;
import cz.cas.lib.vzb.card.dto.UpdateCardDto;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.util.ResponseContainer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

import static core.util.Utils.addPrefilter;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/card")
@RolesAllowed(Roles.USER)
public class CardApi {

    private CardService service;
    private UserDelegate userDelegate;


    @ApiOperation("Creates a new card")
    @ApiResponses({
            @ApiResponse(code = 200, message = "first version of card content with link to the new card"),
            @ApiResponse(code = 403, message = "USER_QUOTA_REACHED, FILE_TOO_BIG, FILE_FORBIDDEN or simply not authorized - see response body for difference"),
            @ApiResponse(code = 409, message = "Card with that ID already exists.")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CardContent create(
            @ApiParam(value = "DTO.. attributes does not has to have ID or relation to card content") @Valid @RequestBody CreateCardDto createCardDto) {
        return service.createCard(createCardDto);
    }


    @ApiOperation(value = "Updates the latest content of the card.",
            notes = "If newVersion is set to true, new version of content is created, otherwise the old one is updated.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "OK, content was updated/created"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @PutMapping(path = "/{id}/content", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CardContent updateContent(
            @ApiParam(value = "card id") @PathVariable("id") String cardId,
            @ApiParam(value = "dto") @RequestBody @NotNull UpdateCardContentDto updateCardDto) {
        return service.updateCardContent(cardId, updateCardDto);
    }


    @ApiOperation(value = "Updates card and its relationships with cards, categories, labels, records")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, entity was updated"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @PutMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CardContent update(
            @ApiParam(value = "card id") @PathVariable("id") String cardId,
            @ApiParam(value = "dto") @Valid @RequestBody UpdateCardDto updateCardDto) {
        return service.updateCard(cardId, updateCardDto);
    }


    @ApiOperation(value = "Hard delete for a single card with soft-deletion flag and all card's contents",
            notes = "Fully remove card from trash bin functionality")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, the card was fully removed"),
            @ApiResponse(code = 403, message = "Card is not soft-deleted (in trash bin) | Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteCardInTrashBin(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        service.eraseSingleCardFromTrashBin(id);
    }


    // TODO: Test
    @ApiOperation(value = "Hard delete for all cards with soft-deletion flag and their contents",
            notes = "Empty whole cards trash bin functionality")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, all cards were fully removed"),
    })
    @DeleteMapping(path = "/soft-deleted")
    public long deleteAllCardsInTrashBin() {
        return service.eraseAllCardsFromTrashBin();
    }


    @ApiOperation(value = "Operation to set soft-delete status for card")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, card's soft-deletion flag updated"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @PostMapping(path = "/set-softdelete", consumes = APPLICATION_JSON_VALUE)
    public void switchSoftDeletionFlag(
            @ApiParam(value = "DTO with cards IDs and value for set/unset soft-delete", required = true) @Valid @RequestBody BulkFlagSetDto dto) {
        service.switchSoftDeletionFlag(dto);
    }


    @ApiOperation(value = "Searches cards and returns results relevant to the search query.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Searched cards", response = CardSearchResultDto.class, responseContainer = ResponseContainer.LIST)
    })
    @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
    public Result<CardSearchResultDto> simpleSearch(
            @ApiParam(value = "query string", required = true) @RequestParam(value = "q") String queryString,
            @ApiParam(value = "page size, 0=disabled pagination") @RequestParam(value = "pageSize", required = false, defaultValue = "0") int pageSize,
            @ApiParam(value = "page number") @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber) {
        return service.simpleSearch(queryString, userDelegate.getId(), pageSize, pageNumber);
    }


    @ApiOperation(value = "Retrieves all instances (that are not soft-deleted) that respect the selected parameters", notes = "POST Body variant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "NON-soft-deleted cards", response = Card.class, responseContainer = ResponseContainer.LIST)
    })
    @PostMapping(value = "/parametrized", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Result<Card> listParametrized(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.DELETED, FilterOperation.IS_NULL, null, null));
        return service.findAll(params);
    }


    @ApiOperation(value = "Gets all soft-deleted cards complying with given parameters")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Soft-deleted cards", response = Card.class, responseContainer = ResponseContainer.LIST)
    })
    @PostMapping(path = "/deleted", produces = APPLICATION_JSON_VALUE)
    public Result<Card> listSoftDeleted(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.DELETED, FilterOperation.NOT_NULL, null, null));
        return service.findAll(params);
    }


    @ApiOperation(value = "Gets all contents of the card.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardContent.class, responseContainer = ResponseContainer.LIST),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}/content", produces = APPLICATION_JSON_VALUE)
    public List<CardContent> findAllContents(@ApiParam(value = "card id", required = true) @PathVariable("id") String cardId) {
        return service.findAllContentsForCard(cardId);
    }


    @ApiOperation(value = "Retrieve card with relations (categories, labels etc.) but without contents")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Card.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Card find(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @Inject
    public void setService(CardService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

}
