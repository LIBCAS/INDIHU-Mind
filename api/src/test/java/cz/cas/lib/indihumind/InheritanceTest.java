package cz.cas.lib.indihumind;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cas.lib.indihumind.document.*;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InheritanceTest {

    @Test
    public void attachmentTypeMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        LocalAttachmentFile localFile = new LocalAttachmentFile();
        String json = mapper.writeValueAsString(localFile);
        AttachmentFile deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized).isInstanceOf(LocalAttachmentFile.class);

        ExternalAttachmentFile googleDriveFile = new ExternalAttachmentFile();
        googleDriveFile.setProviderType(AttachmentFileProviderType.GOOGLE_DRIVE);
        json = mapper.writeValueAsString(googleDriveFile);
        deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized).isInstanceOf(ExternalAttachmentFile.class);

        ExternalAttachmentFile dropboxFile = new ExternalAttachmentFile();
        dropboxFile.setProviderType(AttachmentFileProviderType.DROPBOX);
        json = mapper.writeValueAsString(dropboxFile);
        deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized).isInstanceOf(ExternalAttachmentFile.class);

        UrlAttachmentFile urlFile = new UrlAttachmentFile();
        json = mapper.writeValueAsString(urlFile);
        deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized).isInstanceOf(UrlAttachmentFile.class);

    }
}
