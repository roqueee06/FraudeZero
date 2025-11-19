package com.bradesco.auth_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compras")
@Data
public class Compra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    
    @Column(name = "distancia_da_casa", precision = 8, scale = 2)
    private BigDecimal distanciaDaCasa;
    
    @Column(name = "distancia_da_ultima_transacao", precision = 8, scale = 2)
    private BigDecimal distanciaDaUltimaTransacao;
    
    @Column(name = "preco_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoCompra;
    
    @Column(name = "id_loja", nullable = false)
    private Integer idLoja;
    
    @Column(name = "usou_chip")
    private Boolean usouChip = false;
    
    @Column(name = "usou_senha")
    private Boolean usouSenha = false;
    
    @Column(name = "pedido_online")
    private Boolean pedidoOnline = false;
    
    @Column(name = "data_transacao")
    private LocalDateTime dataTransacao;
    
    @PrePersist
    protected void onCreate() {
        if (dataTransacao == null) {
            dataTransacao = LocalDateTime.now();
        }
    }
}