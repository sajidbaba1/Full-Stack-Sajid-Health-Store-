import axios, { AxiosInstance } from 'axios';
import {
  User,
  Product,
  Cart,
  Order,
  Review,
  Category,
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  ReviewRequestDTO,
  UserUpdateDTO,
  PasswordUpdateDTO
} from '../types/api';

class ApiService {
  private api: AxiosInstance;
  private baseURL = 'http://localhost:5454';

  constructor() {
    this.api = axios.create({
      baseURL: this.baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    // Add request interceptor to include JWT token
    this.api.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('jwt');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Add response interceptor for error handling
    this.api.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 403) {
          console.error('Access forbidden - check authentication');
          // Don't redirect for public endpoints
          if (!error.config?.url?.includes('/products') && !error.config?.url?.includes('/categories')) {
            localStorage.removeItem('jwt');
            window.location.href = '/login';
          }
        }
        return Promise.reject(error);
      }
    );
  }

  // Generic error handler
  private handleError(error: any): never {
    console.error('API Error:', error);
    throw error;
  }

  // Public endpoints - no auth required
  async getProducts(params?: {
    page?: number;
    size?: number;
    sortBy?: string;
    sortOrder?: 'asc' | 'desc';
    categoryId?: number;
    query?: string;
    minPrice?: number;
    maxPrice?: number;
  }): Promise<any> {
    try {
      const response = await this.api.get('/api/products', {
        params: {
          page: params?.page || 0,
          size: params?.size || 12,
          sort: params?.sortBy === 'newest' ? 'createdAt,desc' : (params?.sortBy || 'name'),
          order: params?.sortOrder || 'asc',
          categoryId: params?.categoryId,
          search: params?.query,
          minPrice: params?.minPrice,
          maxPrice: params?.maxPrice,
        },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching products:', error);
      // Return empty response instead of throwing for 403 errors
      return {
        content: [],
        totalPages: 0,
        totalElements: 0,
        page: 0,
        size: 12
      };
    }
  }

  async getProduct(id: number): Promise<Product> {
    try {
      const response = await this.api.get(`/api/products/${id}`);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async getCategories(): Promise<Category[]> {
    try {
      const response = await this.api.get('/api/categories');
      return response.data;
    } catch (error) {
      console.error('Error fetching categories:', error);
      return [];
    }
  }

  async getCategory(id: number): Promise<Category> {
    try {
      const response = await this.api.get(`/api/categories/${id}`);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Authentication endpoints
  async login(credentials: AuthRequest): Promise<AuthResponse> {
    try {
      const response = await this.api.post('/auth/login', credentials);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async register(userData: RegisterRequest): Promise<User> {
    try {
      const response = await this.api.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Cart endpoints
  async getCart(): Promise<Cart> {
    try {
      const response = await this.api.get('/api/cart');
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async addToCart(productId: number, quantity: number): Promise<Cart> {
    try {
      const response = await this.api.post('/api/cart/add', { productId, quantity });
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async updateCartItem(productId: number, quantity: number): Promise<Cart> {
    try {
      const response = await this.api.put('/api/cart/update', { productId, quantity });
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async removeFromCart(productId: number): Promise<Cart> {
    try {
      const response = await this.api.delete(`/api/cart/remove/${productId}`);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Order endpoints
  async getOrders(): Promise<Order[]> {
    try {
      const response = await this.api.get('/api/orders');
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async createOrder(orderData: any): Promise<Order> {
    try {
      const response = await this.api.post('/api/orders', orderData);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async updateOrderStatus(orderId: number, status: string): Promise<Order> {
    try {
      const response = await this.api.put(`/api/orders/${orderId}/status`, { status });
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Review endpoints
  async getReviews(productId: number): Promise<Review[]> {
    try {
      const response = await this.api.get(`/api/reviews/product/${productId}`);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async createReview(reviewData: ReviewRequestDTO): Promise<Review> {
    try {
      const response = await this.api.post('/api/reviews', reviewData);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // User endpoints
  async getUserProfile(): Promise<User> {
    try {
      const response = await this.api.get('/api/users/profile');
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async updateUserProfile(userData: UserUpdateDTO): Promise<User> {
    try {
      const response = await this.api.put('/api/users/profile', userData);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async changePassword(passwordData: PasswordUpdateDTO): Promise<void> {
    try {
      await this.api.put('/api/users/change-password', passwordData);
    } catch (error) {
      this.handleError(error);
    }
  }

  async getCurrentUser(): Promise<User> {
    try {
      const response = await this.api.get('/auth/me');
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Payment endpoints
  async createPaymentSession(orderId: number): Promise<any> {
    try {
      const response = await this.api.post('/api/payments/create-session', { orderId });
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Admin endpoints
  async getSalesReport(startDate: string, endDate: string): Promise<any> {
    try {
      const response = await this.api.get(`/api/admin/reports/sales?startDate=${startDate}&endDate=${endDate}`);
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  async getUserActivityReport(): Promise<any> {
    try {
      const response = await this.api.get('/api/admin/reports/users');
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Chatbot endpoint
  async sendChatMessage(message: string): Promise<any> {
    try {
      const response = await this.api.post('/api/chatbot/ask', { message: message });
      return response.data;
    } catch (error) {
      console.error('Error sending chat message:', error);
      throw new Error('Failed to send message to chatbot');
    }
  }

  // File upload endpoint
  async uploadFile(file: File): Promise<any> {
    try {
      const formData = new FormData();
      formData.append('file', file);
      
      const response = await this.api.post('/api/files/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      this.handleError(error);
    }
  }

  // Utility methods
  async setAuthToken(token: string): Promise<void> {
    this.api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }

  removeAuthToken(): void {
    localStorage.removeItem('jwt');
    localStorage.removeItem('user');
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('jwt');
  }

  getCurrentUserFromStorage(): User | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  setCurrentUser(user: User): void {
    localStorage.setItem('user', JSON.stringify(user));
  }

  // Generic HTTP methods
  async get<T = any>(url: string, config?: any): Promise<{ data: T }> {
    const response = await this.api.get(url, config);
    return response;
  }

  async post<T = any>(url: string, data?: any, config?: any): Promise<{ data: T }> {
    const response = await this.api.post(url, data, config);
    return response;
  }

  async put<T = any>(url: string, data?: any, config?: any): Promise<{ data: T }> {
    const response = await this.api.put(url, data, config);
    return response;
  }

  async delete<T = any>(url: string, config?: any): Promise<{ data: T }> {
    const response = await this.api.delete(url, config);
    return response;
  }
}

export const apiService = new ApiService();
export default apiService;
