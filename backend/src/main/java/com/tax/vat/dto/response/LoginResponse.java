package com.tax.vat.dto.response;

import java.util.List;

public class LoginResponse {

    private String token;
    private Long id;
    private String name;
    private String username;
    private String email;
    private Long companyId;
    private String companyName;
    private String groupName;
    private Boolean isAdmin;
    private List<String> permissions;

    public LoginResponse() {
    }

    public LoginResponse(String token, Long id, String name, String username, String email, Long companyId, String companyName, String groupName, Boolean isAdmin, List<String> permissions) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.companyId = companyId;
        this.companyName = companyName;
        this.groupName = groupName;
        this.isAdmin = isAdmin;
        this.permissions = permissions;
    }

    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getGroupName() { return groupName; }
    public Boolean getIsAdmin() { return isAdmin; }
    public List<String> getPermissions() { return permissions; }
}
