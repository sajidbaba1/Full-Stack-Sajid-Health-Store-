// User and Authentication Types
export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  avatar?: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

export interface AuthRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  jwt: string;
  user: User;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

// Product Types
export interface Product {
  id: string;
  name: string;
  description: string;
  imageUrl?: string;
  imageUrls: string[];
  category: Category;
  variants: ProductVariant[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
  averageRating?: number;
  reviewCount?: number;
}

export interface ProductVariant {
  id: string;
  sku: string;
  price: number;
  originalPrice?: number;
  stockQuantity: number;
  options: VariantOption[];
  product: Product;
}

export interface VariantOption {
  id: string;
  name: string;
  value: string;
}

export interface Category {
  id: string;
  name: string;
  description?: string;
  imageUrl?: string;
  parentCategory?: Category;
  subCategories: Category[];
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

// Cart Types
export interface Cart {
  id: string;
  user: User;
  items: CartItem[];
  totalAmount: number;
  createdAt: string;
  updatedAt: string;
}

export interface CartItem {
  id: string;
  product: Product;
  productVariant: ProductVariant;
  quantity: number;
  price: number;
  cart: Cart;
}

// Order Types
export interface Order {
  id: string;
  user: User;
  items: OrderItem[];
  totalAmount: number;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  shippingAddress: Address;
  billingAddress: Address;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItem {
  id: string;
  product: Product;
  productVariant: ProductVariant;
  quantity: number;
  price: number;
  order: Order;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  RETURNED = 'RETURNED'
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED'
}

export interface Address {
  id: string;
  street: string;
  city: string;
  state: string;
  zipCode: string;
  country: string;
  user: User;
}

// Review and Rating Types
export interface Review {
  id: string;
  product: Product;
  user: User;
  rating: number;
  comment: string;
  createdAt: string;
  updatedAt: string;
}

export interface Rating {
  id: string;
  product: Product;
  user: User;
  rating: number;
  createdAt: string;
}

// API Response Types
export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
}

export interface PaginatedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export interface SearchFilterDTO {
  name?: string;
  categoryId?: string;
  minPrice?: number;
  maxPrice?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  size?: number;
}

// UI Component Types
export interface NavItem {
  title: string;
  href: string;
  disabled?: boolean;
  external?: boolean;
  icon?: React.ComponentType<{ className?: string }>;
  label?: string;
}

export interface SidebarNavItem extends NavItem {
  items?: SidebarNavItem[];
}

export interface DashboardConfig {
  mainNav: NavItem[];
  sidebarNav: SidebarNavItem[];
}

// Form Types
export interface ContactForm {
  name: string;
  email: string;
  subject: string;
  message: string;
}

export interface NewsletterForm {
  email: string;
}

// Theme Types
export type Theme = 'light' | 'dark' | 'system';

// Store Types (for Zustand)
export interface AuthStore {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (credentials: AuthRequest) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
}

export interface CartStore {
  cart: Cart | null;
  isLoading: boolean;
  addItem: (productId: string, variantId: string, quantity: number) => Promise<void>;
  updateItem: (itemId: string, quantity: number) => Promise<void>;
  removeItem: (itemId: string) => Promise<void>;
  clearCart: () => Promise<void>;
  fetchCart: () => Promise<void>;
}

export interface ProductStore {
  products: Product[];
  categories: Category[];
  isLoading: boolean;
  currentProduct: Product | null;
  searchResults: Product[];
  fetchProducts: (filters?: SearchFilterDTO) => Promise<void>;
  fetchProduct: (id: string) => Promise<void>;
  fetchCategories: () => Promise<void>;
  searchProducts: (query: string, filters?: SearchFilterDTO) => Promise<void>;
}

// Utility Types
export type WithClassName<T = {}> = T & {
  className?: string;
}

export type WithChildren<T = {}> = T & {
  children?: React.ReactNode;
}

export type ButtonVariant = 'default' | 'destructive' | 'outline' | 'secondary' | 'ghost' | 'link';
export type ButtonSize = 'default' | 'sm' | 'lg' | 'icon';

export type InputVariant = 'default' | 'destructive';

export type CardVariant = 'default' | 'elevated' | 'outlined';
