package com.billeterie.service;

import com.google.zxing.WriterException;
import com.billeterie.model.*;
import com.billeterie.repository.BilletRepository;
import com.billeterie.repository.ReservationDetailsRepository;
import com.billeterie.repository.EtudiantRepository;
import com.billeterie.repository.PaiementRepository;
import com.billeterie.repository.ReservationRepository;
import com.billeterie.util.QRCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class BilletService {

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private ReservationDetailsRepository reservationDetailsRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private PaiementRepository paiementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private QRCodeGenerator qrCodeGenerator;

    @Autowired
    private com.billeterie.repository.StatusRepository statusRepository;

    public List<Billet> getAllBillets() throws ExecutionException, InterruptedException {
        return billetRepository.findAll();
    }

    public Billet getBilletById(String id) throws ExecutionException, InterruptedException {
        return billetRepository.findById(id);
    }

    public Billet createBillet(Billet billet) throws ExecutionException, InterruptedException, WriterException, IOException {
        // Générer un numéro unique si non fourni
        if (billet.getNumero() == null || billet.getNumero().isEmpty()) {
            billet.setNumero("BILL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Définir un prix par défaut si non fourni (5000 Ar)
        if (billet.getPrix() == 0) {
            billet.setPrix(15000.0);
        }

        // Générer le QR code
        String qrCode = qrCodeGenerator.generateQRCodeForBillet(billet.getNumero());
        billet.setQrcode(qrCode);

        return billetRepository.save(billet);
    }

    public Billet updateBillet(String id, Billet billet) throws ExecutionException, InterruptedException {
        billetRepository.update(id, billet);
        billet.setId(id);
        return billet;
    }

    public void deleteBillet(String id) throws ExecutionException, InterruptedException {
        billetRepository.delete(id);
    }

    public Map<String, Object> getBilletDetailsComplete(String billetId) throws ExecutionException, InterruptedException {
        Map<String, Object> result = new HashMap<>();

        // Récupérer le billet
        Billet billet = billetRepository.findById(billetId);
        result.put("billet", billet);

        if (billet != null) {
            // Récupérer les détails de réservation associés à ce billet
            ReservationDetails reservationDetails = reservationDetailsRepository.findByBilletId(billetId);

            if (reservationDetails != null) {
                // Récupérer l'étudiant
                Etudiant etudiant = etudiantRepository.findById(reservationDetails.getIdEtudiant());
                result.put("etudiant", etudiant);

                // Récupérer la réservation pour obtenir les paiements
                String reservationId = reservationDetails.getIdReservation();
                Reservation reservation = reservationRepository.findById(reservationId);
                result.put("reservation", reservation);

                // Récupérer les paiements associés à la réservation
                List<Paiement> paiements = paiementRepository.findByReservationId(reservationId);
                result.put("paiements", paiements);

                // Calculer le montant total et le statut de paiement
                double montantTotal = paiements.stream().mapToDouble(Paiement::getMontant).sum();
                result.put("montantTotal", montantTotal);

                boolean isPaid = paiements.stream().anyMatch(p -> "paid".equalsIgnoreCase(p.getStatus()));
                result.put("isPaid", isPaid);
            }
        }

        return result;
    }

    public Map<String, Object> validerScan(String numeroBillet) throws ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();

        // Chercher le billet par son numéro
        Billet billet = billetRepository.findByNumero(numeroBillet);

        if (billet == null) {
            throw new RuntimeException("Billet non trouvé: " + numeroBillet);
        }

        // Chercher les détails de réservation associés
        ReservationDetails reservationDetails = reservationDetailsRepository.findByBilletId(billet.getId());

        if (reservationDetails == null) {
            throw new RuntimeException("Aucune réservation associée à ce billet");
        }

        // Récupérer le statut actuel
        com.billeterie.model.Status currentStatus = null;
        if (reservationDetails.getIdStatus() != null) {
            currentStatus = statusRepository.findById(reservationDetails.getIdStatus());
        }

        // Vérifier si déjà scanné (status = "present")
        if (currentStatus != null && "present".equalsIgnoreCase(currentStatus.getValue())) {
            throw new RuntimeException("Ce billet a déjà été scanné et validé!");
        }

        // Chercher le statut "present"
        List<com.billeterie.model.Status> allStatus = statusRepository.findAll();
        com.billeterie.model.Status presentStatus = allStatus.stream()
                .filter(s -> "present".equalsIgnoreCase(s.getValue()))
                .findFirst()
                .orElse(null);

        if (presentStatus == null) {
            throw new RuntimeException("Statut 'present' non trouvé dans la base de données");
        }

        // Mettre à jour le statut
        reservationDetails.setIdStatus(presentStatus.getId());
        reservationDetailsRepository.update(reservationDetails.getId(), reservationDetails);

        // Récupérer l'étudiant pour afficher les infos
        Etudiant etudiant = etudiantRepository.findById(reservationDetails.getIdEtudiant());

        response.put("success", true);
        response.put("message", "Billet validé avec succès!");
        response.put("billet", billet);
        response.put("etudiant", etudiant);
        response.put("reservationDetails", reservationDetails);

        return response;
    }
}
