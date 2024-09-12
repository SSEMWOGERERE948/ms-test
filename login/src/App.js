import React, { useState } from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Screens/Login/Login';
import Result from './Screens/Result/Result';

function App() {
  const [token, setToken] = useState(null);

  return (
    <Router>
      <div className="App">
        <Routes>
          {/* Redirect to login if no token */}
          <Route path="/" element={token ? <Navigate to="/results" /> : <Login setToken={setToken} />} />
          
          {/* Results page */}
          <Route path="/results" element={token ? <Result token={token} /> : <Navigate to="/" />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
