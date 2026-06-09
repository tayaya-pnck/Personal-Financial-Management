import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { auth } from '../services/api';
import type { AuthResponse } from '../types';

interface AuthState {
  user: { id: string; email: string; name: string } | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthState['user'] | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    const name = localStorage.getItem('userName');
    const email = localStorage.getItem('userEmail');
    const id = localStorage.getItem('userId');
    if (token && id && name && email) {
      setUser({ id, email, name });
    }
    setIsLoading(false);
  }, []);

  const handleAuthResponse = (data: AuthResponse) => {
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('userName', data.name);
    localStorage.setItem('userEmail', data.email);
    setUser({ id: data.userId, email: data.email, name: data.name });
  };

  const login = async (email: string, password: string) => {
    const data = await auth.login(email, password);
    handleAuthResponse(data);
  };

  const register = async (name: string, email: string, password: string) => {
    const data = await auth.register(name, email, password);
    handleAuthResponse(data);
  };

  const logout = () => {
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
