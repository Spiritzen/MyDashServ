package com.afci.training.planning.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "parametrage", uniqueConstraints = @UniqueConstraint(columnNames = "cle"))
public class Parametrage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_param")
    private Integer idParam;

    @NotBlank @Size(max = 80)
    @Column(name = "cle", length = 80, nullable = false, unique = true)
    private String cle;

    @NotBlank @Size(max = 120)
    @Column(name = "valeur", length = 120, nullable = false)
    private String valeur;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    public Parametrage() {}
    public Parametrage(String cle, String valeur, String description) {
        this.cle = cle; this.valeur = valeur; this.description = description;
    }
    public Parametrage(Integer idParam, String cle, String valeur, String description) {
        this(cle, valeur, description); this.idParam = idParam;
    }

    public Integer getIdParam() { return idParam; }
    public void setIdParam(Integer idParam) { this.idParam = idParam; }
    public String getCle() { return cle; }
    public void setCle(String cle) { this.cle = cle; }
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override public String toString() {
        return "Parametrage{id=" + idParam + ", cle='" + cle + "', valeur='" + valeur + "'}";
    }
}
