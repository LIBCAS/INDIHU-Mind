package cz.cas.lib.vzb;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cas.lib.vzb.card.attachment.AttachmentFile;
import cz.cas.lib.vzb.card.attachment.AttachmentFileProviderType;
import cz.cas.lib.vzb.card.attachment.ExternalAttachmentFile;
import cz.cas.lib.vzb.card.attachment.LocalAttachmentFile;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

public class InheritanceTest {

    @Test
    public void attachmentTypeMapping() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        LocalAttachmentFile local = new LocalAttachmentFile();
        String json = mapper.writeValueAsString(local);
        AttachmentFile deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized, instanceOf(LocalAttachmentFile.class));

        ExternalAttachmentFile gdrive = new ExternalAttachmentFile();
        gdrive.setProviderType(AttachmentFileProviderType.GOOGLE_DRIVE);
        json = mapper.writeValueAsString(gdrive);
        deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized, instanceOf(ExternalAttachmentFile.class));

        ExternalAttachmentFile dropbox = new ExternalAttachmentFile();
        dropbox.setProviderType(AttachmentFileProviderType.DROPBOX);
        json = mapper.writeValueAsString(dropbox);
        deserialized = mapper.readValue(json, AttachmentFile.class);
        assertThat(deserialized, instanceOf(ExternalAttachmentFile.class));
    }
}
