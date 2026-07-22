import type { UserRole } from '../types/auth';
import type { NewsResponse } from '../types/news';

interface NewsCardProps {
  news: NewsResponse;
  role: UserRole;
  onView: (id: number) => void;
  onEdit: (news: NewsResponse) => void;
  onPublish: (id: number) => void;
  onArchive: (id: number) => void;
  onDelete: (id: number) => void;
}

export function NewsCard({ news, role, onView, onEdit, onPublish, onArchive, onDelete }: NewsCardProps) {
  const isAdmin = role === 'ADMIN';
  const canEdit = isAdmin && (news.status === 'DRAFT' || news.status === 'PUBLISHED');
  const canPublish = isAdmin && news.status === 'DRAFT';
  const canArchive = isAdmin && news.status === 'PUBLISHED';
  const canDelete = isAdmin && (news.status === 'DRAFT' || news.status === 'ARCHIVED');

  return (
    <article className="news-card" data-testid={`news-card-${news.id}`}>
      <div className="card-header">
        <div>
          <p className="muted">#{news.id}</p>
          <h3 data-testid={`news-title-${news.id}`}>{news.title}</h3>
        </div>
        <span className={`status status-${news.status.toLowerCase()}`} data-testid={`news-status-${news.id}`}>
          {news.status}
        </span>
      </div>

      <p>{news.previewText}</p>
      <dl className="meta-grid">
        <div>
          <dt>Author</dt>
          <dd>{news.author}</dd>
        </div>
        <div>
          <dt>Created</dt>
          <dd>{formatDate(news.createdAt)}</dd>
        </div>
        <div>
          <dt>Updated</dt>
          <dd>{formatDate(news.updatedAt)}</dd>
        </div>
        <div>
          <dt>Published</dt>
          <dd>{formatDate(news.publishedAt)}</dd>
        </div>
        <div>
          <dt>Archived</dt>
          <dd>{formatDate(news.archivedAt)}</dd>
        </div>
      </dl>

      <div className="button-row">
        <button data-testid={`view-news-button-${news.id}`} type="button" onClick={() => onView(news.id)}>
          View
        </button>
        {canEdit && (
          <button data-testid={`edit-news-button-${news.id}`} type="button" onClick={() => onEdit(news)}>
            Edit
          </button>
        )}
        {canPublish && (
          <button data-testid={`publish-news-button-${news.id}`} type="button" onClick={() => onPublish(news.id)}>
            Publish
          </button>
        )}
        {canArchive && (
          <button data-testid={`archive-news-button-${news.id}`} type="button" onClick={() => onArchive(news.id)}>
            Archive
          </button>
        )}
        {canDelete && (
          <button data-testid={`delete-news-button-${news.id}`} type="button" className="danger" onClick={() => onDelete(news.id)}>
            Delete
          </button>
        )}
      </div>
    </article>
  );
}

function formatDate(value: string | null): string {
  return value ? new Date(value).toLocaleString() : '-';
}
