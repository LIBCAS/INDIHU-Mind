package cz.cas.lib.indihumind.security;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginApi {

    @ApiOperation(value = "Authentication endpoint. Format: Basic <token>",
            notes = "If endpoint returns <b>200 without bearer token</b> then it is highly possible that header does <b>not</b> contain 'Basic' prefix.")
    @ApiImplicitParam(name = "Authorization", value = "Basic <token>", required = true, dataType = "string", paramType = "header")
    @PostMapping
    public void dummy() {
    }

}
