package com.afci.training.planning.service;

import com.afci.training.planning.dto.UtilisateurCreateDTO;
import com.afci.training.planning.dto.UtilisateurDTO;
import com.afci.training.planning.dto.UtilisateurUpdateDTO;
import com.afci.training.planning.entity.Formateur;
import com.afci.training.planning.entity.Utilisateur;
import com.afci.training.planning.repository.FormateurRepository;
import com.afci.training.planning.repository.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository repo;
    private final FormateurRepository formateurRepo;
    private final PasswordEncoder passwordEncoder;

    /** Dossiers alignés avec WebMvcConfig et application.properties */
    private final Path filesRoot;   // = afci.upload.root
    private final Path usersDir;    // = afci.upload.users-dir

    public UtilisateurService(UtilisateurRepository repo,
                              FormateurRepository formateurRepo,
                              PasswordEncoder passwordEncoder,
                              @Value("${afci.upload.root}") String uploadsRoot,
                              @Value("${afci.upload.users-dir}") String usersDirProp) {
        this.repo = repo;
        this.formateurRepo = formateurRepo;
        this.passwordEncoder = passwordEncoder;

        this.filesRoot = Paths.get(uploadsRoot).toAbsolutePath().normalize();
        this.usersDir  = Paths.get(usersDirProp).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.usersDir);
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier d'upload: " + this.usersDir, e);
        }
        System.out.println("[UPLOAD INIT] filesRoot=" + filesRoot + " | usersDir=" + usersDir);
    }

    // ===================== CREATE =====================

    public UtilisateurDTO create(@Valid UtilisateurCreateDTO dto) {
        String passwordHash = passwordEncoder.encode(dto.getPassword());

        Utilisateur u = new Utilisateur(
            dto.getEmail(),
            passwordHash,
            Utilisateur.Role.FORMATEUR,
            dto.getNom(), dto.getPrenom(),
            dto.getAdresse(), dto.getVille(), dto.getCodePostal(),
            dto.getTelephone(), dto.getPhotoPath()
        );

        Formateur f = new Formateur();
        f.setActif(false);
        if (f.getHeuresMaxMensuelles() == null) f.setHeuresMaxMensuelles(151);
        f = formateurRepo.save(f);

        u.setFormateur(f);
        u = repo.save(u);

        return toDTO(u);
    }

    // ===================== READ =====================

    @Transactional(readOnly = true)
    public List<UtilisateurDTO> findAll() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UtilisateurDTO findById(Integer id) {
        Utilisateur u = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));
        return toDTO(u);
    }

    @Transactional(readOnly = true)
    public UtilisateurDTO findByEmail(String email) {
        Utilisateur u = repo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + email + " introuvable"));
        return toDTO(u);
    }

    @Transactional(readOnly = true)
    public Utilisateur findByEmailOrThrow(String email) {
        return repo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + email + " introuvable"));
    }

    // ===================== UPDATE =====================

    public UtilisateurDTO update(Integer id, @Valid UtilisateurUpdateDTO dto) {
        Utilisateur u = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));
        applyIdentityContact(u, dto);
        return toDTO(u);
    }

    public UtilisateurDTO updateByEmail(String email, @Valid UtilisateurUpdateDTO dto) {
        Utilisateur u = repo.findByEmail(email)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + email + " introuvable"));
        applyIdentityContact(u, dto);
        return toDTO(u);
    }

    // ===================== PHOTO =====================

    public UtilisateurDTO uploadPhoto(Integer id, MultipartFile file) {
        System.out.println("[UPLOAD PHOTO] begin for userId=" + id + " | empty=" + (file == null || file.isEmpty()));
        Utilisateur u = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Aucun fichier fourni (paramètre 'file' attendu).");
        }

        String contentType = Optional.ofNullable(file.getContentType()).orElse("");
        String ext = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            default -> {
                System.err.println("[UPLOAD PHOTO] Unsupported contentType=" + contentType);
                throw new IllegalArgumentException("Type de fichier non supporté (jpeg/png/webp).");
            }
        };

        try {
            // Nom de fichier unique
            String filename = UUID.randomUUID().toString() + "." + ext;
            Path target = usersDir.resolve(filename).normalize();

            // Ecrit le fichier
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // Supprime l'ancienne photo si présente
            deletePhysicalFileIfExists(u.getPhotoPath());

            // Met à jour l'entité (photoPath RELATIF à /files/**)
            u.setPhotoPath(relativizeToRoot(target));
            u = repo.save(u);

            System.out.println("[UPLOAD PHOTO] saved -> " + target + " | photoPath=" + u.getPhotoPath());
            return toDTO(u);
        } catch (IOException e) {
            System.err.println("[UPLOAD PHOTO] IO error: " + e.getMessage());
            throw new RuntimeException("Échec d'upload de l'image : " + e.getMessage(), e);
        }
    }

    public void deletePhoto(Integer id) {
        Utilisateur u = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));

        deletePhysicalFileIfExists(u.getPhotoPath());
        u.setPhotoPath(null);
        repo.save(u);
        System.out.println("[DELETE PHOTO] deleted for userId=" + id);
    }

    // ===================== DELETE USER =====================

    public void delete(Integer id) {
        Utilisateur u = repo.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Utilisateur " + id + " introuvable"));

        Integer formateurId = (u.getFormateur() != null ? u.getFormateur().getIdFormateur() : null);

        deletePhysicalFileIfExists(u.getPhotoPath());
        repo.deleteById(id);
        if (formateurId != null) formateurRepo.deleteById(formateurId);

        System.out.println("[USER DELETE] userId=" + id + " (and formateurId=" + formateurId + ")");
    }

    // ===================== HELPERS =====================

    private void applyIdentityContact(Utilisateur u, UtilisateurUpdateDTO dto) {
        if (dto.getNom() != null) u.setNom(dto.getNom());
        if (dto.getPrenom() != null) u.setPrenom(dto.getPrenom());
        if (dto.getAdresse() != null) u.setAdresse(dto.getAdresse());
        if (dto.getVille() != null) u.setVille(dto.getVille());
        if (dto.getCodePostal() != null) u.setCodePostal(dto.getCodePostal());
        if (dto.getTelephone() != null) u.setTelephone(dto.getTelephone());
        if (dto.getPhotoPath() != null) u.setPhotoPath(dto.getPhotoPath());
    }

    private void deletePhysicalFileIfExists(String photoPath) {
        if (photoPath == null || photoPath.isBlank()) return;
        try {
            Path p = filesRoot.resolve(photoPath).normalize();
            if (Files.exists(p)) {
                Files.delete(p);
                System.out.println("[DELETE FILE] " + p);
            }
        } catch (IOException ex) {
            System.err.println("[DELETE FILE] failed: " + ex.getMessage());
        }
    }

    /** Convertit un chemin absolu sous filesRoot en chemin relatif (ex: users/xxx.jpg) */
    private String relativizeToRoot(Path absolutePathUnderRoot) {
        Path rel = filesRoot.relativize(absolutePathUnderRoot);
        // Normalise séparateurs pour URL (Windows safe)
        String path = rel.toString().replace('\\', '/');
        if (!path.startsWith("users/")) {
            // sécurité : si on n'est pas sous users/, on force
            return "users/" + path.substring(path.lastIndexOf('/') + 1);
        }
        return path;
    }

    // ===================== MAPPING =====================

    private UtilisateurDTO toDTO(com.afci.training.planning.entity.Utilisateur u) {
        // Construit l’URL publique à partir de photoPath (stocké côté DB)
        // Ex: photoPath = "users/xxx.jpg"  -> photoUrl = "/files/users/xxx.jpg"
        String path = u.getPhotoPath();
        String url  = (path != null && !path.isBlank())
                ? ("/files/" + path.replace('\\','/'))
                : null;

        return new UtilisateurDTO(
            u.getIdUtilisateur(),
            u.getEmail(),
            (u.getRole() != null ? u.getRole().name() : null),
            u.getNom(), u.getPrenom(),
            u.getAdresse(), u.getVille(), u.getCodePostal(),
            u.getTelephone(),
            url,                                           // ✅ photoUrl attendu par le front
            Boolean.valueOf(u.isCompteValide()),           // ✅ booleans -> Boolean
            Boolean.valueOf(u.isEmailVerifie()),           // ✅ booleans -> Boolean
            (u.getFormateur() != null ? u.getFormateur().getIdFormateur() : null)
        );
    }
}
