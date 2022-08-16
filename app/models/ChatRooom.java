package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
//@EqualsAndHashCode(of = "id")
public class ChatRooom extends BaseModel {

    private String name;
    private List<String> readACL = new ArrayList<>();
    private List<String> writeACL = new ArrayList<>();
}