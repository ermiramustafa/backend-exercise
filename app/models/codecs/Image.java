package models.codecs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "type", value = "IMAGE")
@NoArgsConstructor
@AllArgsConstructor
public @Data class Image extends Content {
    private String url;

    public Type getType(){
        return Type.IMAGE;
    }
}

