package models.codecs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
public @NotEmpty class Data {
    private String category;
    private int value;
}
