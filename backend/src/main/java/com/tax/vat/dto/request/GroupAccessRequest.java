package com.tax.vat.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class GroupAccessRequest {

    @NotNull(message = "Permissions list cannot be null")
    private List<String> permissions;

    public GroupAccessRequest() {
    }

    public GroupAccessRequest(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
