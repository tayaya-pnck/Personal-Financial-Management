import { useState } from 'react';
import { useCategories, useCreateCategory } from '../hooks/useApi';
import { Plus, Tag } from 'lucide-react';
import type { CategoryType } from '../types';

function CategoryTree({ categories, depth = 0 }: { categories: any[]; depth?: number }) {
  return (
    <div className="space-y-1">
      {categories.map((cat) => (
        <div key={cat.id}>
          <div className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-surface-light/50 transition-colors" style={{ marginLeft: depth * 16 }}>
            <div className="p-1.5 rounded-lg bg-surface-light">
              <Tag size={14} style={{ color: cat.color || '#0ea5e9' }} />
            </div>
            <div className="flex-1">
              <p className="text-sm font-medium">{cat.name}</p>
              <p className="text-xs text-slate-400">{cat.type}</p>
            </div>
          </div>
          {cat.children?.length > 0 && (
            <CategoryTree categories={cat.children} depth={depth + 1} />
          )}
        </div>
      ))}
    </div>
  );
}

export default function CategoriesPage() {
  const { data: categories, isLoading } = useCategories();
  const createCategory = useCreateCategory();
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState('');
  const [type, setType] = useState<CategoryType>('EXPENSE');
  const [parentId, setParentId] = useState('');
  const [color, setColor] = useState('#0ea5e9');

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createCategory.mutateAsync({
      name, type, color,
      icon: 'tag',
      parentId: parentId || undefined,
    } as any);
    setName('');
    setParentId('');
    setShowForm(false);
  };

  if (isLoading) return <div className="text-slate-400">Loading...</div>;

  const allCats = categories || [];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Categories</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
          <Plus size={18} /> Add Category
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="card grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <div>
            <label className="block text-sm text-slate-300 mb-1">Name</label>
            <input className="input" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Type</label>
            <select className="select" value={type} onChange={(e) => setType(e.target.value as CategoryType)}>
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Parent</label>
            <select className="select" value={parentId} onChange={(e) => setParentId(e.target.value)}>
              <option value="">None (root category)</option>
              {allCats.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Color</label>
            <input className="input h-[42px]" type="color" value={color}
              onChange={(e) => setColor(e.target.value)} />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary flex-1" disabled={createCategory.isPending}>
              {createCategory.isPending ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <div className="card">
        {allCats.length === 0 ? (
          <p className="text-slate-500 text-center py-8">No categories yet.</p>
        ) : (
          <CategoryTree categories={allCats} />
        )}
      </div>
    </div>
  );
}
