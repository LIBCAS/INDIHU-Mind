package cz.cas.lib.vzb.init.providers;

import cz.cas.lib.vzb.attachment.*;
import cz.cas.lib.vzb.init.builders.ExternalAttachmentBuilder;
import cz.cas.lib.vzb.init.builders.LocalAttachmentBuilder;
import cz.cas.lib.vzb.init.builders.UrlAttachmentBuilder;
import cz.cas.lib.vzb.security.user.User;
import lombok.Getter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.inject.Inject;


@Getter
@Component
public class AttachmentFileTestData implements TestDataRemovable {

    @Inject private AttachmentFileStore attachmentStore;


    @Override
    public void wipeAllDatabaseData() {
        attachmentStore.clearTable();
    }

    public AttachmentFile externalFile1(User owner) {
        ExternalAttachmentFile ef = ExternalAttachmentBuilder.builder()
                .owner(owner)
                .name("druhe GOT meme")
                .provider(AttachmentFileProviderType.DROPBOX)
                .providerId("gotMeme")
                .type("webp")
                .link("https://img-9gag-fun.9cache.com/photo/aGZ6B30_700bwp.webp").build();
        return attachmentStore.save(ef);
    }

    public AttachmentFile externalFile2(User owner) {
        ExternalAttachmentFile extFile = ExternalAttachmentBuilder.builder()
                .name("GOT memecko")
                .owner(owner)
                .provider(AttachmentFileProviderType.GOOGLE_DRIVE)
                .providerId("got mem")
                .type("webp")
                .link("https://img-9gag-fun.9cache.com/photo/aGZ6B30_700bwp.webp").build();
        return attachmentStore.save(extFile);

    }

    public AttachmentFile localFile1(User owner) {
        LocalAttachmentFile locFile = LocalAttachmentBuilder.builder()
                .name("LokalNyfAjl")
                .owner(owner)
                .contentType("image/png")
                .type("png").build();
        return attachmentStore.save(locFile);
    }

    public AttachmentFile urlFile1(User owner) {
        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder()
                .name("NKP Logo")
                .location(UrlAttachmentFile.UrlDocumentLocation.WEB)
                .owner(owner)
                .size(29647L)
                .link("https://kramerius5.nkp.cz/logo.png")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .type("png").build();
        return attachmentStore.save(urlFile);
    }

    public AttachmentFile adminLocalFile(User owner) {
        LocalAttachmentFile locFile = LocalAttachmentBuilder.builder()
                .name("adminLocalFile")
                .owner(owner)
                .contentType("image/png")
                .type("png").build();
        return attachmentStore.save(locFile);
    }

    public AttachmentFile adminUrlFile(User owner) {
        UrlAttachmentFile urlFile = UrlAttachmentBuilder.builder()
                .name("adminUrlFile")
                .location(UrlAttachmentFile.UrlDocumentLocation.SERVER)
                .owner(owner)
                .size(29647L)
                .link("https://kramerius5.nkp.cz/logo.png")
                .contentType(MediaType.IMAGE_PNG_VALUE)
                .type("png").build();
        return attachmentStore.save(urlFile);
    }
}