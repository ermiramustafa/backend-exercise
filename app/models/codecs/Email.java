package models.codecs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import javax.validation.constraints.NotEmpty;

@BsonDiscriminator(key = "type", value="EMAIL")
@AllArgsConstructor
@NoArgsConstructor

public @Data class Email extends Content {

    @NotEmpty
    private String email;

    @NotEmpty
    private String subject;

    @NotEmpty
    private String text;

    public Type getType() {
        return Type.EMAIL;
    }

}
