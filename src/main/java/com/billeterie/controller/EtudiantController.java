package com.billeterie.controller;

import com.billeterie.model.Etudiant;
import com.billeterie.service.EtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/etudiant")
@CrossOrigin(origins = "*")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants() {
        try {
            List<Etudiant> etudiants = etudiantService.getAllEtudiants();
            return new ResponseEntity<>(etudiants, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Etudiant> getEtudiantDetails(@PathVariable String id) {
        try {
            Etudiant etudiant = etudiantService.getEtudiantById(id);
            if (etudiant != null) {
                return new ResponseEntity<>(etudiant, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Etudiant> createEtudiant(@RequestBody Etudiant etudiant) {
        try {
            Etudiant created = etudiantService.createEtudiant(etudiant);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> updateEtudiant(@PathVariable String id, @RequestBody Etudiant etudiant) {
        try {
            Etudiant updated = etudiantService.updateEtudiant(id, etudiant);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEtudiant(@PathVariable String id) {
        try {
            etudiantService.deleteEtudiant(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
