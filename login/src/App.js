import React, { useState } from 'react';
import './App.css';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './Screens/Login/Login';
import Result from './Screens/Result/Result';
import FileUpload from './Screens/Upload/FileUpload';
import DocumentEditor from './Screens/Upload/DocumentEditor';
import WordDocumentViewer from './Screens/Upload/WordDocumentViewer';

function App() {
  const [token, setToken] = useState(localStorage.getItem('token'));

  return (
    <Router>
      <div className="App">
        <Routes>
          <Route path="/" element={token ? <Navigate to="/upload" /> : <Login setToken={setToken} />} />
          <Route path="/results" element={token ? <Result token={token} /> : <Navigate to="/" />} />
          <Route path="/upload" element={token ? <FileUpload token={token} /> : <Navigate to="/" />} />
          {/* <Route path="/edit/:documentId" element={token ? <WordDocumentViewer token={token} /> : <Navigate to="/" />} /> */}
        </Routes>
      </div>
    </Router>
  );
}

export default App;
