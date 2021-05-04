package cz.cas.lib.indihumind.card;

import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import cz.cas.lib.indihumind.card.dto.CardSearchResultDto;
import cz.cas.lib.indihumind.card.dto.CreateCardDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardContentDto;
import cz.cas.lib.indihumind.card.dto.UpdateCardDto;
import cz.cas.lib.indihumind.card.view.CardListDto;
import cz.cas.lib.indihumind.cardcontent.CardContent;
import cz.cas.lib.indihumind.cardcontent.view.CardContentListDto;
import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.util.IndihuMindUtils;
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
import static cz.cas.lib.indihumind.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/card")
@RolesAllowed(Roles.USER)
public class CardApi {

    private CardService service;
    private UserDelegate userDelegate;


    @ApiOperation(value = "Retrieve card. Without LAZY entities, use specific endpoint for them")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = Card.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public Card find(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Retrieve card note", notes = "Possibly large requests (megabytes) because of encoded images.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardNote.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}/card-note", produces = APPLICATION_JSON_VALUE)
    public CardNote findCardNote(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        return service.findCardNote(id);
    }

    @ApiOperation("Creates a new card")
    @ApiResponses({
            @ApiResponse(code = 200, message = "first version of card content with link to the new card"),
            @ApiResponse(code = 403, message = "USER_QUOTA_REACHED, FILE_TOO_BIG, FILE_FORBIDDEN or simply not authorized - see response body for difference"),
            @ApiResponse(code = 409, message = "Card with that ID already exists.")
    })
    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CardContent create(@ApiParam(value = "DTO attributes does not has to have ID or relation to card content") @Valid @RequestBody CreateCardDto createCardDto) {
        return service.createCard(createCardDto);
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
            @ApiParam(value = "dto") @Valid @RequestBody @NotNull UpdateCardDto updateCardDto) {
        return service.updateCard(cardId, updateCardDto);
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
            @ApiParam(value = "dto") @Valid @RequestBody @NotNull UpdateCardContentDto updateCardDto) {
        return service.updateCardContent(cardId, updateCardDto);
    }

    @ApiOperation(value = "Delete single card from trash bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, card was deleted"),
            @ApiResponse(code = 400, message = "Card is not in trash bin"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @DeleteMapping(path = "/{id}")
    public void deleteCardInTrashBin(@ApiParam(value = "card id", required = true) @PathVariable("id") String id) {
        service.deleteSingleCardFromTrashBin(id);
    }

    @ApiOperation(value = "Delete all cards from trash bin (empty trash bin functionality)")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK, All cards from trash bin were deleted")})
    @DeleteMapping(path = "/trash-bin")
    public Result<Card> deleteAllCardsInTrashBin() {
        return service.deleteAllCardsFromTrashBin();
    }

    @ApiOperation(value = "Discard or restore provided cards from trash bin")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK, cards' status was updated"),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @PostMapping(path = "/status", consumes = APPLICATION_JSON_VALUE)
    public void switchCardTrashBinStatus(@ApiParam(value = "DTO with card IDs", required = true) @Valid @RequestBody IndihuMindUtils.IdList dto) {
        service.switchCardTrashBinStatus(dto.getIds());
    }

    @ApiOperation(value = "Searches cards and returns results relevant to the search query.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Searched cards", response = CardSearchResultDto.class, responseContainer = LIST)})
    @GetMapping(path = "/search", produces = APPLICATION_JSON_VALUE)
    public Result<CardSearchResultDto> simpleSearch(
            @ApiParam(value = "query string", required = true) @RequestParam(value = "q") String queryString,
            @ApiParam(value = "page size") @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
            @ApiParam(value = "page number") @RequestParam(value = "page", required = false, defaultValue = "0") int pageNumber) {
        return service.simpleHighlightSearch(queryString, pageNumber, pageSize, userDelegate.getId());
    }

    @ApiOperation(value = "List all cards (not in trash bin)")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CardListDto.class, responseContainer = LIST)})
    @PostMapping(value = "/parametrized", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Result<CardListDto> listParametrized(@ApiParam(value = "Parameters to comply with", required = true) @Valid @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.STATUS, FilterOperation.EQ, Card.CardStatus.AVAILABLE.name(), null));
        return service.findAll(params);
    }

    @ApiOperation(value = "List all cards in trash bin")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Cards from trash bin", response = CardListDto.class, responseContainer = LIST)})
    @PostMapping(path = "/trash-bin", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Result<CardListDto> listAllCardsInTrashBin(@ApiParam(value = "Parameters to comply with", required = true) @Valid @RequestBody Params params) {
        addPrefilter(params, new Filter(IndexedCard.USER_ID, FilterOperation.EQ, userDelegate.getId(), null));
        addPrefilter(params, new Filter(IndexedCard.STATUS, FilterOperation.EQ, Card.CardStatus.TRASHED.name(), null));
        return service.findAll(params);
    }

    @ApiOperation(value = "Gets all contents of the card.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardContentListDto.class, responseContainer = LIST),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(path = "/{id}/contents", produces = APPLICATION_JSON_VALUE)
    public List<CardContentListDto> findAllContents(@ApiParam(value = "card id", required = true) @PathVariable("id") String cardId) {
        return service.findAllContentsForCard(cardId);
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
