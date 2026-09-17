package com.tax.vat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class GroupCreateRequest {

    @NotBlank(message = "Group Name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    private Long parentGroupId;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    private Boolean isAdmin = false;

    public GroupCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Long parentGroupId) {
        this.parentGroupId = parentGroupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
