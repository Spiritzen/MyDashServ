package com.afci.training.planning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.UUID;

@Service
public class StorageService {

    private final Path evidencesRoot;

    public StorageService(@Value("${afci.upload.evidences-dir}") String evidencesDir) {
        this.evidencesRoot = Path.of(evidencesDir);
        try { Files.createDirectories(this.evidencesRoot); } catch (Exception ignored) {}
    }

    public record Saved(String filename, String mimeType, long size, String absPath, String sha256) {}

    public Saved saveEvidence(Integer formateurId, Integer competenceId, MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) throw new IllegalArgumentException("Fichier vide");

        Path dir = evidencesRoot.resolve(String.valueOf(formateurId)).resolve(String.valueOf(competenceId));
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() != null ? file.getOriginalFilename() : "doc";
        String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String stored = UUID.randomUUID() + "_" + safeName;

        Path target = dir.resolve(stored);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        String sha = sha256(target);
        return new Saved(stored, file.getContentType(), file.getSize(), target.toString().replace('\\','/'), sha);
    }

    public void delete(String absolutePath) {
        try { Files.deleteIfExists(Path.of(absolutePath)); } catch (Exception ignored) {}
    }

    private static String sha256(Path p) throws Exception {
        byte[] bytes = Files.readAllBytes(p);
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
