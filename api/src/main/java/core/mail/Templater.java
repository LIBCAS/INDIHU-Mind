package core.mail;

import core.Changed;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.springframework.mail.MailPreparationException;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Changed("velocity replaced with freemarker")
public class Templater {
    private final Configuration freeMarkerConfig;

    @Inject
    public Templater(Configuration freeMarkerConfig) {
        this.freeMarkerConfig = freeMarkerConfig;
        this.freeMarkerConfig.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/");
    }

    /**
     * Transform template into string of HTML
     *
     * @param templateName path to template
     * @param beans        fields to fill into template
     * @return generated HTML string
     */
    public String transform(String templateName, Map<String, Object> beans) {
        try {
            return FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfig.getTemplate(templateName, StandardCharsets.UTF_8.name()), beans);
        } catch (IOException | TemplateException e) {
            throw new MailPreparationException(e);
        }
    }
}
