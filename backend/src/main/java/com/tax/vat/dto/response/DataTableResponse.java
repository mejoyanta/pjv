package com.tax.vat.dto.response;

import java.util.Collections;
import java.util.List;

public class DataTableResponse<T> {

    private Long draw;
    private Long recordsTotal;
    private Long recordsFiltered;
    private List<T> data = Collections.emptyList();
    private Boolean customActionStatus;
    private String customActionMessage;

    public DataTableResponse() {
    }

    public DataTableResponse(Long draw, Long recordsTotal, Long recordsFiltered, List<T> data) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recordsFiltered;
        this.data = data;
    }

    public static <T> DataTableResponse<T> of(Long draw, Long total, Long filtered, List<T> data) {
        return new DataTableResponse<>(draw, total, filtered, data);
    }

    public Long getDraw() {
        return draw;
    }

    public void setDraw(Long draw) {
        this.draw = draw;
    }

    public Long getRecordsTotal() {
        return recordsTotal;
    }

    public void setRecordsTotal(Long recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public Long getRecordsFiltered() {
        return recordsFiltered;
    }

    public void setRecordsFiltered(Long recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Boolean getCustomActionStatus() {
        return customActionStatus;
    }

    public void setCustomActionStatus(Boolean customActionStatus) {
        this.customActionStatus = customActionStatus;
    }

    public String getCustomActionMessage() {
        return customActionMessage;
    }

    public void setCustomActionMessage(String customActionMessage) {
        this.customActionMessage = customActionMessage;
    }
}
