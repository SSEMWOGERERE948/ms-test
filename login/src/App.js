import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function App() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [token, setToken] = useState(null);
  const [jwtResults, setJwtResults] = useState([]);
  const [isLogin, setIsLogin] = useState(true); 
 
  // Login handler to authenticate and retrieve the token
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/auth/authenticate', {
        username,
        password
      });
      console.log(response.data.token);
      setToken(response.data.token); // Store the JWT token
      alert('Login successful!');
    } catch (error) {
      console.error('Login failed', error);
      alert('Login failed. Please try again or register.');
    }
  };

  // Register handler to create a new user account
  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:8080/auth/register', {
        username,
        password 
           });
      alert('Registration successful! Please log in.');
      setIsLogin(true); // Switch to login form after successful registration
    } catch (error) {
      console.error('Registration failed', error);
      alert('Registration failed. Please try again.');
    }
  };

  // Fetch student results once the user is authenticated
  useEffect(() => {
    const fetchStudentResults = async () => {
      if (token) {
        try {
          const resultResponse = await axios.get('http://localhost:8080/alevel/results', {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          setJwtResults(resultResponse.data);
        } catch (error) {
          console.error('Error fetching student results', error);
        }
      }
    };
    fetchStudentResults();
  }, [token]);

  return (
    <div className="App">
      {!token ? (
        isLogin ? (
          // Login form
          <form onSubmit={handleLogin}>
            <h2>Login</h2>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
            <button type="submit">Login</button>
            <p>
              Don't have an account?{' '}
              <span onClick={() => setIsLogin(false)} style={{ color: 'blue', cursor: 'pointer' }}>
                Register here
              </span>
            </p>
          </form>
        ) : (
          // Registration form
          <form onSubmit={handleRegister}>
            <h2>Register</h2>
            <input
              type="text"
              placeholder="Username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
            <input
              type="password"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />

            <button type="submit">Register</button>
            <p>
              Already have an account?{' '}
              <span onClick={() => setIsLogin(true)} style={{ color: 'blue', cursor: 'pointer' }}>
                Login here
              </span>
            </p>
          </form>
        )
      ) : (
        <div>
          <h2>This is a jwt token authentication login process </h2>
        </div>
      )}
    </div>
  );
}

export default App;
