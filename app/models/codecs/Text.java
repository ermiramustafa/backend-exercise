package models.codecs;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public @lombok.Data class Text extends Content {
    @NotEmpty
    private String text;

    public Type getType() {
        return Type.TEXT;
    }
}