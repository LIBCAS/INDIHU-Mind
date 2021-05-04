package cz.cas.lib.indihumind;

import core.store.Transactional;
import cz.cas.lib.indihumind.citation.Citation;
import cz.cas.lib.indihumind.citationtemplate.GeneratePdfDto;
import cz.cas.lib.indihumind.citationtemplate.ReferenceTemplate;
import cz.cas.lib.indihumind.citationtemplate.ReferenceTemplateService;
import cz.cas.lib.indihumind.init.builders.UserBuilder;
import cz.cas.lib.indihumind.init.providers.CitationTestData;
import cz.cas.lib.indihumind.init.providers.RefTemplateTestData;
import cz.cas.lib.indihumind.security.user.User;
import cz.cas.lib.indihumind.security.user.UserService;
import helper.auth.WithMockCustomUser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
public class CitationGenerationTest {

    private static Path TEST_CITATIONS_DIRECTORY;

    @Inject private UserService userService;
    @Inject private CitationTestData citationProvider;
    @Inject private RefTemplateTestData templateProvider;
    @Inject private ReferenceTemplateService service;

    private final User user = UserBuilder.builder().id("user").password("password").email("mail").allowed(true).build();

    @Before
    public void before() {
        userService.create(user);
    }

    @BeforeClass  // create directory for test files
    public static void beforeClass() throws IOException {
        Properties props = new Properties();
        props.load(ClassLoader.getSystemResourceAsStream("application.properties"));
        TEST_CITATIONS_DIRECTORY = Paths.get(props.getProperty("vzb.citation.file.path"));

        if (!Files.isDirectory(TEST_CITATIONS_DIRECTORY) && Files.exists(TEST_CITATIONS_DIRECTORY)) {
            throw new RuntimeException("There already exist a file with name meant for test files directory!");
        } else if (!Files.isDirectory(TEST_CITATIONS_DIRECTORY)) {
            Files.createDirectory(TEST_CITATIONS_DIRECTORY);
        }
    }

    @WithMockCustomUser
    @Test
    public void generatePdfCitations_VerifyFileManually() throws Exception {
        ReferenceTemplate rt1 = templateProvider.locationWithAuthor(user);
        ReferenceTemplate rt2 = templateProvider.euroScience(user);
        ReferenceTemplate rt3 = templateProvider.authors(user);
        ReferenceTemplate rt4 = templateProvider.recordNameWithAuthors(user);
        ReferenceTemplate rt5 = templateProvider.allFieldsTemplate(user);

        Citation c1 = citationProvider.recordMovieMLissUsa(user);
        Citation c2 = citationProvider.recordChabonMichaelSummerland(user);
        Citation c3 = citationProvider.recordIsbn(user);
        Citation c4 = citationProvider.recordSurveyCatalogingPractices(user);
        Citation c5 = citationProvider.recordDanielSmith(user);
        Citation c6 = citationProvider.recordAntiqueWorld(user);
        Citation c7 = citationProvider.recordWithHumanPrimaryAuthor(user);
        Citation c8 = citationProvider.recordWithCompanyPrimaryAuthor(user);
        Citation c9 = citationProvider.briefRecord1(user);

        List<String> allTemplates = List.of(rt1.getId(), rt2.getId(), rt3.getId(), rt4.getId(), rt5.getId());
        List<String> allCitations = List.of(c1.getId(), c2.getId(), c3.getId(), c4.getId(), c5.getId(), c6.getId(), c7.getId(), c8.getId(), c9.getId());

        // use all templates for pdf generation
        for (String templateId : allTemplates) {
            GeneratePdfDto dto = new GeneratePdfDto();
            dto.setTemplateId(templateId);
            dto.setIds(allCitations);

            // generate pdf
            ResponseEntity<InputStreamResource> result = service.generateWithCitations(dto);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
            assertThat(result.getHeaders().getContentDisposition().getFilename()).isNotBlank();

            assertThat(result.getBody()).isNotNull();
            // save pdf to file
            try (InputStream stream = result.getBody().getInputStream()) {
                assertThat(stream).isNotNull();
                Path path = Paths.get(TEST_CITATIONS_DIRECTORY.toString(), dto.getTemplateId() + ".pdf");
                Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }

}
