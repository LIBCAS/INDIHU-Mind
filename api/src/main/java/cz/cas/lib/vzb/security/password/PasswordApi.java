package cz.cas.lib.vzb.security.password;

import core.store.Transactional;
import cz.cas.lib.vzb.security.user.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RequestMapping("/api/password")
@RestController
public class PasswordApi {

    private UserService userService;

    @Transactional
    @ApiOperation(value = "Send link for password-reset page to given mail, link is {serverURL}/resetPassword?token={token.id}")
    @RequestMapping(method = RequestMethod.POST, path = "/forgotten/token/{email}")
    public String sendPasswordResetToken(
            @ApiParam(value = "Email to which should be token sent", required = true) @PathVariable String email) {
        return userService.sendResetLinkToEmail(email);
    }

    @Transactional
    @ApiOperation(value = "Validate time on token and update password for user")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "Token has been already used / Token is expired"),
            @ApiResponse(code = 404, message = "Token with given ID was not found"),
            @ApiResponse(code = 200, message = "Token has been correctly used and password was changed")
    })
    @RequestMapping(method = RequestMethod.POST, path = "/forgotten/new/{tokenId}")
    public void updatePassword(
            @ApiParam(value = "ID of sent token", required = true) @PathVariable
                    String tokenId,
            @ApiParam(value = "New password that should be assigned to owner of token", required = true) @RequestParam
                    String password) {
        userService.validateTokenSetNewPassword(tokenId, password);
    }

    @Inject
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
