package com.bradesco.auth_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "usuarios")
@Data
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;
    
    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;
    
    @Column(name = "senha", nullable = false)
    private String senha;
    
    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "saldo_disponivel", precision = 10, scale = 2)
    private BigDecimal saldoDisponivel = new BigDecimal("50000.00");

    @Column(name = "role", nullable = false)
    private String role = "USER";
    
    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}