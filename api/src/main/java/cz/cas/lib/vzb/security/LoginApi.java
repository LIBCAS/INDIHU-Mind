package cz.cas.lib.vzb.security;

import io.swagger.annotations.ApiImplicitParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginApi {
    @ApiImplicitParam(name = "Authorization", value = "Enter Basic auth header value", required = true, dataType = "string", paramType = "header")
    @RequestMapping(method = RequestMethod.POST)
    public void dummy() {

    }
}
