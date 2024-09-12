import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import remoteService from '../../remoteService';

function Login({ setToken }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [isLogin, setIsLogin] = useState(true);
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    const requestData = { username, password };

    try {
      await remoteService.sendRequestToServer(
        'auth',   
        'authenticate',
        requestData,
        true,
        (response) => {
          setToken(response.token);  
          alert('Login successful!');
          navigate('/results');
        },
        (error) => {
          console.error('Login failed', error);
          alert('Login failed. Please try again.');
        }
      );
    } catch (error) {
      console.error('Login error', error);
      alert('An error occurred. Please try again.');
    }
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    const requestData = { username, password };

    try {
      await remoteService.sendRequestToServer(
        'auth',  
        'register',
        requestData,
        true,
        (response) => {
          alert('Registration successful!');
          setIsLogin(true);
        },
        (error) => {
          console.error('Registration failed', error);
          alert('Registration failed. Please try again.');
        }
      );
    } catch (error) {
      console.error('Registration error', error);
      alert('An error occurred. Please try again.');
    }
  };

  return (
    <div>
      <form onSubmit={isLogin ? handleLogin : handleRegister}>
        <h2>{isLogin ? 'Login' : 'Register'}</h2>
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
        <button type="submit">{isLogin ? 'Login' : 'Register'}</button>
        <p>
          {isLogin ? (
            <>
              Don't have an account?{' '}
              <span onClick={() => setIsLogin(false)} style={{ color: 'blue', cursor: 'pointer' }}>
                Register here
              </span>
            </>
          ) : (
            <>
              Already have an account?{' '}
              <span onClick={() => setIsLogin(true)} style={{ color: 'blue', cursor: 'pointer' }}>
                Login here
              </span>
            </>
          )}
        </p>
      </form>
    </div>
  );
}

export default Login;
