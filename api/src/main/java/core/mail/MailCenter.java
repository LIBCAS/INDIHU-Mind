package core.mail;

import core.Changed;
import core.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Central mailing component responsible for building up and sending mail messages from templates.
 */
@ConditionalOnProperty(prefix = "mail", name = "excluded", havingValue = "false", matchIfMissing = true)
@Slf4j
@Component
@Changed("bpm not supported, velocity replaced with freemarker")
public class MailCenter {

    /**
     * Flag whether Mailing enabled in application properties.
     */
    private Boolean enabled;
    /**
     * Flag whether log debug texts.
     */
    private Boolean debug;

    private AsyncMailSender sender;
    private Templater templater;

    private String senderEmail;
    private String senderName;
    private String appLogo;
    private String appName;
    private String appUrl;

    private Map<String, Object> generalArguments() {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("appLogo", appLogo);
        arguments.put("appName", appName);
        arguments.put("appUrl", appUrl);
        arguments.put("senderEmail", senderEmail);
        return arguments;
    }

    /**
     * Creates message helper.
     *
     * The result is used during message transformation and sending.
     *
     * @param emailTo       Recipient email
     * @param subject       Email subject
     * @param hasAttachment Will the email have attachment
     * @return Prepared message helper
     */
    public MimeMessageHelper generalMessage(String emailTo, @Nullable String subject, boolean hasAttachment) throws MessagingException {
        MimeMessage message = sender.create();

        // use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachment);

        if (emailTo != null) {
            helper.setTo(emailTo);
        }

        if (subject != null) {
            helper.setSubject(subject);
        }

        try {
            helper.setFrom(senderEmail, senderName);
        } catch (UnsupportedEncodingException ex) {
            log.warn("Can not set email 'from' encoding, fallbacking.");
            helper.setFrom(senderEmail);
        }

        return helper;
    }

    /**
     * Transforms input template with provided data and prepared message and sends it.
     *
     * @param templateName FreeMaker template name (or rather path), e.g. mail/myEmail.ftl
     * @param arguments    Data
     * @param helper       Previously created message helper
     */
    public void transformAndSend(String templateName, Map<String, Object> arguments, MimeMessageHelper helper) throws MessagingException {
        Map<String, Object> beans = mergeArgumentMaps(arguments, generalArguments());

        String text = templater.transform(templateName, beans);
        helper.setText(text, true);

        if (debug) {
            log.info(text);
        }

        if (!enabled) {
            log.warn("Mail message was silently consumed because mail system is disabled.");
            return;
        }

        MimeMessage message = helper.getMimeMessage();

        if (message.getAllRecipients() != null && message.getAllRecipients().length > 0) {
            sender.sendAsync(message);
        } else {
            log.warn("Mail message was silently consumed because there were no recipients.");
        }
    }

    public void sendNotification(String email, String title, String description) {
        try {
            MimeMessageHelper message = generalMessage(email, appName + ": " + title, false);

            Map<String, Object> params = generalArguments();
            params.put("content", description);

            String templatePath = "templates/notification.ftl";
            transformAndSend(templatePath, params, message);

        } catch (MessagingException ex) {
            throw new GeneralException(ex);
        }
    }

    private Map<String, Object> mergeArgumentMaps(Map<String, Object> map1, Map<String, Object> map2) {
        return Stream.of(map1, map2)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Inject
    public void setEnabled(@Value("${mail.enabled:false}") Boolean enabled) {
        this.enabled = enabled;
    }

    @Inject
    public void setDebug(@Value("${mail.debug:false}") Boolean debug) {
        this.debug = debug;
    }

    @Inject
    public void setSenderEmail(@Value("${mail.sender.email}") String senderEmail) {
        this.senderEmail = senderEmail;
    }

    @Inject
    public void setSenderName(@Value("${mail.sender.name}") String senderName) {
        this.senderName = senderName;
    }

    @Inject
    public void setAppLogo(@Value("${mail.app.logo}") String appLogo) {
        this.appLogo = appLogo;
    }

    @Inject
    public void setAppName(@Value("${mail.app.name}") String appName) {
        this.appName = appName;
    }

    @Inject
    public void setAppUrl(@Value("${mail.app.url}") String appUrl) {
        this.appUrl = appUrl;
    }

    @Inject
    public void setSender(AsyncMailSender sender) {
        this.sender = sender;
    }

    @Inject
    public void setTemplater(Templater templater) {
        this.templater = templater;
    }

}