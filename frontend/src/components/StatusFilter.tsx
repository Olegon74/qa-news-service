import type { NewsStatusFilter } from '../types/news';

interface StatusFilterProps {
  value: NewsStatusFilter;
  onChange: (value: NewsStatusFilter) => void;
}

export function StatusFilter({ value, onChange }: StatusFilterProps) {
  return (
    <div className="filter-row">
      <label htmlFor="statusFilter">Status</label>
      <select
        id="statusFilter"
        data-testid="status-filter"
        value={value}
        onChange={(event) => onChange(event.target.value as NewsStatusFilter)}
      >
        <option value="ALL">ALL</option>
        <option value="DRAFT">DRAFT</option>
        <option value="PUBLISHED">PUBLISHED</option>
        <option value="ARCHIVED">ARCHIVED</option>
      </select>
    </div>
  );
}
