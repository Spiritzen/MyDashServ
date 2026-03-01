package com.afci.training.planning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_doc")
public class EvidenceDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doc")
    private Integer idDoc;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "competence_id", referencedColumnName = "competence_id", nullable = false),
        @JoinColumn(name = "formateur_id",  referencedColumnName = "formateur_id",  nullable = false)
    })
    private FormateurComp formateurComp;

    @Column(name = "filename", nullable = false, length = 200)
    private String filename;

    @Column(name = "mime_type", nullable = false, length = 120)
    private String mimeType;

    @Column(name = "size_bytes", nullable = false)
    private Long sizeBytes;

    @Column(name = "storage_path", nullable = false, length = 260)
    private String storagePath;

    @Column(name = "sha256", nullable = false, length = 64)
    private String sha256;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

    // --- Ctors ---
    public EvidenceDoc() {}

    public EvidenceDoc(FormateurComp fc, String filename, String mimeType,
                       Long sizeBytes, String storagePath, String sha256) {
        this.formateurComp = fc;
        this.filename = filename;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
        this.storagePath = storagePath;
        this.sha256 = sha256;
    }

    // --- Getters/Setters ---
    public Integer getIdDoc() { return idDoc; }
    public void setIdDoc(Integer idDoc) { this.idDoc = idDoc; }

    public FormateurComp getFormateurComp() { return formateurComp; }
    public void setFormateurComp(FormateurComp formateurComp) { this.formateurComp = formateurComp; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(Long sizeBytes) { this.sizeBytes = sizeBytes; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public String getSha256() { return sha256; }
    public void setSha256(String sha256) { this.sha256 = sha256; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    @Override
    public String toString() {
        return "EvidenceDoc{idDoc=" + idDoc +
               ", filename='" + filename + '\'' +
               ", sizeBytes=" + sizeBytes +
               ", formateurId=" + (formateurComp!=null? formateurComp.getFormateur().getIdFormateur():null) +
               ", competenceId=" + (formateurComp!=null? formateurComp.getCompetence().getIdCompetence():null) +
               '}';
    }
}
