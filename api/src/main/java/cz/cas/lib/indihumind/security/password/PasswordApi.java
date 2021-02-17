package cz.cas.lib.indihumind.security.password;

import cz.cas.lib.indihumind.security.user.Roles;
import cz.cas.lib.indihumind.security.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.Collection;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RolesAllowed({})
@RequestMapping("/api/password")
@RestController
public class PasswordApi {

    private UserService userService;

    @ApiOperation(value = "Send email with token for setting a new password.", notes = "Sent URL in email is: <b>{serverURL}/reset-password?token={token.id}</b>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 404, message = "Email not found")
    })
    @PutMapping(value = "/reset")
    public void resetForgottenPassword(@RequestParam("email") String email) {
        userService.resetPassword(email);
    }

    @ApiOperation(value = "Set new password if token is valid")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Token expired or already used"),
            @ApiResponse(code = 404, message = "Token not found")
    })
    @PutMapping(value = "/new/{tokenId}")
    public void setNewPasswordWithToken(@PathVariable("tokenId") String tokenId,
                                        @RequestParam("newPassword") String newPassword) {
        userService.setNewPassword(tokenId, newPassword);
    }

    @RolesAllowed(Roles.USER)
    @ApiOperation(value = "Change password")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Validation failed"),
            @ApiResponse(code = 403, message = "User is not logged in"),
    })
    @PutMapping(value = "/change")
    public void changePassword(@RequestParam("old") String oldPassword, @RequestParam("new") String newPassword) {
        userService.changePassword(oldPassword, newPassword);
    }


    @ApiOperation(value = "Get all tokens in DB", notes = "<b>For debug purposes</b>")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Collection<PasswordToken> listAllTokens() {
        return userService.findAllTokens();
    }


    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

}
