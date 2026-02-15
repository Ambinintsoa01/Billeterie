package com.billeterie.controller;

import com.billeterie.model.Paiement;
import com.billeterie.service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/paiement")
@CrossOrigin(origins = "*")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @GetMapping
    public ResponseEntity<List<Paiement>> getAllPaiements() {
        try {
            List<Paiement> paiements = paiementService.getAllPaiements();
            return new ResponseEntity<>(paiements, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable String id) {
        try {
            Paiement paiement = paiementService.getPaiementById(id);
            if (paiement != null) {
                return new ResponseEntity<>(paiement, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<Paiement>> getPaiementsByReservation(@PathVariable String reservationId) {
        try {
            List<Paiement> paiements = paiementService.getPaiementsByReservationId(reservationId);
            return new ResponseEntity<>(paiements, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Paiement> createPaiement(@RequestBody Paiement paiement) {
        try {
            Paiement created = paiementService.createPaiement(paiement);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> updatePaiement(@PathVariable String id, @RequestBody Paiement paiement) {
        try {
            Paiement updated = paiementService.updatePaiement(id, paiement);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable String id) {
        try {
            paiementService.deletePaiement(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
