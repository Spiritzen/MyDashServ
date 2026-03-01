package com.afci.training.planning.util;

import com.afci.training.planning.dto.DocumentDTO;
import com.afci.training.planning.entity.EvidenceDoc;

public final class DocumentMapper {
    private DocumentMapper(){}

    public static DocumentDTO toDTO(EvidenceDoc d) {
        if (d == null) return null;

        // Construit l’URL publique à partir du chemin absolu:
        // .../evidences/<formateurId>/<competenceId>/<file> -> /files/evidences/<formateurId>/<competenceId>/<file>
        String url = null;
        if (d.getStoragePath()!=null) {
            String normalized = d.getStoragePath().replace('\\','/');
            int idx = normalized.lastIndexOf("/evidences/");
            if (idx >= 0) url = "/files" + normalized.substring(idx);
        }

        return new DocumentDTO(
            d.getIdDoc(),
            d.getFilename(),
            d.getMimeType(),
            d.getSizeBytes(),
            url,
            d.getUploadedAt()
        );
    }
}
