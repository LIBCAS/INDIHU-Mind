package cz.cas.lib.vzb.card.attachment;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Table(name = "vzb_external_file")
public class ExternalAttachmentFile extends AttachmentFile {
    /**
     * id of the file assigned by the provider
     */
    private String providerId;
    private String link;
    @Enumerated(value = EnumType.STRING)
    private AttachmentFileProviderType providerType;
}
