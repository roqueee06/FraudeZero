import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminDashboard.css";

function AdminDashboard() {
  const navigate = useNavigate();
  const [admin, setAdmin] = useState(null);
  const [dashboardData, setDashboardData] = useState(null);
  const [relatorioFraudes, setRelatorioFraudes] = useState([]);
  const [activeTab, setActiveTab] = useState("dashboard");

  useEffect(() => {
    const usuarioSalvo = localStorage.getItem("usuario");
    if (usuarioSalvo) {
      const user = JSON.parse(usuarioSalvo);
      if (user.role === "ADMIN") {
        setAdmin(user);
        carregarDashboard();
      } else {
        alert("Acesso negado! Área restrita para administradores.");
        navigate("/dashboard");
      }
    } else {
      navigate("/login");
    }
  }, [navigate]);

  const carregarDashboard = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/admin/dashboard");
      if (response.ok) {
        const data = await response.json();
        setDashboardData(data);
      }
    } catch (error) {
      console.error("Erro ao carregar dashboard:", error);
    }
  };

  const carregarRelatorioFraudes = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/admin/relatorio-fraudes");
      if (response.ok) {
        const data = await response.json();
        setRelatorioFraudes(data);
      }
    } catch (error) {
      console.error("Erro ao carregar relatório:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("usuario");
    navigate("/login");
  };

  const calcularTaxaFraude = () => {
    if (!dashboardData) return 0;
    
    const totalCompras = dashboardData.totalCompras || 0;
    const fraudesConfirmadas = dashboardData.fraudesConfirmadas || 0;
    
    if (totalCompras === 0) return 0;
    
    return (fraudesConfirmadas / totalCompras) * 100;
  };

  if (!admin) return <div>Carregando...</div>;

  return (
    <div className="admin-dashboard">
      <header className="admin-header">
        <div className="admin-header-content">
          <h1>🏢 Painel Administrativo - FraudeZero</h1>
          <div className="admin-user-info">
            <span>Administrador: {admin.nome}</span>
            <button onClick={handleLogout} className="btn-logout">Sair</button>
          </div>
        </div>
      </header>

      <nav className="admin-nav">
        <button 
          className={activeTab === "dashboard" ? "nav-btn active" : "nav-btn"}
          onClick={() => setActiveTab("dashboard")}
        >
          📊 Dashboard
        </button>
        <button 
          className={activeTab === "fraudes" ? "nav-btn active" : "nav-btn"}
          onClick={() => {
            setActiveTab("fraudes");
            carregarRelatorioFraudes();
          }}
        >
          🚨 Relatório de Fraudess
        </button>
        <button 
          className={activeTab === "usuarios" ? "nav-btn active" : "nav-btn"}
          onClick={() => setActiveTab("usuarios")}
        >
          👥 Usuários
        </button>
        <button 
          className={activeTab === "compras" ? "nav-btn active" : "nav-btn"}
          onClick={() => setActiveTab("compras")}
        >
          💳 Todas as Compras
        </button>
      </nav>

      <main className="admin-main">
        {activeTab === "dashboard" && dashboardData && (
          <div className="dashboard-cards">
            <div className="admin-card">
              <h3>Total de Usuários</h3>
              <div className="admin-card-value">{dashboardData.totalUsuarios || 0}</div>
              <p>Usuários cadastrados no sistema</p>
            </div>

            <div className="admin-card">
              <h3>Total de Compras</h3>
              <div className="admin-card-value">{dashboardData.totalCompras || 0}</div>
              <p>Transações realizadas</p>
            </div>


            <div className="admin-card suspeitas-card">
              <h3>Transações Suspeitas Pendentes</h3>
              <div className="admin-card-value">{dashboardData.totalSuspeitas || 0}</div>
              <p>Aguardando análise do usuário</p>
            </div>

            <div className="admin-card fraudes-card">
              <h3>Fraudess Confirmadas</h3>
              <div className="admin-card-value">{dashboardData.fraudesConfirmadas || 0}</div>
              <p>Transações contestadas pelos usuários</p>
            </div>

            <div className="admin-card taxa-card">
              <h3>Taxa de Fraude</h3>
              <div className="admin-card-value">
                {calcularTaxaFraude().toFixed(2)}%
              </div>
              <p>Fraudes confirmadas / Total de compras</p>
            </div>

            <div className="admin-card valor-card">
              <h3>Valor em Suspeitas</h3>
              <div className="admin-card-value">
                R$ {dashboardData.valorTotalSuspeitas?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || "0,00"}
              </div>
              <p>Valor total das transações suspeitas</p>
            </div>

            <div className="admin-card grafico-card">
              <h3>📈 Distribuição de Alertas</h3>
              <div className="grafico-tipos">
                <div className="tipo-item">
                  <span className="tipo-nome">🟡 Suspeitas Pendentes</span>
                  <span className="tipo-quantidade pendente">{dashboardData.totalSuspeitas || 0}</span>
                </div>
                <div className="tipo-item">
                  <span className="tipo-nome">🔴 Fraudess Confirmadas</span>
                  <span className="tipo-quantidade fraude">{dashboardData.fraudesConfirmadas || 0}</span>
                </div>
                <div className="tipo-item">
                  <span className="tipo-nome">🟢 Compras Normais</span>
                  <span className="tipo-quantidade normal">
                    {(dashboardData.totalCompras || 0) - (dashboardData.fraudesConfirmadas || 0) - (dashboardData.totalSuspeitas || 0)}
                  </span>
                </div>
              </div>
              <p className="legenda-grafico">Distribuição do status das transações</p>
            </div>
          </div>
        )}

        {activeTab === "fraudes" && (
          <div className="relatorio-section">
            <h2>🚨 Relatório Detalhado de Fraudess</h2>
            <div className="resumo-fraudes">
              <div className="resumo-item">
                <span className="resumo-label">Total de Fraudess:</span>
                <span className="resumo-valor">{relatorioFraudes.filter(f => f.status === 'CONTESTADA').length}</span>
              </div>
              <div className="resumo-item">
                <span className="resumo-label">Suspeitas Pendentes:</span>
                <span className="resumo-valor">{relatorioFraudes.filter(f => f.status === 'Pendente').length}</span>
              </div>
            </div>
            
            {relatorioFraudes.length === 0 ? (
              <div className="sem-dados">
                <p>📊 Nenhuma transação suspeita ou fraudulenta detectada</p>
                <p className="subtitulo">O sistema está funcionando com segurança</p>
              </div>
            ) : (
              <div className="tabela-fraudes">
                <table>
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Usuário</th>
                      <th>CPF</th>
                      <th>Tipo de Fraude</th>
                      <th>Valor</th>
                      <th>Data</th>
                      <th>Status</th>
                      <th>Tipo</th>
                    </tr>
                  </thead>
                  <tbody>
                    {relatorioFraudes.map((fraude) => (
                      <tr key={fraude.idSuspeita} className={fraude.status === 'CONTESTADA' ? 'linha-fraude' : 'linha-suspeita'}>
                        <td className="id-coluna">{fraude.idSuspeita}</td>
                        <td className="usuario-coluna">
                          <strong>{fraude.usuario}</strong>
                        </td>
                        <td className="cpf-coluna">{fraude.cpfUsuario}</td>
                        <td className="tipo-coluna">
                          <span className={`badge ${fraude.tipoFraude?.includes('Online') ? 'badge-online' : 'badge-presencial'}`}>
                            {fraude.tipoFraude || "Não especificado"}
                          </span>
                        </td>
                        <td className="valor-coluna">
                          <strong>R$ {fraude.valor?.toLocaleString('pt-BR', { minimumFractionDigits: 2 }) || "0,00"}</strong>
                        </td>
                        <td className="data-coluna">
                          {fraude.dataDeteccao ? new Date(fraude.dataDeteccao).toLocaleDateString('pt-BR') : "N/A"}
                        </td>
                        <td className="status-coluna">
                          <span className={`badge ${fraude.status === 'CONTESTADA' ? 'badge-fraude' : 'badge-pendente'}`}>
                            {fraude.status === 'CONTESTADA' ? 'FRAUDE CONFIRMADA' : 'PENDENTE'}
                          </span>
                        </td>
                        <td className="tipo-status-coluna">
                          {fraude.status === 'CONTESTADA' ? '🚫 Fraude' : '⚠️ Suspeita'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        )}

        {activeTab === "usuarios" && (
          <div className="usuarios-section">
            <h2>👥 Gerenciamento de Usuários</h2>
            <div className="sem-dados">
              <p>🛠️ Módulo em desenvolvimento</p>
              <p className="subtitulo">Em breve: Lista completa de usuários e gestão de contas</p>
            </div>
          </div>
        )}

        {activeTab === "compras" && (
          <div className="compras-section">
            <h2>💳 Todas as Transações do Sistema</h2>
            <div className="sem-dados">
              <p>📋 Módulo em desenvolvimento</p>
              <p className="subtitulo">Em breve: Histórico completo de todas as transações</p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default AdminDashboard;