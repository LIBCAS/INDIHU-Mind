package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.dto.validation.Uuid;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCardDto {

    @NotBlank
    @ApiModelProperty(value = "Name of the card", required = true, example = "Test Name for Card")
    private String name;

    @Size(max = 2000)
    @ApiModelProperty(value = "Note of the card, max size is 2000 characters.", example = "Text of note.")
    private String note;

    @ApiModelProperty(value = "IDs of categories for card.")
    private List<@Uuid String> categories = new ArrayList<>();

    @ApiModelProperty(value = "IDs of labels for card.")
    private List<@Uuid String> labels = new ArrayList<>();

    // TODO: Add to CardApiTest
    @ApiModelProperty(value = "IDs of records for card.")
    private List<@Uuid String> records = new ArrayList<>();

    @ApiModelProperty(value = "IDs of cards referenced from card of this update. (CardOfThisUpdate -> OtherCards)")
    private List<@Uuid String> linkedCards = new ArrayList<>();

    // TODO: Add to CardApiTest
    @ApiModelProperty(value = "IDs of cards referencing card of this update. (LinkingCard -> CardOfThisUpdate)")
    private List<@Uuid String> linkingCards = new ArrayList<>();

    @ApiModelProperty(value = "IDs of documents")
    private List<@Uuid String> files = new ArrayList<>();

}
