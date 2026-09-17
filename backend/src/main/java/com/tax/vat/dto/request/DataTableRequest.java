package com.tax.vat.dto.request;

import java.util.List;

public class DataTableRequest {

    private Long draw = 1L;
    private Integer start = 0;
    private Integer length = 10;
    private String searchValue;
    private Integer sortColumn = 0;
    private String sortDirection = "desc";

    // Row Actions from DataTables
    private String actionType;
    private Long recordId;
    private List<Long> recordIds;

    public DataTableRequest() {
    }

    public Long getDraw() {
        return draw != null ? draw : 1L;
    }

    public void setDraw(Long draw) {
        this.draw = draw;
    }

    public Integer getStart() {
        return start != null ? start : 0;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getLength() {
        return length != null ? length : 10;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public Integer getSortColumn() {
        return sortColumn != null ? sortColumn : 0;
    }

    public void setSortColumn(Integer sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortDirection() {
        return sortDirection != null ? sortDirection : "desc";
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
}
