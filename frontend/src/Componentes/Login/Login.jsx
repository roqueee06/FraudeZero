import { FaUser, FaLock } from "react-icons/fa";
import { useState } from "react";
import "./Login.css";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const navigate = useNavigate();

  const [cpf, setCpf] = useState("");
  const [senha, setSenha] = useState("");
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

    // LIMPA O CPF ANTES DE ENVIAR
    const cpfLimpo = limparCpf(cpf);
    
    console.log("Dados de Login:", { cpf: cpfLimpo, senha });

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          cpf: cpfLimpo,  // ENVIA CPF LIMPO
          senha: senha
        }),
      });

      if (response.ok) {
        const usuario = await response.json();
        console.log("Login bem-sucedido:", usuario);
        
        localStorage.setItem("usuario", JSON.stringify(usuario));
        navigate("/dashboard");
      } else {
        const erro = await response.text();
        alert(`Erro no login: ${erro}`);
      }
    } catch (error) {
      console.error("Erro na requisição:", error);
      alert("Erro de conexão com o servidor");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-page">
      <div className="container">
        <form onSubmit={handleSubmit}>
          <h1>Acesso à conta</h1>

          <div className="input-field">
            <input
              type="text"
              placeholder="CPF"
              value={cpf}
              onChange={handleCpfChange}
              maxLength="14"
            />
            <FaUser className="icon" />
          </div>

          <div className="input-field">
            <input
              type="password"
              placeholder="Digite sua senha"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
            />
            <FaLock className="icon" />
          </div>

          <div className="recall-forget">
            <label>
              <input type="checkbox" />
              Lembrar do CPF
            </label>
            <a href="#">Esqueceu sua Senha?</a>
          </div>

          <button type="submit" disabled={loading}>
            {loading ? "Entrando..." : "Entrar"}
          </button>

          <div className="signup-link">
            <p>
              Não tem uma Conta? <a href="/cadastro">Crie agora mesmo!</a>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;