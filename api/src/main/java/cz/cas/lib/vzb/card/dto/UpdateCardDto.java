package cz.cas.lib.vzb.card.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCardDto {
    @NotNull
    private String name;
    @Size(max = 2000)
    private String note;
    private List<String> categories = new ArrayList<>();
    private List<String> labels = new ArrayList<>();
    // TODO: Add Records to CardApiTest
    private List<String> records = new ArrayList<>();
    private List<String> linkedCards = new ArrayList<>();
}
