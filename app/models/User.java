package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;


@NoArgsConstructor
@AllArgsConstructor
public @Data class User extends BaseModel {
    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    //role???
    //private String role;

}
