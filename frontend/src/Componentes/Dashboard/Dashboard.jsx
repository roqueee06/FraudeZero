import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

// Import dos ícones
import pixIcon from "../../assets/pix.png";
import transIcon from "../../assets/trans.png";
import extratoIcon from "../../assets/extrato.png";

function Dashboard() {
  const navigate = useNavigate();
  const [usuario, setUsuario] = useState(null);
  const [transacoesSuspeitas, setTransacoesSuspeitas] = useState([]);

  useEffect(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    if (usuarioSalvo) {
      const user = JSON.parse(usuarioSalvo);
      setUsuario(user);
      // Buscar transações suspeitas do usuário
      buscarTransacoesSuspeitas(user.id);
    }
  }, []);

  const buscarTransacoesSuspeitas = async (userId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suspeitas/usuario/${userId}`);
      if (response.ok) {
        const transacoes = await response.json();
        setTransacoesSuspeitas(transacoes);
      }
    } catch (error) {
      console.error("Erro ao buscar transações suspeitas:", error);
    }
  };

  const handleAprovar = async (idCompra) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suspeitas/aprovar/${idCompra}`, {
        method: "POST"
      });
      if (response.ok) {
        // Remove a transação da lista
        setTransacoesSuspeitas(prev => prev.filter(t => t.id_compra !== idCompra));
        alert("Transação aprovada com sucesso!");
      }
    } catch (error) {
      console.error("Erro ao aprovar transação:", error);
    }
  };

  const handleContestar = async (idCompra) => {
    try {
      const response = await fetch(`http://localhost:8080/api/suspeitas/contestar/${idCompra}`, {
        method: "POST"
      });
      if (response.ok) {
        // Remove a transação da lista
        setTransacoesSuspeitas(prev => prev.filter(t => t.id_compra !== idCompra));
        alert("Transação contestada! Nossa equipe irá analisar.");
      }
    } catch (error) {
      console.error("Erro ao contestar transação:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("usuario");
    navigate("/login");
  };

  return (
    <div className="dashboard">
      {/* Header */}
      <header className="header">
        <div className="header-content">
          <h1>Bradesco</h1>
          <div className="user-info">
            <span>Olá, {usuario?.nome || "Usuário"}</span>
            <button onClick={handleLogout} className="logout-btn">Sair</button>
          </div>
        </div>
      </header>

      {/* Conteúdo Principal */}
      <main className="main">
        {/* Cards */}
        <div className="cards">
          <div className="card">
            <h3>Saldo Disponível</h3>
            <p className="valor">R$ 2.450,00</p>
            <span>Conta Corrente</span>
          </div>

          <div className="card">
            <h3>Cartão de Crédito</h3>
            <p className="valor">R$ 3.250,00</p>
            <span>Limite disponível</span>
          </div>
        </div>

        {/* Transações Suspeitas */}
        <div className="suspeitas-section">
          <h2>⚠️ Transações Suspeitas</h2>
          {transacoesSuspeitas.length === 0 ? (
            <div className="sem-suspeitas">
              <p>Nenhuma transação suspeita no momento</p>
            </div>
          ) : (
            <div className="transacoes-lista">
              {transacoesSuspeitas.map((transacao) => (
                <div key={transacao.id_compra} className="transacao-item">
                  <div className="transacao-info">
                    <h4>Compra Suspeita #{transacao.id_compra}</h4>
                    <p><strong>Loja:</strong> {transacao.nome_loja || "Loja não identificada"}</p>
                    <p><strong>Valor:</strong> R$ {transacao.preco_compra}</p>
                    <p><strong>Data:</strong> {new Date(transacao.data_transacao).toLocaleDateString()}</p>
                    <p><strong>Distância:</strong> {transacao.distancia_da_casa} km</p>
                  </div>
                  <div className="transacao-acoes">
                    <button 
                      onClick={() => handleAprovar(transacao.id_compra)}
                      className="btn-aprovar"
                    >
                      ✅ Aprovar
                    </button>
                    <button 
                      onClick={() => handleContestar(transacao.id_compra)}
                      className="btn-contestar"
                    >
                      🚫 Contestar
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Ações Rápidas */}
        <div className="acoes">
          <h2>Ações Rápidas</h2>
          <div className="botoes-acoes">
            <button className="botao-acao">
              <img src={pixIcon} alt="PIX" className="acao-icone" />
              <span className="acao-texto">Pagar com PIX</span>
            </button>
            <button className="botao-acao">
              <img src={transIcon} alt="Transferir" className="acao-icone" />
              <span className="acao-texto">Transferir</span>
            </button>
            <button className="botao-acao">
              <img src={extratoIcon} alt="Extrato" className="acao-icone" />
              <span className="acao-texto">Extrato</span>
            </button>
            <button 
              className="botao-acao"
              onClick={() => navigate("/nova-compra")}
            >
              <span className="acao-icone">🛒</span>
              <span className="acao-texto">Nova Compra</span>
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}

export default Dashboard;