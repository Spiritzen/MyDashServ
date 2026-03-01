package com.afci.training.planning.dto;

import java.time.LocalDateTime;

public class DocumentDTO {
    private Integer idDoc;
    private String filename;
    private String mimeType;
    private Long sizeBytes;
    private String url;                 // URL publique pour le front
    private LocalDateTime uploadedAt;

    public DocumentDTO() {}

    public DocumentDTO(Integer idDoc, String filename, String mimeType,
                       Long sizeBytes, String url, LocalDateTime uploadedAt) {
        this.idDoc = idDoc; this.filename = filename; this.mimeType = mimeType;
        this.sizeBytes = sizeBytes; this.url = url; this.uploadedAt = uploadedAt;
    }

    public Integer getIdDoc() { return idDoc; }
    public void setIdDoc(Integer idDoc) { this.idDoc = idDoc; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
