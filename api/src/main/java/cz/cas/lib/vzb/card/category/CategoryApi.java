package cz.cas.lib.vzb.card.category;

import cz.cas.lib.vzb.security.delegate.UserDelegate;
import cz.cas.lib.vzb.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static cz.cas.lib.vzb.util.ResponseContainer.LIST;

@Slf4j
@RestController
@RequestMapping("/api/category")
@RolesAllowed(Roles.USER)
public class CategoryApi {

    private CategoryService service;
    private UserDelegate userDelegate;

    @ApiResponse(code = 409, message = "Name of new entity already exists. Name uniqueness is enforced.")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Category save(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id,
                         @ApiParam(value = "Single instance", required = true) @RequestBody Category category) {
        return service.save(id, category);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Retrieve all entities of user.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CategoryDto.class, responseContainer = LIST)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Collection<CategoryDto> findAllOfUser() {
        return service.findAllOfUser(userDelegate.getId());
    }


    @Inject
    public void setService(CategoryService service) {
        this.service = service;
    }

    @Inject
    public void setUserDelegate(UserDelegate userDelegate) {
        this.userDelegate = userDelegate;
    }

}
