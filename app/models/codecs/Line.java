package models.codecs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.Collections;
import java.util.List;

@BsonDiscriminator(key = "type", value="LINE")
@AllArgsConstructor
@NoArgsConstructor
public @lombok.Data class Line extends Content {
    private List<Data> data= Collections.emptyList();

    public Type getType() {
        return Type.LINE;
    }
}
