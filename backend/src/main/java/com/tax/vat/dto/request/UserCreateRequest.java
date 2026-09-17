package com.tax.vat.dto.request;

import com.tax.vat.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class UserCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,40}$",
            message = "Password must have at least 1 uppercase, 1 lowercase, 1 special character and 1 digit (min 8 chars)")
    private String password;

    @NotNull(message = "Group is required")
    private Long groupId;

    private Long designationId;
    private Long departmentId;
    private Long companyId;
    private Long companyBranchId;

    private List<Long> accessCompanyId;
    private List<Long> accessCompanyBranchId;

    private String contact;

    @NotBlank(message = "NID is required")
    private String nid;

    private UserStatus status = UserStatus.ACTIVE;

    private MultipartFile pictureFile;

    public UserCreateRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getDesignationId() {
        return designationId;
    }

    public void setDesignationId(Long designationId) {
        this.designationId = designationId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getCompanyBranchId() {
        return companyBranchId;
    }

    public void setCompanyBranchId(Long companyBranchId) {
        this.companyBranchId = companyBranchId;
    }

    public List<Long> getAccessCompanyId() {
        return accessCompanyId;
    }

    public void setAccessCompanyId(List<Long> accessCompanyId) {
        this.accessCompanyId = accessCompanyId;
    }

    public List<Long> getAccessCompanyBranchId() {
        return accessCompanyBranchId;
    }

    public void setAccessCompanyBranchId(List<Long> accessCompanyBranchId) {
        this.accessCompanyBranchId = accessCompanyBranchId;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public MultipartFile getPictureFile() {
        return pictureFile;
    }

    public void setPictureFile(MultipartFile pictureFile) {
        this.pictureFile = pictureFile;
    }
}
