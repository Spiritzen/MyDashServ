// src/main/java/com/afci/training/planning/dto/admin/AdminEvidenceDocDTO.java
package com.afci.training.planning.dto.admin;

import java.time.Instant;

public class AdminEvidenceDocDTO {
    private Integer id;
    private String filename;
    private String mimeType;
    private long sizeBytes;
    private Instant uploadedAt;
    private String url; // lien de téléchargement

    public AdminEvidenceDocDTO() {}
    public AdminEvidenceDocDTO(Integer id, String filename, String mimeType, long sizeBytes, Instant uploadedAt, String url) {
        this.id = id; this.filename = filename; this.mimeType = mimeType;
        this.sizeBytes = sizeBytes; this.uploadedAt = uploadedAt; this.url = url;
    }
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
