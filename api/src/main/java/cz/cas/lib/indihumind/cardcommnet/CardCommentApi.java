package cz.cas.lib.indihumind.cardcommnet;

import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.util.ResponseContainer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;

import static cz.cas.lib.indihumind.util.ResponseContainer.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/card-comment")
@RolesAllowed(Roles.USER)
public class CardCommentApi {

    private CardCommentService service;

    @ApiOperation(value = "Find Comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardComment.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public CardComment find(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Create Comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardComment.class),
            @ApiResponse(code = 400, message = "Validation has failed."),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID"),
    })
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public CardComment create(@ApiParam(value = "Create DTO", required = true) @Valid @RequestBody CardCommentCreateDto dto) {
        return service.create(dto);
    }

    @ApiOperation(value = "Update Comment")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = CardComment.class),
            @ApiResponse(code = 400, message = "Validation has failed."),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID"),
            @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    })
    @PutMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public CardComment updateText(@ApiParam(value = "Update DTO", required = true) @Valid @RequestBody CardCommentUpdateDto dto) {
        return service.updateText(dto);
    }

    @ApiOperation(value = "Remove Comment")
    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }


    @ApiOperation(value = "Retrieve all query entities of user.", notes = "For <b>DEBUG<b> purposes. For production query a particular card.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CardComment.class, responseContainer = LIST)})
    @GetMapping(value = "/debug/find-for-card/{id}", produces = APPLICATION_JSON_VALUE)
    public Collection<CardComment> findAllOfCard(@ApiParam(value = "Id of the card", required = true) @PathVariable("id") String id) {
        return service.findByCard(id);
    }


    @Inject
    public void setService(CardCommentService service) {
        this.service = service;
    }
}
