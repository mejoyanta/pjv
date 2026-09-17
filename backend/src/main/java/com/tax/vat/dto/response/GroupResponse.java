package com.tax.vat.dto.response;

import com.tax.vat.entity.Group;

import java.time.LocalDateTime;

public class GroupResponse {

    private Long id;
    private String name;
    private String slug;
    private Long parentGroupId;
    private String parentGroupName;
    private String motherAdmin;
    private String admin;
    private String description;
    private Boolean isAdmin;
    private Long usersCount;
    private LocalDateTime createdAt;

    public static GroupResponse fromEntity(Group group) {
        if (group == null) return null;
        GroupResponse res = new GroupResponse();
        res.id = group.getId();
        res.name = group.getName();
        res.slug = group.getSlug();
        if (group.getParent() != null) {
            res.parentGroupId = group.getParent().getId();
            res.parentGroupName = group.getParent().getName();

            if (group.getParent().getParent() != null) {
                res.motherAdmin = group.getParent().getParent().getName();
                res.admin = group.getParent().getName();
            } else {
                res.motherAdmin = group.getParent().getName();
                res.admin = "—";
            }
        } else {
            res.motherAdmin = "—";
            res.admin = "Main Group";
        }
        res.description = group.getDescription();
        res.isAdmin = group.getIsAdmin();
        res.createdAt = group.getCreatedAt();
        return res;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSlug() { return slug; }
    public Long getParentGroupId() { return parentGroupId; }
    public String getParentGroupName() { return parentGroupName; }
    public String getMotherAdmin() { return motherAdmin; }
    public String getAdmin() { return admin; }
    public String getDescription() { return description; }
    public Boolean getIsAdmin() { return isAdmin; }
    public Long getUsersCount() { return usersCount; }
    public void setUsersCount(Long usersCount) { this.usersCount = usersCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
