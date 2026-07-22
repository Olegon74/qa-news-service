import axios from 'axios';
import { useEffect, useState } from 'react';
import {
  archiveNews,
  createNews,
  deleteNews,
  getAllNews,
  getNewsById,
  publishNews,
  updateNews
} from './api/newsApi';
import { clearAuthData, getRole, getUsername, isAuthenticated } from './auth/authStorage';
import { LoginForm } from './components/LoginForm';
import { NewsDetails } from './components/NewsDetails';
import { NewsForm } from './components/NewsForm';
import { NewsList } from './components/NewsList';
import { StatusFilter } from './components/StatusFilter';
import type { UserRole } from './types/auth';
import type { ErrorResponse, NewsRequest, NewsResponse, NewsStatus, NewsStatusFilter } from './types/news';

export default function App() {
  const [authenticated, setAuthenticated] = useState(isAuthenticated());
  const [username, setUsername] = useState(getUsername() ?? '');
  const [role, setRole] = useState<UserRole>((getRole() ?? 'VIEWER') as UserRole);
  const [news, setNews] = useState<NewsResponse[]>([]);
  const [selectedNews, setSelectedNews] = useState<NewsResponse | null>(null);
  const [editingNews, setEditingNews] = useState<NewsResponse | null>(null);
  const [statusFilter, setStatusFilter] = useState<NewsStatusFilter>('ALL');
  const [success, setSuccess] = useState('');
  const [error, setError] = useState('');
  const [authError, setAuthError] = useState('');

  useEffect(() => {
    if (authenticated) {
      void loadNews(statusFilter);
    }
  }, [authenticated, statusFilter]);

  function handleLogin(nextUsername: string, nextRole: UserRole) {
    setUsername(nextUsername);
    setRole(nextRole);
    setAuthenticated(true);
    setAuthError('');
    setError('');
  }

  function handleLogout() {
    clearAuthData();
    setAuthenticated(false);
    setNews([]);
    setSelectedNews(null);
    setEditingNews(null);
    setSuccess('');
    setError('');
    setAuthError('');
  }

  async function loadNews(filter: NewsStatusFilter) {
    await runApiAction(async () => {
      const status = filter === 'ALL' ? undefined : (filter as NewsStatus);
      const data = await getAllNews(status);
      setNews(data);
    });
  }

  async function handleCreate(request: NewsRequest) {
    await runApiAction(async () => {
      await createNews(request);
      setSuccess('Success: News created');
      await loadNews(statusFilter);
    });
  }

  async function handleUpdate(id: number, request: NewsRequest) {
    await runApiAction(async () => {
      await updateNews(id, request);
      setEditingNews(null);
      setSuccess('Success: News updated');
      await loadNews(statusFilter);
    });
  }

  async function handleView(id: number) {
    await runApiAction(async () => {
      const data = await getNewsById(id);
      setSelectedNews(data);
      setSuccess('');
    });
  }

  async function handlePublish(id: number) {
    await runApiAction(async () => {
      const data = await publishNews(id);
      setSelectedNews(data);
      setSuccess('Success: News published');
      await loadNews(statusFilter);
    });
  }

  async function handleArchive(id: number) {
    await runApiAction(async () => {
      const data = await archiveNews(id);
      setSelectedNews(data);
      setSuccess('Success: News archived');
      await loadNews(statusFilter);
    });
  }

  async function handleDelete(id: number) {
    await runApiAction(async () => {
      await deleteNews(id);
      setSelectedNews((current) => (current?.id === id ? null : current));
      setEditingNews((current) => (current?.id === id ? null : current));
      setSuccess('Success: News deleted');
      await loadNews(statusFilter);
    });
  }

  async function runApiAction(action: () => Promise<void>) {
    setError('');
    try {
      await action();
    } catch (apiError) {
      const message = getApiErrorMessage(apiError);
      if (message === 'Error: Unauthorized') {
        clearAuthData();
        setAuthenticated(false);
        setAuthError(message);
      } else {
        setError(message);
      }
    }
  }

  if (!authenticated) {
    return <LoginForm onLogin={handleLogin} externalError={authError} />;
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <div>
          <h1>QA News Service</h1>
          <p>
            <span data-testid="current-user">{username}</span> · <span data-testid="current-role">{role}</span>
          </p>
        </div>
        <button data-testid="logout-button" type="button" onClick={handleLogout}>
          Logout
        </button>
      </header>

      {success && (
        <div className="message success" data-testid="success-message">
          {success}
        </div>
      )}
      {error && (
        <div className="message error" data-testid="error-message">
          {error}
        </div>
      )}

      {role === 'ADMIN' ? (
        <section className="panel">
          <h2>{editingNews ? 'Edit news' : 'Create news'}</h2>
          <NewsForm
            editingNews={editingNews}
            onCreate={handleCreate}
            onUpdate={handleUpdate}
            onCancelEdit={() => setEditingNews(null)}
          />
        </section>
      ) : (
        <div className="message info" data-testid="viewer-readonly-message">
          You have VIEWER role. Only news viewing is available.
        </div>
      )}

      <StatusFilter value={statusFilter} onChange={setStatusFilter} />
      <NewsDetails news={selectedNews} />
      <NewsList
        news={news}
        role={role}
        onView={handleView}
        onEdit={(item) => {
          setEditingNews(item);
          setSelectedNews(item);
          setSuccess('');
          setError('');
        }}
        onPublish={handlePublish}
        onArchive={handleArchive}
        onDelete={handleDelete}
      />
    </div>
  );
}

function getApiErrorMessage(apiError: unknown): string {
  if (!axios.isAxiosError<ErrorResponse>(apiError)) {
    return 'Error: Unexpected error';
  }

  const status = apiError.response?.status;
  if (status === 401) {
    return 'Error: Unauthorized';
  }
  if (status === 403) {
    return 'Error: Forbidden';
  }

  const validationErrors = apiError.response?.data?.validationErrors;
  if (validationErrors && Object.keys(validationErrors).length > 0) {
    return `Error: ${Object.entries(validationErrors)
      .map(([field, message]) => `${field}: ${message}`)
      .join(', ')}`;
  }

  return `Error: ${apiError.response?.data?.message ?? apiError.message}`;
}
