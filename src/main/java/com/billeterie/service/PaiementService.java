package com.billeterie.service;

import com.billeterie.model.*;
import com.billeterie.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationDetailsRepository reservationDetailsRepository;

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private StatusRepository statusRepository;

    public List<Paiement> getAllPaiements() throws ExecutionException, InterruptedException {
        return paiementRepository.findAll();
    }

    public List<Paiement> getPaiementsByReservationId(String reservationId) throws ExecutionException, InterruptedException {
        return paiementRepository.findByReservationId(reservationId);
    }

    public Paiement getPaiementById(String id) throws ExecutionException, InterruptedException {
        return paiementRepository.findById(id);
    }

    public Paiement createPaiement(Paiement paiement) throws ExecutionException, InterruptedException {
        Paiement saved = paiementRepository.save(paiement);

        // Vérifier si le paiement est complet et mettre à jour le statut
        verifierEtMettreAJourStatut(paiement.getIdReservation());

        return saved;
    }

    private void verifierEtMettreAJourStatut(String reservationId) throws ExecutionException, InterruptedException {
        // Récupérer tous les détails de la réservation
        List<ReservationDetails> details = reservationDetailsRepository.findByReservationId(reservationId);

        // Récupérer tous les paiements de la réservation
        List<Paiement> paiements = paiementRepository.findByReservationId(reservationId);

        // Calculer le montant total payé
        double montantPaye = paiements.stream()
                .filter(p -> "paid".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Paiement::getMontant)
                .sum();

        // Calculer le montant total attendu (nombre de billets × prix)
        double montantTotal = 0;
        for (ReservationDetails detail : details) {
            if (detail.getIdBillet() != null) {
                Billet billet = billetRepository.findById(detail.getIdBillet());
                if (billet != null) {
                    montantTotal += billet.getPrix();
                }
            }
        }

        // Si le montant payé >= montant total, marquer tous les détails comme "payé"
        if (montantPaye >= montantTotal && montantTotal > 0) {
            // Chercher le statut "Payé"
            Status statusPaye = findOrCreateStatus("paid", "Payé");

            for (ReservationDetails detail : details) {
                detail.setIdStatus(statusPaye.getId());
                reservationDetailsRepository.update(detail.getId(), detail);
            }
        }
    }

    private Status findOrCreateStatus(String value, String libelle) throws ExecutionException, InterruptedException {
        // Récupérer tous les statuts et chercher celui qui correspond
        List<Status> allStatus = statusRepository.findAll();
        for (Status s : allStatus) {
            if (value.equalsIgnoreCase(s.getValue())) {
                return s;
            }
        }

        // Si non trouvé, créer un nouveau statut
        Status newStatus = new Status();
        newStatus.setValue(value);
        newStatus.setLibelle(libelle);
        return statusRepository.save(newStatus);
    }

    public Paiement updatePaiement(String id, Paiement paiement) throws ExecutionException, InterruptedException {
        paiementRepository.update(id, paiement);
        paiement.setId(id);
        return paiement;
    }

    public void deletePaiement(String id) throws ExecutionException, InterruptedException {
        paiementRepository.delete(id);
    }
}
