import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { User, AuthRequest, RegisterRequest, AuthResponse } from '../types/api';
import { apiService } from '../services/api';

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  login: (credentials: AuthRequest) => Promise<void>;
  register: (userData: RegisterRequest) => Promise<void>;
  logout: () => void;
  clearError: () => void;
  refreshUser: () => Promise<void>;
  isAdmin: () => boolean;
  isSeller: () => boolean;
  isCustomer: () => boolean;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      isLoading: false,
      error: null,

      login: async (credentials: AuthRequest) => {
        set({ isLoading: true, error: null });
        try {
          const response: AuthResponse = await apiService.login(credentials);
          
          const token = response.jwt;
          const user = response.user;
          
          // Store token
          localStorage.setItem('jwt', token);
          apiService.setAuthToken(token);
          
          set({ 
            user: user, 
            token: token, 
            isAuthenticated: true, 
            isLoading: false 
          });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || error.message || 'Login failed';
          set({ 
            error: errorMessage, 
            isLoading: false,
            isAuthenticated: false,
            user: null,
            token: null
          });
          throw error;
        }
      },

      register: async (userData: RegisterRequest) => {
        set({ isLoading: true, error: null });
        try {
          const response = await apiService.register(userData);
          // Extract token and user from response structure
          const token = (response as any).token || (response as any).jwt;
          const user = (response as any).user || response;
          
          set({ 
            user: user as User, 
            token: token as string, 
            isAuthenticated: true, 
            isLoading: false 
          });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Registration failed';
          set({ 
            error: errorMessage, 
            isLoading: false,
            isAuthenticated: false,
            user: null,
            token: null
          });
          throw error;
        }
      },

      logout: () => {
        apiService.removeAuthToken();
        set({ 
          user: null, 
          token: null, 
          isAuthenticated: false, 
          error: null 
        });
      },

      clearError: () => {
        set({ error: null });
      },

      refreshUser: async () => {
        set({ isLoading: true, error: null });
        try {
          const user = await apiService.getUserProfile();
          set({ user, isLoading: false });
        } catch (error: any) {
          const errorMessage = error.response?.data?.message || 'Failed to refresh user';
          set({ 
            error: errorMessage, 
            isLoading: false,
            isAuthenticated: false,
            user: null,
            token: null
          });
        }
      },

      isAdmin: () => {
        const { user } = get();
        return user?.roles?.some(role => role.name === 'ADMIN') || false;
      },

      isSeller: () => {
        const { user } = get();
        return user?.roles?.some(role => role.name === 'SELLER') || false;
      },

      isCustomer: () => {
        const { user } = get();
        return user?.roles?.some(role => role.name === 'CUSTOMER') || false;
      }
    }),
    {
      name: 'auth-store',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
        isAuthenticated: state.isAuthenticated,
      }),
    }
  )
);
