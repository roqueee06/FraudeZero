import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './Componentes/Login/Login';
import Cadastro from './Componentes/Cadastro/Cadastro';
import Dashboard from './Componentes/Dashboard/Dashboard';
import NovaCompra from './Componentes/NovaCompra/NovaCompra'; // ← CORRIGIDO

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/cadastro" element={<Cadastro />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/nova-compra" element={<NovaCompra />} />
        <Route path="/" element={<Login />} />
      </Routes>
    </Router>
  );
}

export default App;