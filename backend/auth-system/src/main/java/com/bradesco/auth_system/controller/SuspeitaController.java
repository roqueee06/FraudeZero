package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.service.SuspeitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspeitas")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SuspeitaController {
    
    @Autowired
    private SuspeitaService suspeitaService;
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Suspeita>> getSuspeitasPorUsuario(@PathVariable Long idUsuario) {
        List<Suspeita> suspeitas = suspeitaService.buscarSuspeitasPorUsuario(idUsuario);
        return ResponseEntity.ok(suspeitas);
    }
    
    @PostMapping("/aprovar/{idCompra}")
    public ResponseEntity<?> aprovarCompra(@PathVariable Long idCompra) {
        try {
            suspeitaService.aprovarCompra(idCompra);
            return ResponseEntity.ok("Compra aprovada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao aprovar compra: " + e.getMessage());
        }
    }
    
    @PostMapping("/contestar/{idCompra}")
    public ResponseEntity<?> contestarCompra(@PathVariable Long idCompra) {
        try {
            suspeitaService.contestarCompra(idCompra);
            return ResponseEntity.ok("Compra contestada com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao contestar compra: " + e.getMessage());
        }
    }
}