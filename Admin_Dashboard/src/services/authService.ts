import api from './api';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  code: number;
  message: string;
  result: {
    authenticated: boolean;
    token: string;
    role: string;
  };
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
}

export const authService = {
  login: async (loginData: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post('/auth/login', loginData);
    return response.data;
  },

  register: async (registerData: RegisterRequest): Promise<any> => {
    const response = await api.post('/Users/Register', registerData);
    return response.data;
  },

  logout: async (): Promise<any> => {
    const response = await api.post('/auth/logout');
    return response.data;
  },
};
