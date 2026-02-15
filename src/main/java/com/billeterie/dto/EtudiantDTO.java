package com.billeterie.dto;

public class EtudiantDTO {

    private String etu;
    private String nom;
    private String prenom;

    public EtudiantDTO() {
    }

    public EtudiantDTO(String etu, String nom, String prenom) {
        this.etu = etu;
        this.nom = nom;
        this.prenom = prenom;
    }

    public String getEtu() {
        return etu;
    }

    public void setEtu(String etu) {
        this.etu = etu;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
}
