import { useState } from 'react';
import { useGoals, useCreateGoal, useDeleteGoal } from '../hooks/useApi';
import { formatCurrency } from '../lib/utils';
import { Plus, Trash2, Target, CheckCircle } from 'lucide-react';

export default function GoalsPage() {
  const { data: goals, isLoading } = useGoals();
  const createGoal = useCreateGoal();
  const deleteGoal = useDeleteGoal();
  const [showForm, setShowForm] = useState(false);
  const [name, setName] = useState('');
  const [targetAmount, setTargetAmount] = useState('');
  const [deadline, setDeadline] = useState('');
  const [color, setColor] = useState('#6366f1');

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createGoal.mutateAsync({
      name, targetAmount: parseFloat(targetAmount),
      deadline: deadline || undefined,
      color, icon: 'target',
    } as any);
    setName('');
    setTargetAmount('');
    setDeadline('');
    setShowForm(false);
  };

  if (isLoading) return <div className="text-slate-400">Loading...</div>;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Savings Goals</h1>
        <button onClick={() => setShowForm(!showForm)} className="btn-primary flex items-center gap-2">
          <Plus size={18} /> Add Goal
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleCreate} className="card grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
          <div>
            <label className="block text-sm text-slate-300 mb-1">Goal Name</label>
            <input className="input" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Target Amount</label>
            <input className="input" type="number" step="0.01" min="0.01"
              value={targetAmount} onChange={(e) => setTargetAmount(e.target.value)} required />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Deadline</label>
            <input className="input" type="date" value={deadline}
              onChange={(e) => setDeadline(e.target.value)} />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Color</label>
            <input className="input h-[42px]" type="color" value={color}
              onChange={(e) => setColor(e.target.value)} />
          </div>
          <div className="flex items-end gap-2">
            <button type="submit" className="btn-primary flex-1" disabled={createGoal.isPending}>
              {createGoal.isPending ? 'Saving...' : 'Save'}
            </button>
            <button type="button" onClick={() => setShowForm(false)} className="btn-secondary">Cancel</button>
          </div>
        </form>
      )}

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {goals?.map((goal) => (
          <div key={goal.id} className="card">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <div className="p-3 rounded-lg bg-surface-light">
                  {goal.isAchieved ? (
                    <CheckCircle className="text-success" size={20} />
                  ) : (
                    <Target size={20} style={{ color: goal.color || '#6366f1' }} />
                  )}
                </div>
                <div>
                  <p className="font-semibold">{goal.name}</p>
                  {goal.deadline && (
                    <p className="text-xs text-slate-400">
                      {goal.daysRemaining > 0
                        ? `${goal.daysRemaining} days left`
                        : goal.daysRemaining === 0
                        ? 'Due today'
                        : 'Past due'}
                    </p>
                  )}
                </div>
              </div>
              <button onClick={() => deleteGoal.mutate(goal.id)}
                className="p-1.5 text-slate-500 hover:text-danger transition-colors">
                <Trash2 size={14} />
              </button>
            </div>

            <div className="mt-4">
              <div className="flex justify-between text-sm mb-2">
                <span className="text-slate-400">{formatCurrency(goal.currentAmount)}</span>
                <span className="text-slate-400">of {formatCurrency(goal.targetAmount)}</span>
              </div>
              <div className="w-full h-2.5 bg-surface-light rounded-full overflow-hidden">
                <div
                  className="h-full rounded-full transition-all"
                  style={{
                    width: `${Math.min(goal.percentageComplete, 100)}%`,
                    backgroundColor: goal.isAchieved ? '#10b981' : (goal.color || '#6366f1'),
                  }}
                />
              </div>
              <p className="text-xs text-slate-500 mt-1">
                {goal.percentageComplete.toFixed(1)}% complete
              </p>
            </div>
          </div>
        ))}
        {goals?.length === 0 && (
          <div className="card col-span-full text-center text-slate-500 py-8">
            No goals yet. Set your first savings goal!
          </div>
        )}
      </div>
    </div>
  );
}
