package cz.cas.lib.vzb.search.query;

import cz.cas.lib.vzb.search.validation.IndexedClassNameValidation;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QueryDto {

    @NotBlank
    @ApiModelProperty(required = true, dataType = "String", value = "Name of indexed class.", example = "IndexedCard")
    @IndexedClassNameValidation
    private String indexedClass;

    @NotNull
    @ApiModelProperty(required = true, value = "Parameters for query")
    @Valid
    private QueryParams params;

}
