package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.codecs.Content;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public @Data class Dashboard extends BaseModel {

    @NotEmpty
    String name;

    @NotEmpty
    String description;

    ObjectId parentId;

    int level;
    @BsonProperty("children")
    List<Dashboard> children = new ArrayList<>();

    List<String> readACL = new ArrayList<>();

    List<String> writeACL = new ArrayList<>();

    List<Content> content = new ArrayList<>();

    @Override
    public Dashboard clone() throws CloneNotSupportedException {
        Dashboard clone = (Dashboard) super.clone();
        clone.setId(this.getId());
        clone.setUpdatedAt(this.getUpdatedAt());
        clone.setParentId(this.parentId);
        return clone;
    }
    public void addChild(Dashboard dashboard) {
        children.add(dashboard);
    }
}
