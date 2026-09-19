package com.tax.vat.dto.response;

import java.time.LocalDate;

public class Musak91DataTableItem {
    private Long id;
    private String slug;
    private Integer sn;
    private String period;
    private String company;
    private String bin;
    private LocalDate periodFrom;
    private LocalDate periodTo;
    private LocalDate dueDate;
    private LocalDate submissionDate;
    private String returnType;

    public Musak91DataTableItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public Integer getSn() { return sn; }
    public void setSn(Integer sn) { this.sn = sn; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getBin() { return bin; }
    public void setBin(String bin) { this.bin = bin; }

    public LocalDate getPeriodFrom() { return periodFrom; }
    public void setPeriodFrom(LocalDate periodFrom) { this.periodFrom = periodFrom; }

    public LocalDate getPeriodTo() { return periodTo; }
    public void setPeriodTo(LocalDate periodTo) { this.periodTo = periodTo; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }

    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
}
