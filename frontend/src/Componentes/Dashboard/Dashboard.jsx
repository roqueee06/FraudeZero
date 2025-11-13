import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./Dashboard.css";

import pixIcon from "../../assets/pix.png";
import transIcon from "../../assets/trans.png";
import extratoIcon from "../../assets/extrato.png";

function Dashboard() {
  const navigate = useNavigate();
  const [usuario, setUsuario] = useState(null);
  const [saldoDisponivel, setSaldoDisponivel] = useState(0);
  const [transacoesSuspeitas, setTransacoesSuspeitas] = useState([]);

  useEffect(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    if (usuarioSalvo) {
      const user = JSON.parse(usuarioSalvo);
      console.log("Usuário no Dashboard:", user);
      
      let usuarioId;
      let usuarioNome;
      
      if (user.usuario) {
        usuarioId = user.usuario.id;
        usuarioNome = user.usuario.nome;
      } else {
        usuarioId = user.id;
        usuarioNome = user.nome;
      }
      
      console.log("ID:", usuarioId, "Nome:", usuarioNome);
      
      if (user.role === 'ADMIN') {
        navigate("/admin");
        return;
      }
      
      setUsuario({ ...user, id: usuarioId, nome: usuarioNome });
      buscarSaldoDisponivel(usuarioId);
      buscarTransacoesSuspeitas(usuarioId);
    } else {
      navigate("/login");
    }
  }, [navigate]);

  const buscarSaldoDisponivel = async (userId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/usuarios/${userId}/saldo`);
      if (response.ok) {
        const saldo = await response.json();
        setSaldoDisponivel(saldo);
      } else {
        setSaldoDisponivel(50000.00);
      }
    } catch (error) {
      console.error("Erro ao buscar saldo:", error);
      setSaldoDisponivel(50000.00);
    }
  };

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

  const aprovarCompra = async (idCompra) => {
    try {
      const response = await fetch(`http://localhost:8080/api/extrato/aprovar/${idCompra}`, {
        method: 'POST'
      });
      
      if (response.ok) {
        alert('Compra aprovada! Saldo atualizado.');
        buscarTransacoesSuspeitas(usuario.id);
        buscarSaldoDisponivel(usuario.id);
      } else {
        const erro = await response.text();
        alert(`Erro: ${erro}`);
      }
    } catch (error) {
      console.error('Erro ao aprovar compra:', error);
      alert('Erro ao aprovar compra');
    }
  };

  const contestarCompra = async (idCompra) => {
    try {
      const response = await fetch(`http://localhost:8080/api/extrato/contestar/${idCompra}`, {
        method: 'POST'
      });
      
      if (response.ok) {
        alert('Compra contestada! Transação removida.');
        buscarTransacoesSuspeitas(usuario.id);
      } else {
        const erro = await response.text();
        alert(`Erro: ${erro}`);
      }
    } catch (error) {
      console.error('Erro ao contestar compra:', error);
      alert('Erro ao contestar compra');
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("usuario");
    navigate("/login");
  };

  const formatarValor = (valor) => {
    return valor.toLocaleString('pt-BR', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
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
          {/* ✅ SALDO REAL - NÃO MAIS VALOR FIXO */}
          <div className="card saldo-card">
            <h3>Saldo Disponível</h3>
            <p className="valor">R$ {formatarValor(saldoDisponivel)}</p>
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
              <p>Nenhuma transação suspeita no momento 🎉</p>
              <p className="subtitulo">Todas as suas transações estão seguras</p>
            </div>
          ) : (
            <div className="transacoes-lista">
              {transacoesSuspeitas.map((transacao) => (
                <div key={transacao.id || transacao.id_compra} className="transacao-item">
                  <div className="transacao-info">
                    <h4>Compra Suspeita #{transacao.idCompra || transacao.id_compra}</h4>
                    <p><strong>Motivo:</strong> {transacao.tipoFraude || "Transação suspeita"}</p>
                    <p><strong>Valor:</strong> R$ {formatarValor(transacao.valorCompra || transacao.preco_compra || 0)}</p>
                    <p><strong>Data:</strong> {new Date(transacao.dataTransacao || transacao.data_transacao).toLocaleDateString()}</p>
                    <p><strong>Condição:</strong> {transacao.condicao || "Análise de risco"}</p>
                  </div>
                  <div className="transacao-acoes">
                    <button 
                      onClick={() => aprovarCompra(transacao.idCompra || transacao.id_compra)}
                      className="btn-aprovar"
                    >
                      ✅ Aprovar
                    </button>
                    <button 
                      onClick={() => contestarCompra(transacao.idCompra || transacao.id_compra)}
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