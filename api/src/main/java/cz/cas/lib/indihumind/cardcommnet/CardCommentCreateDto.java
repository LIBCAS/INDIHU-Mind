package cz.cas.lib.indihumind.cardcommnet;

import cz.cas.lib.indihumind.validation.Uuid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @see CardComment
 * @see CardCommentUpdateDto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCommentCreateDto {

    @Uuid
    @NotNull
    private String cardId;

    @NotBlank private String text;

}
