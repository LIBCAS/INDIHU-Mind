package cz.cas.lib.vzb.card.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.dto.validation.Uuid;
import cz.cas.lib.vzb.reference.marc.Record;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCardDto {
    @Uuid
    private String id;
    @NotNull
    private String name;
    @Size(max = 2000)
    private String note;
    private List<String> categories = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    // TODO: Add Records to CardApiTest
    private List<String> records = new ArrayList<>();
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private List<String> linkedCards = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
//    private List<UploadAttachmentFileDto> files = new ArrayList<>();
}
