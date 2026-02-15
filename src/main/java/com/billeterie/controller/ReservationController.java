package com.billeterie.controller;

import com.billeterie.dto.ReservationCompleteDTO;
import com.billeterie.model.Reservation;
import com.billeterie.service.ReservationService;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/reservation")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        try {
            List<Reservation> reservations = reservationService.getAllReservations();
            return new ResponseEntity<>(reservations, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<Map<String, Object>> getReservationDetails(@PathVariable String id) {
        try {
            Map<String, Object> details = reservationService.getReservationDetails(id);
            if (details.get("reservation") != null) {
                return new ResponseEntity<>(details, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> createReservationComplete(@RequestBody ReservationCompleteDTO dto) {
        try {
            Map<String, Object> result = reservationService.createReservationComplete(dto);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (ExecutionException | InterruptedException | WriterException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable String id, @RequestBody Reservation reservation) {
        try {
            Reservation updated = reservationService.updateReservation(id, reservation);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable String id) {
        try {
            reservationService.deleteReservation(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
