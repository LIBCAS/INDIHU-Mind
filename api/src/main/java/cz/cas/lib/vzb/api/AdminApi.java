package cz.cas.lib.vzb.api;

import core.index.dto.Filter;
import core.index.dto.FilterOperation;
import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.user.IndexedUser;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import static core.util.Utils.addPrefilter;
import static core.util.Utils.asList;

@RequestMapping("/api/admin")
@RestController
@Transactional
@RolesAllowed(Roles.ADMIN)
public class AdminApi {

    private UserService userService;

    @ApiOperation(value = "Register user with given email")
    @ApiResponse(code = 409, message = "This email address is already used")
    @RequestMapping(method = RequestMethod.POST, value = "/user/{email}/register")
    public void register(
            @ApiParam(value = "email") @PathVariable String email
    ) {
        userService.register(email, null);
    }


    @ApiOperation(value = "For manual use only, will be refactored. Register user with given email and password")
    @ApiResponse(code = 409, message = "This email address is already used")
    @RequestMapping(method = RequestMethod.POST, value = "/user/{email}/register_password")
    public void registerWithPassword(
            @ApiParam(value = "email") @PathVariable String email,
            @ApiParam(value = "password") @RequestParam String password
    ) {
        userService.register(email, password);
    }

    @ApiOperation(value = "Gets all USER (not ADMIN) instances that respect the selected parameters")
    @RequestMapping(method = RequestMethod.GET, path = "/users")
    public Result<User> listUsers(@ApiParam(value = "Parameters to comply with", required = true)
                                  @ModelAttribute Params params) {
        Filter rolesContainsAdmin = new Filter(IndexedUser.ROLES, FilterOperation.CONTAINS, Roles.ADMIN, null);
        addPrefilter(params, new Filter(null, FilterOperation.NEGATE, null, asList(rolesContainsAdmin)));
        return userService.getDelegate().findAll(params);
    }

    @ApiOperation(value = "Operation to set allowed status of users")
    @RequestMapping(method = RequestMethod.POST, path = "/set_allowance")
    public void setAllowedStatus(
            @ApiParam(value = "dto with ids of users and allowance value", required = true) @Valid @RequestBody BulkFlagSetDto dto) {
        userService.setAllowedStatus(dto);
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
