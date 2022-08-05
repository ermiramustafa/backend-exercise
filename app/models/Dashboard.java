package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.codecs.Content;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public @Data class Dashboard extends BaseModel{

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private ObjectId parentId;

    private List<String> readACL = new ArrayList<>();

    private List<String> writeACL = new ArrayList<>();

    List<Content> content=new ArrayList<>();

}
