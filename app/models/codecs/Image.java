package models.codecs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import javax.validation.constraints.NotEmpty;

@BsonDiscriminator(key = "type", value = "IMAGE")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public @Data class Image extends Content {

    @NotEmpty
    private String url;

    public Type getType(){
        return Type.IMAGE;
    }
}

