import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { authService, LoginRequest } from '../services/authService';
import toast from 'react-hot-toast';

interface User {
  email: string;
  role: string;
}

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (email: string, password: string) => Promise<boolean>;
  logout: () => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('auth_token');
    const storedUser = localStorage.getItem('auth_user');
    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (email: string, password: string): Promise<boolean> => {
    try {
      setIsLoading(true);
      const response = await authService.login({ email, password });
      
      if (response.result?.authenticated && response.result?.token) {
        const userData: User = { email, role: response.result.role };
        setUser(userData);
        setToken(response.result.token);
        localStorage.setItem('auth_token', response.result.token);
        localStorage.setItem('auth_user', JSON.stringify(userData));
        toast.success('Đăng nhập thành công!');
        return true;
      }
      toast.error('Đăng nhập thất bại');
      return false;
    } catch (error: any) {
      console.error('Login error:', error);
      const errorMsg = error.response?.data?.message || 'Đăng nhập thất bại';
      toast.error(errorMsg);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('auth_token');
    localStorage.removeItem('auth_user');
    toast.success('Đã đăng xuất');
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!user && !!token, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};
