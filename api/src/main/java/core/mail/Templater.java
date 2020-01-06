package core.mail;

import core.Changed;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
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

    public String transform(String template, Map<String, Object> beans) throws TemplateException, IOException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(
                freeMarkerConfig.getTemplate(template, StandardCharsets.UTF_8.name()), beans);
    }
}
