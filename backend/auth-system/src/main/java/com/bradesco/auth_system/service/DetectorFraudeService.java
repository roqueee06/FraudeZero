package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.repository.CompraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetectorFraudeService {

    @Autowired
    private CompraRepository compraRepository;

    public List<Suspeita> detectarFraudes(Compra compra) {
        List<Suspeita> suspeitas = new ArrayList<>();

        // Buscar histórico do usuário para calcular média
        List<Compra> historicoUsuario = compraRepository.findByIdUsuario(compra.getIdUsuario());
        
        // REGRA 1: Preço acima de 6x a média do usuário
        if (isPrecoAcimaMedia(compra, historicoUsuario)) {
            Suspeita suspeita = criarSuspeita(compra, 
                "Preço Acima do Padrão do Usuário", 
                "Valor 6x maior que a média histórica",
                compra.getPedidoOnline() ? "Compra Online" : "Compra Presencial");
            suspeitas.add(suspeita);
        }

        // REGRA 2: Distância suspeita (20% da distância da casa) - SÓ SE DISTÂNCIA CASA > 60km
        if (isDistanciaSuspeita(compra)) {
            Suspeita suspeita = criarSuspeita(compra,
                "Distância Suspeita da Casa",
                "Distância da última compra > 20% da distância da casa (acima de 60km)",
                compra.getPedidoOnline() ? "Compra Online" : "Compra Presencial");
            suspeitas.add(suspeita);
        }

        // REGRA 3: Compra Online sem autenticação + múltiplos fatores
        if (isCompraOnlineSuspeita(compra, historicoUsuario)) {
            Suspeita suspeita = criarSuspeita(compra,
                "Compra Online Suspeita",
                "Sem autenticação + múltiplos fatores de risco",
                "Compra Online");
            suspeitas.add(suspeita);
        }

        return suspeitas;
    }

    private boolean isPrecoAcimaMedia(Compra compra, List<Compra> historicoUsuario) {
        if (historicoUsuario.isEmpty()) {
            return false; // Não tem histórico para comparar
        }

        // Calcular média do histórico (excluindo a compra atual)
        BigDecimal soma = BigDecimal.ZERO;
        int count = 0;
        
        for (Compra c : historicoUsuario) {
            if (!c.getId().equals(compra.getId())) { // Exclui a compra atual se já estiver no banco
                soma = soma.add(c.getPrecoCompra());
                count++;
            }
        }

        if (count == 0) return false;

        BigDecimal media = soma.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
        BigDecimal limite = media.multiply(new BigDecimal("6"));

        return compra.getPrecoCompra().compareTo(limite) > 0;
    }

    private boolean isDistanciaSuspeita(Compra compra) {
        if (compra.getDistanciaDaCasa() == null || 
            compra.getDistanciaDaUltimaTransacao() == null) {
            return false;
        }

        // ✅ NOVA CONDIÇÃO: Só aplica se distância da casa for maior que 60km
        if (compra.getDistanciaDaCasa().compareTo(new BigDecimal("60")) <= 0) {
            return false;
        }

        // Calcular 20% da distância da casa
        BigDecimal limiteDistancia = compra.getDistanciaDaCasa()
            .multiply(new BigDecimal("0.2"));

        // Verificar se a distância da última transação é maior que 20% da distância da casa
        return compra.getDistanciaDaUltimaTransacao().compareTo(limiteDistancia) > 0;
    }

    private boolean isCompraOnlineSuspeita(Compra compra, List<Compra> historicoUsuario) {
        // Só aplica para compras online sem senha
        if (!compra.getPedidoOnline() || compra.getUsouSenha()) {
            return false;
        }

        int fatoresRisco = 0;

        // Fator 1: Preço acima da média
        if (isPrecoAcimaMedia(compra, historicoUsuario)) {
            fatoresRisco++;
        }

        // Fator 2: Distância suspeita (já inclui a verificação de >60km)
        if (isDistanciaSuspeita(compra)) {
            fatoresRisco++;
        }

        // Fator 3: Distância da casa > 60km
        if (compra.getDistanciaDaCasa() != null && 
            compra.getDistanciaDaCasa().compareTo(new BigDecimal("60")) > 0) {
            fatoresRisco++; // Distância da casa > 60km
        }

        // Fator 4: Valor absoluto muito alto
        if (compra.getPrecoCompra().compareTo(new BigDecimal("1000")) > 0) {
            fatoresRisco++;
        }

        return fatoresRisco >= 2;
    }

    private Suspeita criarSuspeita(Compra compra, String tipoFraude, String condicao, String modoPagamento) {
        Suspeita suspeita = new Suspeita();
        suspeita.setIdUsuario(compra.getIdUsuario());
        suspeita.setIdCompra(compra.getId());
        suspeita.setTipoFraude(tipoFraude);
        suspeita.setCondicao(condicao);
        suspeita.setModoPagamento(modoPagamento);
        return suspeita;
    }
}