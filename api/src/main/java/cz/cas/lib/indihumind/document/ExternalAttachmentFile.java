package cz.cas.lib.indihumind.document;

import cz.cas.lib.indihumind.document.view.DocumentRef;
import cz.cas.lib.indihumind.document.view.DocumentRefExternal;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Table(name = "vzb_attachment_external")
@DiscriminatorValue("EXTERNAL")
@Entity
public class ExternalAttachmentFile extends AttachmentFile {

    /**
     * id of the file assigned by the provider
     */
    @NotNull
    private String providerId;

    @NotNull
    private String link;

    @Override
    public DocumentRef toReference() {
        DocumentRefExternal ref = new DocumentRefExternal();
        ref.setId(id);
        return ref;
    }
}
