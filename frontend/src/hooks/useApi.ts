import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import * as api from '../services/api';

// Accounts
export function useAccounts() {
  return useQuery({ queryKey: ['accounts'], queryFn: api.accounts.list });
}

export function useCreateAccount() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.accounts.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['accounts'] }),
  });
}

export function useDeleteAccount() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.accounts.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['accounts'] }),
  });
}

// Categories
export function useCategories(flat = false) {
  return useQuery({ queryKey: ['categories', flat], queryFn: () => api.categories.list(flat) });
}

export function useCreateCategory() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.categories.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['categories'] }),
  });
}

// Transactions
export function useTransactions(page = 0) {
  return useQuery({
    queryKey: ['transactions', page],
    queryFn: () => api.transactions.list(page),
  });
}

export function useCreateTransaction() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.transactions.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['transactions'] }),
  });
}

export function useDeleteTransaction() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.transactions.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['transactions'] }),
  });
}

// Budgets
export function useBudgets(month?: string) {
  return useQuery({ queryKey: ['budgets', month], queryFn: () => api.budgets.list(month) });
}

export function useCreateBudget() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.budgets.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['budgets'] }),
  });
}

export function useDeleteBudget() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.budgets.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['budgets'] }),
  });
}

// Goals
export function useGoals() {
  return useQuery({ queryKey: ['goals'], queryFn: api.goals.list });
}

export function useCreateGoal() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.goals.create,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals'] }),
  });
}

export function useDeleteGoal() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: api.goals.delete,
    onSuccess: () => qc.invalidateQueries({ queryKey: ['goals'] }),
  });
}

// Reports
export function useDashboard() {
  return useQuery({ queryKey: ['dashboard'], queryFn: api.reports.dashboard });
}

export function useSpendingByCategory(startDate?: string, endDate?: string) {
  return useQuery({
    queryKey: ['spending', startDate, endDate],
    queryFn: () => api.reports.spendingByCategory(startDate, endDate),
  });
}

export function useMonthlySummary(months = 12) {
  return useQuery({
    queryKey: ['monthlySummary', months],
    queryFn: () => api.reports.monthlySummary(months),
  });
}
