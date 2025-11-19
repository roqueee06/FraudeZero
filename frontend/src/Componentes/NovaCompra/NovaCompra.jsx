import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./NovaCompra.css";

function NovaCompra() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  
  const [compra, setCompra] = useState({
    distanciaDaCasa: "",
    distanciaDaUltimaTransacao: "",
    precoCompra: "",
    idLoja: "",
    usouChip: false,
    usouSenha: false,
    pedidoOnline: false
  });

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setCompra(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const usuario = JSON.parse(localStorage.getItem("usuario"));
      
      // DADOS CORRETOS PARA O BACKEND - COM OS MESMOS NOMES DA ENTIDADE
      const compraComUsuario = {
        distanciaDaCasa: compra.distanciaDaCasa ? Number(compra.distanciaDaCasa) : null,
        distanciaDaUltimaTransacao: compra.distanciaDaUltimaTransacao ? Number(compra.distanciaDaUltimaTransacao) : null,
        precoCompra: Number(compra.precoCompra),
        idLoja: Number(compra.idLoja),
        idUsuario: usuario.id,
        usouChip: compra.usouChip,
        usouSenha: compra.usouSenha,
        pedidoOnline: compra.pedidoOnline
      };

      console.log("ENVIANDO PARA BACKEND:", compraComUsuario);

      const response = await fetch("http://localhost:8080/api/compras", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(compraComUsuario),
      });

      const responseText = await response.text();
      console.log("RESPOSTA DO BACKEND:", responseText);

      if (response.ok) {
        alert("Compra registrada com sucesso!");
        navigate("/dashboard");
      } else {
        alert(`Erro: ${responseText}`);
      }
    } catch (error) {
      console.error("Erro:", error);
      alert("Erro de conexão");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="nova-compra-page">
      <div className="container">
        <form onSubmit={handleSubmit}>
          <h1>Simular Nova Compra</h1>
          
          <div className="form-group">
            <label>Distância da Casa (km):</label>
            <input
              type="number"
              step="0.01"
              name="distanciaDaCasa"
              value={compra.distanciaDaCasa}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Distância da Última Transação (km):</label>
            <input
              type="number"
              step="0.01"
              name="distanciaDaUltimaTransacao"
              value={compra.distanciaDaUltimaTransacao}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>Preço da Compra (R$):</label>
            <input
              type="number"
              step="0.01"
              name="precoCompra"
              value={compra.precoCompra}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label>ID da Loja:</label>
            <input
              type="number"
              name="idLoja"
              value={compra.idLoja}
              onChange={handleChange}
              required
            />
          </div>

          <div className="checkboxes">
            <label className="checkbox-label">
              <input
                type="checkbox"
                name="usouChip"
                checked={compra.usouChip}
                onChange={handleChange}
              />
              Usou Chip
            </label>

            <label className="checkbox-label">
              <input
                type="checkbox"
                name="usouSenha"
                checked={compra.usouSenha}
                onChange={handleChange}
              />
              Usou Senha
            </label>

            <label className="checkbox-label">
              <input
                type="checkbox"
                name="pedidoOnline"
                checked={compra.pedidoOnline}
                onChange={handleChange}
              />
              Pedido Online
            </label>
          </div>

          <div className="botoes">
            <button type="submit" disabled={loading}>
              {loading ? "Processando..." : "Registrar Compra"}
            </button>
            <button type="button" onClick={() => navigate("/dashboard")} className="btn-voltar">
              Voltar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default NovaCompra;