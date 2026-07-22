import type { UserRole } from '../types/auth';
import type { NewsResponse } from '../types/news';
import { NewsCard } from './NewsCard';

interface NewsListProps {
  news: NewsResponse[];
  role: UserRole;
  onView: (id: number) => void;
  onEdit: (news: NewsResponse) => void;
  onPublish: (id: number) => void;
  onArchive: (id: number) => void;
  onDelete: (id: number) => void;
}

export function NewsList(props: NewsListProps) {
  return (
    <section className="news-list" data-testid="news-list">
      {props.news.length === 0 && <p className="muted">No news found.</p>}
      {props.news.map((item) => (
        <NewsCard
          key={item.id}
          news={item}
          role={props.role}
          onView={props.onView}
          onEdit={props.onEdit}
          onPublish={props.onPublish}
          onArchive={props.onArchive}
          onDelete={props.onDelete}
        />
      ))}
    </section>
  );
}
