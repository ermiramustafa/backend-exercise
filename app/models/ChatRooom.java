package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public @Data class ChatRooom extends BaseModel{

    private String name;
    private List<ObjectId> readACL;
    private List<ObjectId> writeACL;
}