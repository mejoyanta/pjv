package com.tax.vat.dto.request;

public class CompanyUpdateRequest extends CompanyCreateRequest {

    private String usernameChangeReason;

    public CompanyUpdateRequest() {
    }

    public String getUsernameChangeReason() {
        return usernameChangeReason;
    }

    public void setUsernameChangeReason(String usernameChangeReason) {
        this.usernameChangeReason = usernameChangeReason;
    }
}
