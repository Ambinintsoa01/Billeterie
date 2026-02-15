package com.billeterie.service;

import com.billeterie.model.Etudiant;
import com.billeterie.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    public List<Etudiant> getAllEtudiants() throws ExecutionException, InterruptedException {
        return etudiantRepository.findAll();
    }

    public Etudiant getEtudiantById(String id) throws ExecutionException, InterruptedException {
        return etudiantRepository.findById(id);
    }

    public Etudiant createEtudiant(Etudiant etudiant) throws ExecutionException, InterruptedException {
        // Générer un numéro unique si non fourni
        if (etudiant.getNumero() == null || etudiant.getNumero().isEmpty()) {
            etudiant.setNumero("ETU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        return etudiantRepository.save(etudiant);
    }

    public Etudiant updateEtudiant(String id, Etudiant etudiant) throws ExecutionException, InterruptedException {
        etudiantRepository.update(id, etudiant);
        etudiant.setId(id);
        return etudiant;
    }

    public void deleteEtudiant(String id) throws ExecutionException, InterruptedException {
        etudiantRepository.delete(id);
    }
}
