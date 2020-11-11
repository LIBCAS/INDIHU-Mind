package cz.cas.lib.vzb.card.comment;

import cz.cas.lib.vzb.dto.validation.Uuid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @see CardComment
 * @see CardCommentCreateDto
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardCommentUpdateDto {

    @Uuid
    @NotNull
    private String id;

    @NotBlank private String text;

}
