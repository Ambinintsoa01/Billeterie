package com.billeterie.dto;

import java.util.Date;
import java.util.List;

public class ReservationCompleteDTO {

    private Date date;
    private List<EtudiantDTO> etudiants;

    public ReservationCompleteDTO() {
    }

    public ReservationCompleteDTO(Date date, List<EtudiantDTO> etudiants) {
        this.date = date;
        this.etudiants = etudiants;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<EtudiantDTO> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(List<EtudiantDTO> etudiants) {
        this.etudiants = etudiants;
    }
}
