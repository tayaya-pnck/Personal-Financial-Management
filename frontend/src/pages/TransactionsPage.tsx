import { useState } from 'react';
import {
  useTransactions,
  useCreateTransaction,
  useDeleteTransaction,
  useAccounts,
  useCategories,
} from '../hooks/useApi';
import { formatCurrency, formatDate } from '../lib/utils';
import { Plus, Trash2, ArrowUpRight, ArrowDownRight } from 'lucide-react';
import type { TransactionType } from '../types';

export default function TransactionsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useTransactions(page);
  const { data: accounts } = useAccounts();
  const { data: cats } = useCategories(true);
  const createTransaction = useCreateTransaction();
  const deleteTransaction = useDeleteTransaction();
  const [showForm, setShowForm] = useState(false);

  const [form, setForm] = useState({
    accountId: '',
    categoryId: '',
    amount: '',
    description: '',
    date: new Date().toISOString().split('T')[0],
    type: 'EXPENSE' as TransactionType,
  });

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createTransaction.mutateAsync({
      ...form,
      amount: parseFloat(form.amount),
      categoryId: form.categoryId || undefined,
    } as any);
    setShowForm(false);
    setForm({ accountId: '', categoryId: '', amount: '', description: '', date: new Date().toISOString().split('T')[0], type: 'EXPENSE' });
  };

  if (isLoading) return <div className="text-slate-400">Loading...</div>;

  const expenseCats = cats?.filter((c) => c.type === 'EXPENSE');
  const incomeCats = cats?.filter((c) => c.type === 'INCOME');

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Transactions</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
          <Plus size={18} /> Add Transaction
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="card grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <div>
            <label className="block text-sm text-slate-300 mb-1">Type</label>
            <select className="select" value={form.type}
              onChange={(e) => setForm({ ...form, type: e.target.value as TransactionType, categoryId: '' })}>
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Account</label>
            <select className="select" value={form.accountId}
              onChange={(e) => setForm({ ...form, accountId: e.target.value })} required>
              <option value="">Select account</option>
              {accounts?.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Category</label>
            <select className="select" value={form.categoryId}
              onChange={(e) => setForm({ ...form, categoryId: e.target.value })}>
              <option value="">None</option>
              {(form.type === 'EXPENSE' ? expenseCats : incomeCats)?.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Amount</label>
            <input className="input" type="number" step="0.01" min="0.01"
              value={form.amount} onChange={(e) => setForm({ ...form, amount: e.target.value })} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Date</label>
            <input className="input" type="date" value={form.date}
              onChange={(e) => setForm({ ...form, date: e.target.value })} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Description</label>
            <input className="input" value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })} placeholder="Optional" />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary" disabled={createTransaction.isPending}>
              {createTransaction.isPending ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <div className="card p-0 overflow-hidden">
        <table className="w-full">
          <thead>
            <tr className="border-b border-border">
              <th className="text-left px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Type</th>
              <th className="text-left px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Account</th>
              <th className="text-left px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Category</th>
              <th className="text-left px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Description</th>
              <th className="text-right px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Amount</th>
              <th className="text-right px-6 py-3 text-xs text-slate-400 uppercase tracking-wider">Date</th>
              <th className="px-6 py-3 w-10"></th>
            </tr>
          </thead>
          <tbody>
            {data?.content.map((tx) => (
              <tr key={tx.id} className="border-b border-border/50 hover:bg-surface-light/50 transition-colors">
                <td className="px-6 py-4">
                  <div className={`p-1.5 rounded-lg inline-block ${tx.type === 'INCOME' ? 'bg-success/10' : 'bg-danger/10'}`}>
                    {tx.type === 'INCOME'
                      ? <ArrowUpRight className="text-success" size={16} />
                      : <ArrowDownRight className="text-danger" size={16} />}
                  </div>
                </td>
                <td className="px-6 py-4 text-sm">{tx.accountName}</td>
                <td className="px-6 py-4 text-sm text-slate-300">{tx.categoryName || '-'}</td>
                <td className="px-6 py-4 text-sm text-slate-300">{tx.description || '-'}</td>
                <td className={`px-6 py-4 text-sm font-semibold text-right ${tx.type === 'INCOME' ? 'text-success' : 'text-danger'}`}>
                  {tx.type === 'INCOME' ? '+' : '-'}{formatCurrency(tx.amount)}
                </td>
                <td className="px-6 py-4 text-sm text-right text-slate-400">{formatDate(tx.date)}</td>
                <td className="px-6 py-4">
                  <button onClick={() => deleteTransaction.mutate(tx.id)}
                    className="text-slate-500 hover:text-danger transition-colors">
                    <Trash2 size={14} />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {data && (
        <div className="flex items-center justify-between text-sm text-slate-400">
          <span>Page {data.page + 1} of {data.totalPages} ({data.totalElements} total)</span>
          <div className="flex gap-2">
            <button disabled={data.first} onClick={() => setPage(page - 1)} className="btn-secondary text-xs px-3 py-1">
              Previous
            </button>
            <button disabled={data.last} onClick={() => setPage(page + 1)} className="btn-secondary text-xs px-3 py-1">
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
