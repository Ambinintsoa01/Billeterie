package com.billeterie.service;

import com.billeterie.dto.EtudiantDTO;
import com.billeterie.dto.ReservationCompleteDTO;
import com.billeterie.model.*;
import com.billeterie.repository.*;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationDetailsRepository reservationDetailsRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private BilletService billetService;

    public List<Reservation> getAllReservations() throws ExecutionException, InterruptedException {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(String id) throws ExecutionException, InterruptedException {
        return reservationRepository.findById(id);
    }

    public Map<String, Object> getReservationDetails(String id) throws ExecutionException, InterruptedException {
        Reservation reservation = reservationRepository.findById(id);
        List<ReservationDetails> details = reservationDetailsRepository.findByReservationId(id);

        // Calculer le montant total attendu
        double montantTotal = 0;

        // Enrichir les détails avec les données complètes
        for (ReservationDetails detail : details) {
            if (detail.getIdEtudiant() != null) {
                detail.setEtudiant(etudiantRepository.findById(detail.getIdEtudiant()));
            }
            if (detail.getIdBillet() != null) {
                Billet billet = billetRepository.findById(detail.getIdBillet());
                detail.setBillet(billet);
                if (billet != null) {
                    montantTotal += billet.getPrix();
                }
            }
        }

        // Récupérer les paiements
        List<Paiement> paiements = paiementRepository.findByReservationId(id);

        // Calculer le montant payé
        double montantPaye = paiements.stream()
                .filter(p -> "paid".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Paiement::getMontant)
                .sum();

        // Calculer le reste à payer
        double resteAPayer = Math.max(0, montantTotal - montantPaye);

        Map<String, Object> response = new HashMap<>();
        response.put("reservation", reservation);
        response.put("details", details);
        response.put("paiements", paiements);
        response.put("montantTotal", montantTotal);
        response.put("montantPaye", montantPaye);
        response.put("resteAPayer", resteAPayer);

        return response;
    }

    public Reservation createReservation(Reservation reservation) throws ExecutionException, InterruptedException {
        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(String id, Reservation reservation) throws ExecutionException, InterruptedException {
        reservationRepository.update(id, reservation);
        reservation.setId(id);
        return reservation;
    }

    public void deleteReservation(String id) throws ExecutionException, InterruptedException {
        reservationRepository.delete(id);
    }

    public ReservationDetails addReservationDetail(ReservationDetails detail) throws ExecutionException, InterruptedException {
        return reservationDetailsRepository.save(detail);
    }

    public void updateReservationDetailStatus(String detailId, String statusId) throws ExecutionException, InterruptedException {
        ReservationDetails detail = reservationDetailsRepository.findById(detailId);
        if (detail != null) {
            detail.setIdStatus(statusId);
            reservationDetailsRepository.update(detailId, detail);
        }
    }

    public Map<String, Object> createReservationComplete(ReservationCompleteDTO dto) throws ExecutionException, InterruptedException, WriterException, IOException {
        // 1. Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setDate(dto.getDate());
        Reservation savedReservation = reservationRepository.save(reservation);

        List<Map<String, Object>> createdItems = new ArrayList<>();

        // 2. Pour chaque étudiant dans le DTO
        for (EtudiantDTO etudiantDTO : dto.getEtudiants()) {
            // Créer l'étudiant
            Etudiant etudiant = new Etudiant();
            etudiant.setEtu(etudiantDTO.getEtu());
            etudiant.setNom(etudiantDTO.getNom());
            etudiant.setPrenom(etudiantDTO.getPrenom());
            Etudiant savedEtudiant = etudiantService.createEtudiant(etudiant);

            // Créer le billet (numéro et QR code auto-générés)
            Billet billet = new Billet();
            Billet savedBillet = billetService.createBillet(billet);

            // Créer le détail de réservation
            ReservationDetails detail = new ReservationDetails();
            detail.setIdReservation(savedReservation.getId());
            detail.setIdEtudiant(savedEtudiant.getId());
            detail.setIdBillet(savedBillet.getId());
            detail.setIdStatus("En attente");
            ReservationDetails savedDetail = reservationDetailsRepository.save(detail);

            // Ajouter aux données de retour
            Map<String, Object> item = new HashMap<>();
            item.put("etudiant", savedEtudiant);
            item.put("billet", savedBillet);
            item.put("detail", savedDetail);
            createdItems.add(item);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("reservation", savedReservation);
        response.put("items", createdItems);

        return response;
    }
}
