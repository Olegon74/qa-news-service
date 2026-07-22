import { FormEvent, useState } from 'react';
import axios from 'axios';
import { login } from '../api/authApi';
import { saveAuthData } from '../auth/authStorage';
import type { UserRole } from '../types/auth';
import type { ErrorResponse } from '../types/news';

interface LoginFormProps {
  onLogin: (username: string, role: UserRole) => void;
  externalError?: string;
}

export function LoginForm({ onLogin, externalError }: LoginFormProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await login(username, password);
      saveAuthData(response.token, response.username, response.role);
      onLogin(response.username, response.role);
    } catch (apiError) {
      if (axios.isAxiosError<ErrorResponse>(apiError) && apiError.response?.status === 401) {
        setError('Error: Unauthorized');
      } else if (axios.isAxiosError<ErrorResponse>(apiError)) {
        setError(`Error: ${apiError.response?.data?.message ?? apiError.message}`);
      } else {
        setError('Error: Login failed');
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="login-page">
      <form className="login-form" data-testid="login-form" onSubmit={handleSubmit}>
        <h1>QA News Service</h1>
        <label htmlFor="username">Username</label>
        <input
          id="username"
          data-testid="username-input"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          autoComplete="username"
        />

        <label htmlFor="password">Password</label>
        <input
          id="password"
          data-testid="password-input"
          type="password"
          value={password}
          onChange={(event) => setPassword(event.target.value)}
          autoComplete="current-password"
        />

        <button data-testid="login-button" type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>

        {(error || externalError) && (
          <div className="message error" data-testid="auth-error-message">
            {error || externalError}
          </div>
        )}
      </form>
    </main>
  );
}
