package models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {
    private String groupId;
    private String groupName ;
    private List<User> groupUsers;

    public Group( String groupName, List<User> groupUsers) {
        this.groupId = UUID.randomUUID().toString();
        this.groupName = groupName;
        this.groupUsers = new ArrayList<>();
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<User> getGroupUsers() {
        return groupUsers;
    }
}
