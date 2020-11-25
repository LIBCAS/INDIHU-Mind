package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.dto.validation.MaxAllowedByteSize;
import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateCardDto {

    @Uuid
    @NotNull
    private String id;

    @NotBlank
    @ApiModelProperty(value = "Name of the card", required = true, example = "Test Name for Card")
    private String name;

    private List<Attribute> attributes = new ArrayList<>();

    @MaxAllowedByteSize(megaBytes = 10)
    @ApiModelProperty(value = "Note of the card, in JSON structure with images created by FE")
    private String note;

    @ApiModelProperty(value = "Raw note text without images")
    private String rawNote;

    @ApiModelProperty(value = "IDs of categories for card.")
    private List<@Uuid String> categories = new ArrayList<>();

    @ApiModelProperty(value = "IDs of labels for card.")
    private List<@Uuid String> labels = new ArrayList<>();

    // TODO: Add Records to CardApiTest
    @ApiModelProperty(value = "IDs of records for card.")
    private List<@Uuid String> records = new ArrayList<>();

    @ApiModelProperty(value = "IDs of cards referenced from card of this update. (CardOfThisUpdate -> OtherCards)")
    private List<@Uuid String> linkedCards = new ArrayList<>();

    @ApiModelProperty(value = "IDs of documents")
    private List<@Uuid String> files = new ArrayList<>();

}
