package cz.cas.lib.vzb.api;

import core.index.dto.Params;
import core.index.dto.Result;
import core.store.Transactional;
import cz.cas.lib.vzb.dto.BulkFlagSetDto;
import cz.cas.lib.vzb.security.user.Roles;
import cz.cas.lib.vzb.security.user.User;
import cz.cas.lib.vzb.security.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import static cz.cas.lib.vzb.util.ResponseContainer.LIST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequestMapping("/api/admin")
@RestController
@Transactional
@RolesAllowed(Roles.ADMIN)
public class AdminApi {

    private UserService userService;

    @ApiOperation(value = "Register user with given email")
    @ApiResponse(code = 409, message = "This email address is already used")
    @PostMapping(value = "/user/{email}/register")
    public void register(@ApiParam(value = "email") @PathVariable String email) {
        userService.register(email, null);
    }


    @ApiOperation(value = "Register user with given email and password", notes = "For manual use only so far.")
    @ApiResponse(code = 409, message = "This email address is already used")
    @PostMapping(value = "/user/{email}/register-password")
    public void registerWithPassword(@ApiParam(value = "email") @PathVariable String email,
                                     @ApiParam(value = "password") @RequestParam String password) {
        userService.register(email, password);
    }

    @ApiOperation(value = "Gets all USER (not ADMIN) instances that respect the selected parameters")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = User.class, responseContainer = LIST)})
    @PostMapping(path = "/users", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Result<User> list(@ApiParam(value = "Parameters to comply with", required = true) @RequestBody Params params) {
        return userService.findAll(params);
    }

    @ApiOperation(value = "Operation to set allowed status of users in bulk")
    @PostMapping(path = "/set-allowance", consumes = APPLICATION_JSON_VALUE)
    public void setAllowedStatus(@ApiParam(value = "DTO with user IDs and allowance value", required = true) @Valid @RequestBody BulkFlagSetDto dto) {
        userService.setAllowedStatus(dto);
    }

    @RolesAllowed({})
    @ApiOperation(value = "Send dummy emails to confirm SMTP server is configured and see what emails are send to users",
            notes = "For <b>DEBUG</b> purposes, no login needed")
    @GetMapping(value = "/debug/test-emails")
    public void sendTestEmails(@RequestParam("email") String email) {
        userService.sendTestEmails(email);
    }


    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
