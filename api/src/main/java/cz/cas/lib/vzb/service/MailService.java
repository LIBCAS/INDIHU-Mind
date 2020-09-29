package cz.cas.lib.vzb.service;

import core.exception.GeneralException;
import core.mail.MailCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.mail.MessagingException;
import java.util.Map;
import java.util.UUID;

import static core.util.Utils.asMap;

@Slf4j
@Service
public class MailService {

    private String serverAddress;
    private MailCenter mailCenter;

    public void sendDummyEmails(String email) {
        sendResetPasswordEmail(email, UUID.randomUUID().toString());
        sendUserCreatedEmail(email, "generated-password");
    }

    public void sendResetPasswordEmail(String email, String tokenId) {
        Map<String, Object> params = asMap("passwordResetUrl", serverAddress + "/reset-password?token=" + tokenId);
        String templateName = "mail/passwordReset.ftl";
        String emailSubject = "Obnova hesla";

        createEmail(email, templateName, emailSubject, params);
    }

    public void sendUserCreatedEmail(String email, String password) {
        Map<String, Object> params = asMap("password", password, "email", email);
        String templateName = "mail/userCreated.ftl";
        String emailSubject = "Vytvořen účet";

        createEmail(email, templateName, emailSubject, params);
    }

    /**
     * Creates and sends email
     *
     * @param email        address of receiver
     * @param templateName path to FreeMaker template e.g. mail/my-template.ftl
     * @param emailSubject subject of the email
     * @param fields       map of values to insert into email template
     */
    private void createEmail(String email, String templateName, String emailSubject, Map<String, Object> fields) {
        try {
            MimeMessageHelper message = mailCenter.generalMessage(email, emailSubject, false);

            log.info(String.format("Generating email: '%s'; Receiver: '%s'; Email subject:'%s'", templateName, email, emailSubject));

            mailCenter.transformAndSend(templateName, fields, message);
        } catch (MessagingException ex) {
            throw new GeneralException(ex);
        }
    }


    @Inject
    public void setMailCenter(MailCenter mailCenter) {
        this.mailCenter = mailCenter;
    }

    @Inject
    public void setServerAddress(@Value("${mail.app.url}") String serverAddress) {
        this.serverAddress = serverAddress;
    }

}
