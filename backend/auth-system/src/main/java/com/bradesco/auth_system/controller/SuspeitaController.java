package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.repository.SuspeitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspeitas")
@CrossOrigin("*")
public class SuspeitaController {

    @Autowired
    private SuspeitaRepository suspeitaRepository;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Suspeita>> getSuspeitasPorUsuario(@PathVariable Long idUsuario) {
        List<Suspeita> suspeitas = suspeitaRepository.findByIdUsuario(idUsuario);
        return ResponseEntity.ok(suspeitas);
    }

    @GetMapping
    public ResponseEntity<List<Suspeita>> getAllSuspeitas() {
        List<Suspeita> suspeitas = suspeitaRepository.findAll();
        return ResponseEntity.ok(suspeitas);
    }
}