package com.tax.vat.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Musak91OnlineDataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private String period;
    private LocalDate uploadedDate;
    private LocalDateTime createdAt;
    private String company;
    private String bin;
    private String file;

    public Musak91OnlineDataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public LocalDate getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(LocalDate uploadedDate) { this.uploadedDate = uploadedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }

    public String getFile() { return file; }
    public void setFile(String file) { this.file = file; }
}
