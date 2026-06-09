import type {
  Account,
  AuthResponse,
  Budget,
  Category,
  CategorySpending,
  Dashboard,
  Goal,
  MonthlySummary,
  PageResponse,
  Transaction,
} from '../types';

const API_BASE = '/api';

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('accessToken');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };
  if (token) headers['Authorization'] = `Bearer ${token}`;

  const response = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (response.status === 401) {
    const refreshed = await tryRefresh();
    if (refreshed) {
      headers['Authorization'] = `Bearer ${localStorage.getItem('accessToken')}`;
      const retry = await fetch(`${API_BASE}${path}`, { ...options, headers });
      if (!retry.ok) throw new ApiError(await retry.json(), retry.status);
      return retry.json();
    }
    localStorage.clear();
    window.location.href = '/login';
    throw new Error('Session expired');
  }

  if (!response.ok) throw new ApiError(await response.json(), response.status);
  return response.json();
}

async function tryRefresh(): Promise<boolean> {
  const refreshToken = localStorage.getItem('refreshToken');
  if (!refreshToken) return false;
  try {
    const res = await fetch(`${API_BASE}/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
    if (!res.ok) return false;
    const data: AuthResponse = await res.json();
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('userName', data.name);
    localStorage.setItem('userEmail', data.email);
    return true;
  } catch {
    return false;
  }
}

export class ApiError extends Error {
  body: any;
  status: number;

  constructor(body: any, status: number) {
    super(body?.message || 'Request failed');
    this.body = body;
    this.status = status;
  }
}

// Auth
export const auth = {
  login: (email: string, password: string) =>
    request<AuthResponse>('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  register: (name: string, email: string, password: string) =>
    request<AuthResponse>('/auth/register', { method: 'POST', body: JSON.stringify({ name, email, password }) }),
};

// Accounts
export const accounts = {
  list: () => request<Account[]>('/accounts'),
  get: (id: string) => request<Account>(`/accounts/${id}`),
  create: (data: Partial<Account>) =>
    request<Account>('/accounts', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Partial<Account>) =>
    request<Account>(`/accounts/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<void>(`/accounts/${id}`, { method: 'DELETE' }),
};

// Categories
export const categories = {
  list: (flat = false) => request<Category[]>(`/categories?flat=${flat}`),
  get: (id: string) => request<Category>(`/categories/${id}`),
  create: (data: Partial<Category>) =>
    request<Category>('/categories', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Partial<Category>) =>
    request<Category>(`/categories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<void>(`/categories/${id}`, { method: 'DELETE' }),
};

// Transactions
export const transactions = {
  list: (page = 0, size = 20) =>
    request<PageResponse<Transaction>>(`/transactions?page=${page}&size=${size}`),
  byAccount: (accountId: string, page = 0, size = 20) =>
    request<PageResponse<Transaction>>(`/transactions/by-account/${accountId}?page=${page}&size=${size}`),
  get: (id: string) => request<Transaction>(`/transactions/${id}`),
  create: (data: Partial<Transaction>) =>
    request<Transaction>('/transactions', { method: 'POST', body: JSON.stringify(data) }),
  delete: (id: string) => request<void>(`/transactions/${id}`, { method: 'DELETE' }),
};

// Budgets
export const budgets = {
  list: (month?: string) => request<Budget[]>(`/budgets${month ? `?month=${month}` : ''}`),
  get: (id: string) => request<Budget>(`/budgets/${id}`),
  create: (data: Partial<Budget>) =>
    request<Budget>('/budgets', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Partial<Budget>) =>
    request<Budget>(`/budgets/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  delete: (id: string) => request<void>(`/budgets/${id}`, { method: 'DELETE' }),
};

// Goals
export const goals = {
  list: () => request<Goal[]>('/goals'),
  get: (id: string) => request<Goal>(`/goals/${id}`),
  create: (data: Partial<Goal>) =>
    request<Goal>('/goals', { method: 'POST', body: JSON.stringify(data) }),
  update: (id: string, data: Partial<Goal>) =>
    request<Goal>(`/goals/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  updateProgress: (id: string, currentAmount: number) =>
    request<Goal>(`/goals/${id}/progress`, { method: 'PATCH', body: JSON.stringify({ currentAmount }) }),
  delete: (id: string) => request<void>(`/goals/${id}`, { method: 'DELETE' }),
};

// Reports
export const reports = {
  dashboard: () => request<Dashboard>('/reports/dashboard'),
  spendingByCategory: (startDate?: string, endDate?: string) =>
    request<CategorySpending[]>(`/reports/spending-by-category${startDate ? `?startDate=${startDate}&endDate=${endDate}` : ''}`),
  monthlySummary: (months = 12) => request<MonthlySummary[]>(`/reports/monthly-summary?months=${months}`),
};
