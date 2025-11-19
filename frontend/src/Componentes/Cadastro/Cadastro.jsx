import { FaUser, FaLock, FaEnvelope, FaIdCard } from "react-icons/fa";
import { useState } from "react";
import "./Cadastro.css";
import { useNavigate } from "react-router-dom";

const Cadastro = () => {
  const navigate = useNavigate();

  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");
  const [senha, setSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [loading, setLoading] = useState(false);

  // Função para limpar CPF (remove pontos e traço)
  const limparCpf = (cpfFormatado) => {
    return cpfFormatado.replace(/\D/g, '');
  };

  // Função para formatar CPF na digitação
  const formatarCpf = (value) => {
    const numbers = value.replace(/\D/g, '');
    if (numbers.length <= 11) {
      return numbers.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
    }
    return numbers.substring(0, 14);
  };

  const handleCpfChange = (e) => {
    const formattedCpf = formatarCpf(e.target.value);
    setCpf(formattedCpf);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setLoading(true);

    // Validações
    if (senha !== confirmarSenha) {
      alert("As senhas não coincidem!");
      setLoading(false);
      return;
    }

    if (senha.length < 6) {
      alert("A senha deve ter pelo menos 6 caracteres!");
      setLoading(false);
      return;
    }

    const cpfLimpo = limparCpf(cpf);
    
    console.log("Dados de Cadastro:", { nome, cpf: cpfLimpo, senha });

    try {
      const response = await fetch("http://localhost:8080/api/auth/registrar", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          nome: nome,
          cpf: cpfLimpo,
          senha: senha
        }),
      });

      if (response.ok) {
        const usuario = await response.json();
        console.log("Cadastro bem-sucedido:", usuario);
        alert("Cadastro realizado com sucesso! Faça login para continuar.");
        navigate("/login");
      } else {
        const erro = await response.text();
        alert(`Erro no cadastro: ${erro}`);
      }
    } catch (error) {
      console.error("Erro na requisição:", error);
      alert("Erro de conexão com o servidor");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="cadastro-page">
      <div className="container">
        <form onSubmit={handleSubmit}>
          <h1>Criar Conta</h1>

          <div className="input-field">
            <input
              type="text"
              placeholder="Nome completo"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
            <FaUser className="icon" />
          </div>

          <div className="input-field">
            <input
              type="text"
              placeholder="CPF"
              value={cpf}
              onChange={handleCpfChange}
              maxLength="14"
              required
            />
            <FaIdCard className="icon" />
          </div>

          <div className="input-field">
            <input
              type="password"
              placeholder="Senha (mínimo 6 caracteres)"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              minLength="6"
              required
            />
            <FaLock className="icon" />
          </div>

          <div className="input-field">
            <input
              type="password"
              placeholder="Confirmar senha"
              value={confirmarSenha}
              onChange={(e) => setConfirmarSenha(e.target.value)}
              required
            />
            <FaLock className="icon" />
          </div>

          <button type="submit" disabled={loading}>
            {loading ? "Criando conta..." : "Criar Conta"}
          </button>

          <div className="login-link">
            <p>
              Já tem uma conta? <a href="/login">Faça login</a>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Cadastro;