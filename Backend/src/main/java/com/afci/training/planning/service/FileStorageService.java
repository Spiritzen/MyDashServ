package com.afci.training.planning.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootDir;
    private final Path usersDir;

    public FileStorageService(
            @Value("${afci.upload.root}") String uploadsRoot,
            @Value("${afci.upload.users-dir:${afci.upload.root}/users}") String usersDirProp // fallback si absent
    ) throws IOException {
        this.rootDir  = Path.of(uploadsRoot).toAbsolutePath().normalize();
        this.usersDir = Path.of(usersDirProp).toAbsolutePath().normalize();
        Files.createDirectories(this.rootDir);
        Files.createDirectories(this.usersDir);
    }

    public String storeUserImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new IOException("Fichier vide");
        String ct = file.getContentType();
        if (ct == null || !(ct.equals("image/jpeg") || ct.equals("image/png") || ct.equals("image/webp"))) {
            throw new IOException("Type non supporté (jpeg/png/webp)");
        }
        String ext = switch (ct) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            default -> ".webp";
        };
        String filename = UUID.randomUUID().toString().replace("-", "") + ext;
        Path target = usersDir.resolve(filename).normalize();
        if (!target.startsWith(usersDir)) throw new IOException("Chemin invalide");
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return "users/" + filename; // relatif pour /files/**
    }

    public void deleteIfExists(String pathRelativeToRoot) {
        if (pathRelativeToRoot == null || pathRelativeToRoot.isBlank()) return;
        Path p = rootDir.resolve(pathRelativeToRoot).normalize();
        try { if (p.startsWith(rootDir)) Files.deleteIfExists(p); } catch (IOException ignored) {}
    }
}

