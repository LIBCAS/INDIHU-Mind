package cz.cas.lib.indihumind.document;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Table(name = "vzb_attachment_external")
@DiscriminatorValue("EXTERNAL")
@Entity
public class ExternalAttachmentFile extends AttachmentFile {

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private AttachmentFileProviderType providerType;

    /**
     * id of the file assigned by the provider
     */
    @NotNull
    private String providerId;

    @NotNull
    private String link;

}
