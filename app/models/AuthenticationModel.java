package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@NoArgsConstructor
@AllArgsConstructor
public @Data
class AuthenticationModel {
    @NotEmpty
    String username;

    @NotEmpty
    String password;
}
