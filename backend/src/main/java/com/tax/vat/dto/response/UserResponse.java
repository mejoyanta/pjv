package com.tax.vat.dto.response;

import com.tax.vat.entity.User;
import com.tax.vat.enums.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private String username;
    private Long designationId;
    private String designationName;
    private Long departmentId;
    private String departmentName;
    private Long groupId;
    private String groupName;
    private Long companyId;
    private String companyName;
    private Long companyBranchId;
    private String companyBranchName;
    private String contact;
    private String nid;
    private String picture;
    private Boolean isAdmin;
    private Boolean isOnline;
    private UserStatus status;
    private LocalDate passwordChangedAt;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        UserResponse res = new UserResponse();
        res.id = user.getId();
        res.name = user.getName();
        res.email = user.getEmail();
        res.username = user.getUsername();
        if (user.getDesignation() != null) {
            res.designationId = user.getDesignation().getId();
            res.designationName = user.getDesignation().getName();
        }
        if (user.getDepartment() != null) {
            res.departmentId = user.getDepartment().getId();
            res.departmentName = user.getDepartment().getName();
        }
        if (user.getGroup() != null) {
            res.groupId = user.getGroup().getId();
            res.groupName = user.getGroup().getName();
        }
        if (user.getCompany() != null) {
            res.companyId = user.getCompany().getId();
            res.companyName = user.getCompany().getName();
        }
        if (user.getCompanyBranch() != null) {
            res.companyBranchId = user.getCompanyBranch().getId();
            res.companyBranchName = user.getCompanyBranch().getName();
        }
        res.contact = user.getContact();
        res.nid = user.getNid();
        res.picture = user.getPicture();
        res.isAdmin = user.getIsAdmin();
        res.isOnline = user.getIsOnline();
        res.status = user.getStatus();
        res.passwordChangedAt = user.getPasswordChangedAt();
        res.createdAt = user.getCreatedAt();
        return res;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public Long getDesignationId() { return designationId; }
    public String getDesignationName() { return designationName; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public Long getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }
    public Long getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public Long getCompanyBranchId() { return companyBranchId; }
    public String getCompanyBranchName() { return companyBranchName; }
    public String getContact() { return contact; }
    public String getNid() { return nid; }
    public String getPicture() { return picture; }
    public Boolean getIsAdmin() { return isAdmin; }
    public Boolean getIsOnline() { return isOnline; }
    public UserStatus getStatus() { return status; }
    public LocalDate getPasswordChangedAt() { return passwordChangedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
