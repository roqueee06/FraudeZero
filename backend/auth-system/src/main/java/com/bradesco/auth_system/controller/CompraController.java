package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class CompraController {
    
    @Autowired
    private CompraService compraService;
    
    @PostMapping
    public ResponseEntity<?> criarCompra(@RequestBody Compra compra) {
        try {
            // Use salvarCompra() em vez de salvar()
            Compra novaCompra = compraService.salvarCompra(compra);
            return ResponseEntity.ok(novaCompra);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao criar compra: " + e.getMessage());
        }
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Compra>> getComprasPorUsuario(@PathVariable Long idUsuario) {
        // Use buscarComprasPorUsuario() em vez de buscarTodasCompras()
        List<Compra> compras = compraService.buscarComprasPorUsuario(idUsuario);
        return ResponseEntity.ok(compras);
    }
}