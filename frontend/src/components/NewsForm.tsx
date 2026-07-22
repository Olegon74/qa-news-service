import { FormEvent, useEffect, useState } from 'react';
import type { NewsRequest, NewsResponse } from '../types/news';

const emptyForm: NewsRequest = {
  title: '',
  previewText: '',
  fullText: '',
  author: ''
};

interface NewsFormProps {
  editingNews: NewsResponse | null;
  onCreate: (request: NewsRequest) => Promise<void>;
  onUpdate: (id: number, request: NewsRequest) => Promise<void>;
  onCancelEdit: () => void;
}

export function NewsForm({ editingNews, onCreate, onUpdate, onCancelEdit }: NewsFormProps) {
  const [form, setForm] = useState<NewsRequest>(emptyForm);
  const [validationError, setValidationError] = useState('');

  useEffect(() => {
    if (editingNews) {
      setForm({
        title: editingNews.title,
        previewText: editingNews.previewText,
        fullText: editingNews.fullText,
        author: editingNews.author
      });
    } else {
      setForm(emptyForm);
    }
    setValidationError('');
  }, [editingNews]);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const error = validate(form);
    if (error) {
      setValidationError(error);
      return;
    }

    setValidationError('');
    if (editingNews) {
      await onUpdate(editingNews.id, form);
    } else {
      await onCreate(form);
    }
    setForm(emptyForm);
  }

  return (
    <form className="news-form" data-testid="news-form" onSubmit={handleSubmit}>
      <div className="form-grid">
        <label htmlFor="title">Title</label>
        <input
          id="title"
          data-testid="title-input"
          value={form.title}
          onChange={(event) => setForm({ ...form, title: event.target.value })}
          maxLength={150}
        />

        <label htmlFor="previewText">Preview text</label>
        <textarea
          id="previewText"
          data-testid="preview-text-input"
          value={form.previewText}
          onChange={(event) => setForm({ ...form, previewText: event.target.value })}
          maxLength={300}
          rows={3}
        />

        <label htmlFor="fullText">Full text</label>
        <textarea
          id="fullText"
          data-testid="full-text-input"
          value={form.fullText}
          onChange={(event) => setForm({ ...form, fullText: event.target.value })}
          maxLength={5000}
          rows={6}
        />

        <label htmlFor="author">Author</label>
        <input
          id="author"
          data-testid="author-input"
          value={form.author}
          onChange={(event) => setForm({ ...form, author: event.target.value })}
          maxLength={100}
        />
      </div>

      {validationError && (
        <div className="message error" data-testid="error-message">
          Error: {validationError}
        </div>
      )}

      <div className="button-row">
        {!editingNews && (
          <button data-testid="create-news-button" type="submit">
            Create news
          </button>
        )}
        {editingNews && (
          <>
            <button data-testid="update-news-button" type="submit">
              Update news
            </button>
            <button data-testid="cancel-edit-button" type="button" className="secondary" onClick={onCancelEdit}>
              Cancel edit
            </button>
          </>
        )}
      </div>
    </form>
  );
}

function validate(form: NewsRequest): string {
  if (!form.title.trim()) {
    return 'Title is required';
  }
  if (!form.previewText.trim()) {
    return 'Preview text is required';
  }
  if (!form.fullText.trim()) {
    return 'Full text is required';
  }
  if (!form.author.trim()) {
    return 'Author is required';
  }
  return '';
}
