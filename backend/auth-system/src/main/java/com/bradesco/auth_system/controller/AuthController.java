package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Usuario;
import com.bradesco.auth_system.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = authService.login(loginRequest.getCpf(), loginRequest.getSenha());
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }
    
    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = authService.registrar(usuario);
            return ResponseEntity.ok(novoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // CLASSE INTERNA - deve estar DENTRO da classe AuthController
    public static class LoginRequest {
        private String cpf;
        private String senha;
        
        // getters e setters
        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }
        public String getSenha() { return senha; }
        public void setSenha(String senha) { this.senha = senha; }
    }
} // <- ESTA CHAVE FINALIZA A CLASSE AuthController