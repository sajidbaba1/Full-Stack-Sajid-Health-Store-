import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  User,
  Product,
  ProductResponseDTO,
  Cart,
  CartItem,
  Order,
  Review,
  Category,
  AuthRequest,
  AuthResponse,
  RegisterRequest,
  ReviewRequestDTO,
  SearchFilterDTO,
  PaymentRequestDTO,
  UserUpdateDTO,
  PasswordUpdateDTO,
  PageResponse,
  PageRequest
} from '../types/api';

class ApiService {
  private api: AxiosInstance;
  private baseURL = 'http://localhost:8080';

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
        if (error.response?.status === 401) {
          // Token expired or invalid
          localStorage.removeItem('jwt');
          localStorage.removeItem('user');
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  // Authentication APIs
  async login(credentials: AuthRequest): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.post('/auth/login', credentials);
    return response.data;
  }

  async register(userData: RegisterRequest): Promise<AuthResponse> {
    const response: AxiosResponse<AuthResponse> = await this.api.post('/auth/register', userData);
    return response.data;
  }

  // Product APIs
  async getProducts(filters?: SearchFilterDTO): Promise<PageResponse<ProductResponseDTO>> {
    const params = new URLSearchParams();
    if (filters?.query) params.append('query', filters.query);
    if (filters?.categoryId) params.append('categoryId', filters.categoryId.toString());
    if (filters?.minPrice) params.append('minPrice', filters.minPrice.toString());
    if (filters?.maxPrice) params.append('maxPrice', filters.maxPrice.toString());
    if (filters?.sortBy) params.append('sortBy', filters.sortBy);
    if (filters?.sortDirection) params.append('sortDirection', filters.sortDirection);
    if (filters?.page) params.append('page', filters.page.toString());
    if (filters?.size) params.append('size', filters.size.toString());

    const response: AxiosResponse<PageResponse<ProductResponseDTO>> = await this.api.get(
      `/api/products?${params.toString()}`
    );
    return response.data;
  }

  async getProductById(id: number): Promise<Product> {
    const response: AxiosResponse<Product> = await this.api.get(`/api/products/${id}`);
    return response.data;
  }

  async getFeaturedProducts(): Promise<ProductResponseDTO[]> {
    const response: AxiosResponse<ProductResponseDTO[]> = await this.api.get('/api/products/featured');
    return response.data;
  }

  async searchProducts(query: string): Promise<ProductResponseDTO[]> {
    const response: AxiosResponse<ProductResponseDTO[]> = await this.api.get(
      `/api/products/search?query=${encodeURIComponent(query)}`
    );
    return response.data;
  }

  // Category APIs
  async getCategories(): Promise<Category[]> {
    const response: AxiosResponse<Category[]> = await this.api.get('/api/categories');
    return response.data;
  }

  async getCategoryById(id: number): Promise<Category> {
    const response: AxiosResponse<Category> = await this.api.get(`/api/categories/${id}`);
    return response.data;
  }

  // Cart APIs
  async getCart(): Promise<Cart> {
    const response: AxiosResponse<Cart> = await this.api.get('/api/cart');
    return response.data;
  }

  async addToCart(productId: number, quantity: number): Promise<CartItem> {
    const response: AxiosResponse<CartItem> = await this.api.post('/api/cart/add', {
      productId,
      quantity
    });
    return response.data;
  }

  async updateCartItem(itemId: number, quantity: number): Promise<CartItem> {
    const response: AxiosResponse<CartItem> = await this.api.put(`/api/cart/items/${itemId}`, {
      quantity
    });
    return response.data;
  }

  async removeFromCart(itemId: number): Promise<void> {
    await this.api.delete(`/api/cart/items/${itemId}`);
  }

  async clearCart(): Promise<void> {
    await this.api.delete('/api/cart/clear');
  }

  // Order APIs
  async createOrder(shippingAddressId: number): Promise<Order> {
    const response: AxiosResponse<Order> = await this.api.post('/api/orders', {
      shippingAddressId
    });
    return response.data;
  }

  async getUserOrders(pageRequest?: PageRequest): Promise<PageResponse<Order>> {
    const params = new URLSearchParams();
    if (pageRequest?.page) params.append('page', pageRequest.page.toString());
    if (pageRequest?.size) params.append('size', pageRequest.size.toString());
    if (pageRequest?.sort) params.append('sort', pageRequest.sort);

    const response: AxiosResponse<PageResponse<Order>> = await this.api.get(
      `/api/users/orders?${params.toString()}`
    );
    return response.data;
  }

  async getOrderById(id: number): Promise<Order> {
    const response: AxiosResponse<Order> = await this.api.get(`/api/orders/${id}`);
    return response.data;
  }

  async updateOrderStatus(id: number, status: string): Promise<Order> {
    const response: AxiosResponse<Order> = await this.api.put(`/api/orders/${id}/status`, {
      status
    });
    return response.data;
  }

  // Review APIs
  async getProductReviews(productId: number): Promise<Review[]> {
    const response: AxiosResponse<Review[]> = await this.api.get(`/api/products/${productId}/reviews`);
    return response.data;
  }

  async submitReview(review: ReviewRequestDTO): Promise<Review> {
    const response: AxiosResponse<Review> = await this.api.post('/api/reviews', review);
    return response.data;
  }

  async getUserReviews(): Promise<Review[]> {
    const response: AxiosResponse<Review[]> = await this.api.get('/api/reviews/user');
    return response.data;
  }

  async removeReview(productId: number): Promise<void> {
    await this.api.delete(`/api/reviews/product/${productId}`);
  }

  // User Profile APIs
  async getCurrentUser(): Promise<User> {
    const response: AxiosResponse<User> = await this.api.get('/api/users/profile');
    return response.data;
  }

  async updateProfile(userData: UserUpdateDTO): Promise<User> {
    const response: AxiosResponse<User> = await this.api.put('/api/users/profile', userData);
    return response.data;
  }

  async updatePassword(passwordData: PasswordUpdateDTO): Promise<void> {
    await this.api.put('/api/users/password', passwordData);
  }

  // Payment APIs
  async createCheckoutSession(paymentData: PaymentRequestDTO): Promise<string> {
    const response: AxiosResponse<string> = await this.api.post('/api/payments/create-checkout-session', paymentData);
    return response.data;
  }

  // Recommendation APIs
  async getRecommendations(): Promise<ProductResponseDTO[]> {
    const response: AxiosResponse<ProductResponseDTO[]> = await this.api.get('/api/recommendations');
    return response.data;
  }

  // Chatbot API
  async askChatbot(message: string): Promise<string> {
    const response: AxiosResponse<string> = await this.api.post('/api/chatbot/ask', message, {
      headers: {
        'Content-Type': 'text/plain'
      }
    });
    return response.data;
  }

  // Utility methods
  setAuthToken(token: string): void {
    localStorage.setItem('jwt', token);
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
}

export const apiService = new ApiService();
export default apiService;
