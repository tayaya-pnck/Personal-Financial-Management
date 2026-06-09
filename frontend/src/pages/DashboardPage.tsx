import { useDashboard, useMonthlySummary, useSpendingByCategory } from '../hooks/useApi';
import { formatCurrency, formatDate } from '../lib/utils';
import {
  TrendingUp,
  TrendingDown,
  Wallet,
  PiggyBank,
  ArrowUpRight,
  ArrowDownRight,
} from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  PieChart,
  Pie,
  Cell,
} from 'recharts';

const COLORS = ['#0ea5e9', '#6366f1', '#10b981', '#f59e0b', '#ef4444', '#ec4899', '#8b5cf6', '#14b8a6'];

export default function DashboardPage() {
  const { data: dashboard, isLoading: dashLoading } = useDashboard();
  const { data: monthlySummary } = useMonthlySummary(6);
  const { data: spending } = useSpendingByCategory();

  if (dashLoading || !dashboard) {
    return <div className="text-slate-400">Loading dashboard...</div>;
  }

  const statCards = [
    {
      label: 'Net Worth',
      value: formatCurrency(dashboard.netWorth),
      icon: Wallet,
      color: dashboard.netWorth >= 0 ? 'text-success' : 'text-danger',
      bg: dashboard.netWorth >= 0 ? 'bg-success/10' : 'bg-danger/10',
    },
    {
      label: 'Monthly Income',
      value: formatCurrency(dashboard.monthlyIncome),
      icon: TrendingUp,
      color: 'text-success',
      bg: 'bg-success/10',
    },
    {
      label: 'Monthly Expense',
      value: formatCurrency(dashboard.monthlyExpense),
      icon: TrendingDown,
      color: 'text-danger',
      bg: 'bg-danger/10',
    },
    {
      label: 'Savings Rate',
      value: `${dashboard.savingsRate.toFixed(1)}%`,
      icon: PiggyBank,
      color: dashboard.savingsRate > 0 ? 'text-success' : 'text-warning',
      bg: dashboard.savingsRate > 0 ? 'bg-success/10' : 'bg-warning/10',
    },
  ];

  return (
    <div className="space-y-8">
      <h1 className="text-2xl font-bold">Dashboard</h1>

      {/* Stat cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        {statCards.map(({ label, value, icon: Icon, color, bg }) => (
          <div key={label} className="card">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-slate-400">{label}</p>
                <p className={`text-2xl font-bold mt-1 ${color}`}>{value}</p>
              </div>
              <div className={`p-3 rounded-lg ${bg}`}>
                <Icon className={color} size={24} />
              </div>
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Income vs Expense chart */}
        <div className="card">
          <h3 className="text-lg font-semibold mb-4">Income vs Expense</h3>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={monthlySummary}>
              <CartesianGrid strokeDasharray="3 3" stroke="#334155" />
              <XAxis dataKey="month" stroke="#94a3b8" tick={{ fontSize: 12 }} />
              <YAxis stroke="#94a3b8" tick={{ fontSize: 12 }} />
              <Tooltip
                contentStyle={{ background: '#1e293b', border: '1px solid #475569', borderRadius: 8 }}
                labelStyle={{ color: '#e2e8f0' }}
              />
              <Bar dataKey="income" fill="#10b981" radius={[4, 4, 0, 0]} name="Income" />
              <Bar dataKey="expense" fill="#ef4444" radius={[4, 4, 0, 0]} name="Expense" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Spending by category */}
        <div className="card">
          <h3 className="text-lg font-semibold mb-4">Spending by Category</h3>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={spending}
                dataKey="amount"
                nameKey="categoryName"
                cx="50%"
                cy="50%"
                outerRadius={100}
                label={({ payload }: any) =>
                  `${payload.categoryName} ${payload.percentage.toFixed(0)}%`
                }
                labelLine={false}
              >
                {spending?.map((_, idx) => (
                  <Cell key={idx} fill={COLORS[idx % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{ background: '#1e293b', border: '1px solid #475569', borderRadius: 8 }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Account summary */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Accounts */}
        <div className="card">
          <h3 className="text-lg font-semibold mb-4">Accounts</h3>
          <div className="space-y-3">
            {dashboard.accounts.map((acc) => (
              <div key={acc.id} className="flex items-center justify-between py-2">
                <div className="flex items-center gap-3">
                  <div
                    className="w-3 h-3 rounded-full"
                    style={{ backgroundColor: acc.color || '#0ea5e9' }}
                  />
                  <div>
                    <p className="text-sm font-medium">{acc.name}</p>
                    <p className="text-xs text-slate-400">{acc.type}</p>
                  </div>
                </div>
                <span className={`text-sm font-semibold ${acc.balance < 0 ? 'text-danger' : ''}`}>
                  {formatCurrency(Math.abs(acc.balance))}
                  {acc.balance < 0 ? ' (debt)' : ''}
                </span>
              </div>
            ))}
          </div>
        </div>

        {/* Recent transactions */}
        <div className="card">
          <h3 className="text-lg font-semibold mb-4">Recent Transactions</h3>
          <div className="space-y-3">
            {dashboard.recentTransactions.map((tx) => (
              <div key={tx.id} className="flex items-center justify-between py-2">
                <div className="flex items-center gap-3">
                  <div
                    className={`p-2 rounded-lg ${
                      tx.type === 'INCOME' ? 'bg-success/10' : 'bg-danger/10'
                    }`}
                  >
                    {tx.type === 'INCOME' ? (
                      <ArrowUpRight className="text-success" size={16} />
                    ) : (
                      <ArrowDownRight className="text-danger" size={16} />
                    )}
                  </div>
                  <div>
                    <p className="text-sm font-medium">{tx.description || tx.categoryName || 'Transaction'}</p>
                    <p className="text-xs text-slate-400">
                      {tx.accountName} &middot; {formatDate(tx.date)}
                    </p>
                  </div>
                </div>
                <span
                  className={`text-sm font-semibold ${
                    tx.type === 'INCOME' ? 'text-success' : 'text-danger'
                  }`}
                >
                  {tx.type === 'INCOME' ? '+' : '-'}
                  {formatCurrency(tx.amount)}
                </span>
              </div>
            ))}
            {dashboard.recentTransactions.length === 0 && (
              <p className="text-sm text-slate-500 text-center py-4">No transactions yet</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
