import { useState } from 'react';
import { useAccounts, useCreateAccount, useDeleteAccount } from '../hooks/useApi';
import { formatCurrency, accountTypeLabel } from '../lib/utils';
import { Plus, Trash2, Wallet } from 'lucide-react';
import type { AccountType } from '../types';

export default function AccountsPage() {
  const { data: accounts, isLoading } = useAccounts();
  const createAccount = useCreateAccount();
  const deleteAccount = useDeleteAccount();
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState('');
  const [type, setType] = useState<AccountType>('CHECKING');
  const [balance, setBalance] = useState('0');
  const [color, setColor] = useState('#0ea5e9');

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createAccount.mutateAsync({ name, type, balance: parseFloat(balance), color } as any);
    setName('');
    setType('CHECKING');
    setBalance('0');
    setShowForm(false);
  };

  if (isLoading) return <div className="text-slate-400">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Accounts</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
          <Plus size={18} /> Add Account
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
            <select className="select" value={type} onChange={(e) => setType(e.target.value as AccountType)}>
              <option value="CHECKING">Checking</option>
              <option value="SAVINGS">Savings</option>
              <option value="CREDIT_CARD">Credit Card</option>
              <option value="INVESTMENT">Investment</option>
              <option value="CASH">Cash</option>
            </select>
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Balance</label>
            <input className="input" type="number" step="0.01" value={balance}
              onChange={(e) => setBalance(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Color</label>
            <input className="input h-[42px]" type="color" value={color}
              onChange={(e) => setColor(e.target.value)} />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary flex-1" disabled={createAccount.isPending}>
              {createAccount.isPending ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">
              Cancel
            </button>
          </div>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {accounts?.map((account) => (
          <div key={account.id} className="card">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 rounded-lg bg-surface-light">
                  <Wallet size={20} style={{ color: account.color || '#0ea5e9' }} />
                </div>
                <div>
                  <p className="font-semibold">{account.name}</p>
                  <p className="text-xs text-slate-400">{accountTypeLabel(account.type)}</p>
                </div>
              </div>
              <button
                onClick={() => deleteAccount.mutate(account.id)}
                className="p-2 text-slate-500 hover:text-danger transition-colors"
              >
                <Trash2 size={16} />
              </button>
            </div>
            <p className={`text-xl font-bold mt-4 ${account.balance < 0 ? 'text-danger' : 'text-success'}`}>
              {formatCurrency(Math.abs(account.balance))}
              {account.balance < 0 ? ' (debt)' : ''}
            </p>
          </div>
        ))}
        {accounts?.length === 0 && (
          <div className="card col-span-full text-center text-slate-500 py-8">
            No accounts yet. Add your first account to get started.
          </div>
        )}
      </div>
    </div>
  );
}
