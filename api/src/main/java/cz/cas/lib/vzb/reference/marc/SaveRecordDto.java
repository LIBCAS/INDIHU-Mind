package cz.cas.lib.vzb.reference.marc;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import cz.cas.lib.vzb.card.dto.CardSimpleConverter;
import cz.cas.lib.vzb.dto.validation.Uuid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;


/**
 * DTO for creating, updating Records via API
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SaveRecordDto {
    @Uuid
    private String id;
    @NotNull
    private String name;
    @Size(max = 24)
    private String leader = "";
    private List<Datafield> dataFields = new ArrayList<>();
    @JsonSerialize(contentConverter = CardSimpleConverter.class)
    private List<String> linkedCards = new ArrayList<>();
}
