import type { NewsResponse } from '../types/news';

interface NewsDetailsProps {
  news: NewsResponse | null;
}

export function NewsDetails({ news }: NewsDetailsProps) {
  if (!news) {
    return null;
  }

  return (
    <section className="details" data-testid="selected-news-details">
      <h2>{news.title}</h2>
      <p className="preview">{news.previewText}</p>
      <p>{news.fullText}</p>
      <dl className="meta-grid">
        <div>
          <dt>Author</dt>
          <dd>{news.author}</dd>
        </div>
        <div>
          <dt>Status</dt>
          <dd>{news.status}</dd>
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
    </section>
  );
}

function formatDate(value: string | null): string {
  return value ? new Date(value).toLocaleString() : '-';
}
