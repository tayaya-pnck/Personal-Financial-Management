import { useState } from 'react';
import { useBudgets, useCreateBudget, useDeleteBudget, useCategories } from '../hooks/useApi';
import { formatCurrency, getCurrentMonth, getMonthLabel } from '../lib/utils';
import { Plus, Trash2, PiggyBank } from 'lucide-react';

export default function BudgetsPage() {
  const [month, setMonth] = useState(getCurrentMonth());
  const { data: budgets, isLoading } = useBudgets(month);
  const { data: cats } = useCategories(true);
  const createBudget = useCreateBudget();
  const deleteBudget = useDeleteBudget();
  const [showForm, setShowForm] = useState(false);
  const [categoryId, setCategoryId] = useState('');
  const [amount, setAmount] = useState('');

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createBudget.mutateAsync({ categoryId, month, amount: parseFloat(amount) } as any);
    setCategoryId('');
    setAmount('');
    setShowForm(false);
  };

  if (isLoading) return <div className="text-slate-400">Loading...</div>;

  const expenseCats = cats?.filter((c) => c.type === 'EXPENSE');
  const usedCatIds = new Set(budgets?.map((b) => b.categoryId));
  const availableCats = expenseCats?.filter((c) => !usedCatIds.has(c.id));

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Budgets</h1>
        <div className="flex items-center gap-4">
          <input
            type="month"
            className="input w-auto"
            value={month}
            onChange={(e) => setMonth(e.target.value)}
          />
          <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
            <Plus size={18} /> Add Budget
          </button>
        </div>
      </div>

      <p className="text-slate-400 text-sm">{getMonthLabel(month)}</p>

      {showForm && (
        <form onSubmit={handleCreate} className="card grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm text-slate-300 mb-1">Category</label>
            <select className="select" value={categoryId}
              onChange={(e) => setCategoryId(e.target.value)} required>
              <option value="">Select category</option>
              {availableCats?.map((c) => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Budget Amount</label>
            <input className="input" type="number" step="0.01" min="0.01"
              value={amount} onChange={(e) => setAmount(e.target.value)} required />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary" disabled={createBudget.isPending}>
              {createBudget.isPending ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {budgets?.map((budget) => {
          const percent = budget.percentageUsed;
          const isOver = percent >= 100;
          const isClose = percent >= 80 && percent < 100;

          return (
            <div key={budget.id} className="card">
              <div className="flex items-center justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div className="p-2 rounded-lg bg-surface-light">
                    <PiggyBank size={18} style={{ color: budget.categoryColor || '#0ea5e9' }} />
                  </div>
                  <div>
                    <p className="font-semibold">{budget.categoryName}</p>
                  </div>
                </div>
                <button onClick={() => deleteBudget.mutate(budget.id)}
                  className="p-1.5 text-slate-500 hover:text-danger transition-colors">
                  <Trash2 size={14} />
                </button>
              </div>

              <div className="flex justify-between text-sm mb-2">
                <span className="text-slate-400">
                  Spent: <span className={isOver ? 'text-danger' : ''}>{formatCurrency(budget.spent)}</span>
                </span>
                <span className="text-slate-400">Budget: {formatCurrency(budget.amount)}</span>
              </div>

              <div className="w-full h-2 bg-surface-light rounded-full overflow-hidden">
                <div
                  className={`h-full rounded-full transition-all ${
                    isOver ? 'bg-danger' : isClose ? 'bg-warning' : 'bg-success'
                  }`}
                  style={{ width: `${Math.min(percent, 100)}%` }}
                />
              </div>

              <div className="flex justify-between mt-1">
                <span className={`text-xs ${isOver ? 'text-danger' : 'text-slate-500'}`}>
                  {percent.toFixed(1)}% used
                </span>
                <span className={`text-xs ${budget.remaining < 0 ? 'text-danger' : 'text-slate-500'}`}>
                  {budget.remaining >= 0 ? `${formatCurrency(budget.remaining)} left` : `${formatCurrency(Math.abs(budget.remaining))} over`}
                </span>
              </div>
            </div>
          );
        })}
        {budgets?.length === 0 && (
          <div className="card col-span-full text-center text-slate-500 py-8">
            No budgets set for this month.
          </div>
        )}
      </div>
    </div>
  );
}
