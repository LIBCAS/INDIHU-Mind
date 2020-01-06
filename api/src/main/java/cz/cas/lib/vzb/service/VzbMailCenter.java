package cz.cas.lib.vzb.service;

import core.exception.GeneralException;
import core.mail.MailCenter;
import core.util.Utils;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
public class VzbMailCenter extends MailCenter {

    public void sendUserCreatedNotification(String email, String password) {
        sendNotificationInternal(email, "Byl Vám vytvořen účet", null, password, Instant.now(), "mail/userCreated.ftl");
    }

    public void sendUserPasswordGenerationLink(String email, String pwdGenerationLink) {
        sendNotificationInternal(email, "Zadost o resetovani hesla", null, pwdGenerationLink, Instant.now(), "mail/passwordReset.ftl");
    }

    private void sendNotificationInternal(String email, String subject, String externalId, String result, Instant created, String templateName) {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd. MM. yyyy");

            MimeMessageHelper message = generalMessage(email, subject, false);

            Map<String, Object> params = generalArguments();
            params.put("externalId", externalId);
            params.put("result", result);
            params.put("createdDate", Utils.extractDate(created).format(dateFormatter));
            params.put("createdTime", Utils.extractTime(created).format(timeFormatter));

            transformAndSend(templateName, params, message);
        } catch (MessagingException | IOException | TemplateException ex) {
            throw new GeneralException(ex);
        }
    }
}
