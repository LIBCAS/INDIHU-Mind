package cz.cas.lib.indihumind.cardcategory;

import cz.cas.lib.indihumind.security.delegate.UserDelegate;
import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.util.ResponseContainer;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static cz.cas.lib.indihumind.util.ResponseContainer.*;

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
                         @ApiParam(value = "Single instance", required = true) @Valid @RequestBody Category category) {
        return service.save(id, category);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Category get(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        return service.find(id);
    }

    @ApiOperation(value = "Deletes category and all its subcategories. Asynchronously reindexes cards that were under deleted categories.")
    @DeleteMapping(value = "/{id}")
    public void delete(@ApiParam(value = "Id of the instance", required = true) @PathVariable("id") String id) {
        service.delete(id);
    }

    @ApiOperation(value = "Retrieve all entities of user [sorted by ordinal number].")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = CategoryDto.class, responseContainer = LIST)})
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CategoryDto> findAllOfUser() {
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
