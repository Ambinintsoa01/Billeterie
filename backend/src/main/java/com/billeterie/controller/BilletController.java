package com.billeterie.controller;

import com.google.zxing.WriterException;
import com.billeterie.model.Billet;
import com.billeterie.service.BilletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/billet")
@CrossOrigin(origins = "*")
public class BilletController {

    @Autowired
    private BilletService billetService;

    @GetMapping
    public ResponseEntity<List<Billet>> getAllBillets() {
        try {
            List<Billet> billets = billetService.getAllBillets();
            return new ResponseEntity<>(billets, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getBilletDetails(@PathVariable String id) {
        try {
            Map<String, Object> details = billetService.getBilletDetailsComplete(id);
            if (details.get("billet") != null) {
                return new ResponseEntity<>(details, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Billet> createBillet(@RequestBody Billet billet) {
        try {
            Billet created = billetService.createBillet(billet);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ExecutionException | InterruptedException | WriterException | IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Billet> updateBillet(@PathVariable String id, @RequestBody Billet billet) {
        try {
            Billet updated = billetService.updateBillet(id, billet);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable String id) {
        try {
            billetService.deleteBillet(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/valider-scan")
    public ResponseEntity<?> validerScan(@RequestBody Map<String, String> payload) {
        try {
            String numeroBillet = payload.get("numeroBillet");
            if (numeroBillet == null || numeroBillet.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Numéro de billet requis"
                ));
            }

            Map<String, Object> response = billetService.validerScan(numeroBillet);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (ExecutionException | InterruptedException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erreur serveur: " + e.getMessage()
            ));
        }
    }
}
