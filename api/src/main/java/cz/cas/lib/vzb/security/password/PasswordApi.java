package cz.cas.lib.vzb.security.password;

import cz.cas.lib.vzb.security.user.UserService;
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
    public void resetPassword(@RequestParam("email") String email) {
        userService.resetPassword(email);
    }

    @ApiOperation(value = "Set new password if token is valid")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 403, message = "Token expired or already used"),
            @ApiResponse(code = 404, message = "Token not found")
    })
    @PutMapping(value = "/new/{tokenId}")
    public void setNewPassword(@PathVariable("tokenId") String tokenId,
                               @RequestParam("newPassword") String newPassword) {
        userService.setNewPassword(tokenId, newPassword);
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
