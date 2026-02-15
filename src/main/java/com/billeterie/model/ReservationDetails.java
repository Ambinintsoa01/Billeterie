package com.billeterie.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDetails {

    private String id;
    private String numero;
    private String idReservation;
    private String idEtudiant;
    private String idBillet;
    private String idStatus;

    // Relations pour les détails
    private Etudiant etudiant;
    private Billet billet;
    private Status status;

    public ReservationDetails(Billet billet, Etudiant etudiant, String id, String idBillet, String idEtudiant, String idReservation, String idStatus, String numero, Status status) {
        this.billet = billet;
        this.etudiant = etudiant;
        this.id = id;
        this.idBillet = idBillet;
        this.idEtudiant = idEtudiant;
        this.idReservation = idReservation;
        this.idStatus = idStatus;
        this.numero = numero;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(String idReservation) {
        this.idReservation = idReservation;
    }

    public String getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(String idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public String getIdBillet() {
        return idBillet;
    }

    public void setIdBillet(String idBillet) {
        this.idBillet = idBillet;
    }

    public String getIdStatus() {
        return idStatus;
    }

    public void setIdStatus(String idStatus) {
        this.idStatus = idStatus;
    }

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;
    }

    public Billet getBillet() {
        return billet;
    }

    public void setBillet(Billet billet) {
        this.billet = billet;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
