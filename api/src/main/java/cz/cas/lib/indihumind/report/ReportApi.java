package cz.cas.lib.indihumind.report;

import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.security.user.Roles;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/report")
@RolesAllowed(Roles.USER)
public class ReportApi {

    private ReportService service;

    @ApiOperation(value = "Create a report for cards [PDF/CSV]")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = InputStreamResource.class),
            @ApiResponse(code = 403, message = "Entity not owned by logged in user"),
            @ApiResponse(code = 404, message = "Entity not found for given ID")
    })
    @PostMapping(value = "/card", consumes = APPLICATION_JSON_VALUE) // `produces` is not necessary because response entity has set content type
    public  ResponseEntity<InputStreamResource>  createCardReport(
            @ApiParam(value = "DTO with card IDs", required = true) @Valid @RequestBody ReportService.ReportDto reportDto) {
        return service.createCardReport(reportDto);
    }

    @Inject
    public void setService(ReportService service) {
        this.service = service;
    }

}
