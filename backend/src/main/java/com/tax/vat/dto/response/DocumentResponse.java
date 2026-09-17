package com.tax.vat.dto.response;

public class DocumentResponse {

    private String id;
    private String title;
    private String fileName;
    private String fileUrl;
    private String mimeType;
    private Long size;
    private String expiryDate;
    private String createdAt;

    public DocumentResponse() {
    }

    public DocumentResponse(String id, String title, String fileName, String fileUrl, String mimeType, Long size, String expiryDate, String createdAt) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.mimeType = mimeType;
        this.size = size;
        this.expiryDate = expiryDate;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSize() { return size; }
    public void setSize(Long size) { this.size = size; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
