package cz.cas.lib.vzb.card.dto;

import cz.cas.lib.vzb.card.attribute.Attribute;
import cz.cas.lib.vzb.dto.validation.Uuid;
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
    @NotNull
    private String id;
    @NotNull
    private String name;
    private String note;
    private String rawNote;
    private List<String> categories = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    // TODO: Add Records to CardApiTest
    private List<String> records = new ArrayList<>();
    private List<String> linkedCards = new ArrayList<>();
    private List<Attribute> attributes = new ArrayList<>();
    private List<@Uuid String> files = new ArrayList<>();
}
