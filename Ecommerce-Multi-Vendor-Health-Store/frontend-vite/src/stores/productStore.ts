import { create } from 'zustand';
import { Product, ProductResponseDTO, Category, SearchFilterDTO, PageResponse } from '../types/api';
import { apiService } from '../services/api';

interface ProductState {
  products: ProductResponseDTO[];
  featuredProducts: ProductResponseDTO[];
  categories: Category[];
  currentProduct: Product | null;
  totalPages: number;
  currentPage: number;
  isLoading: boolean;
  error: string | null;
  searchQuery: string;
  selectedCategory: number | null;
  sortBy: string;
  
  // Actions
  fetchProducts: (filters?: SearchFilterDTO) => Promise<void>;
  fetchFeaturedProducts: () => Promise<void>;
  fetchCategories: () => Promise<void>;
  fetchProductById: (id: number) => Promise<void>;
  searchProducts: (query: string) => Promise<void>;
  setSearchQuery: (query: string) => void;
  setSelectedCategory: (categoryId: number | null) => void;
  setSortBy: (sortBy: string) => void;
  clearError: () => void;
  clearCurrentProduct: () => void;
}

export const useProductStore = create<ProductState>((set, get) => ({
  products: [],
  featuredProducts: [],
  categories: [],
  currentProduct: null,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  error: null,
  searchQuery: '',
  selectedCategory: null,
  sortBy: 'featured',

  fetchProducts: async (filters?: SearchFilterDTO) => {
    set({ isLoading: true, error: null });
    try {
      const response: PageResponse<ProductResponseDTO> = await apiService.getProducts(filters);
      set({ 
        products: response.content,
        totalPages: response.totalPages,
        currentPage: response.number,
        isLoading: false 
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch products';
      set({ error: errorMessage, isLoading: false });
    }
  },

  fetchFeaturedProducts: async () => {
    set({ isLoading: true, error: null });
    try {
      const featuredProducts = await apiService.getFeaturedProducts();
      set({ featuredProducts, isLoading: false });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch featured products';
      set({ error: errorMessage, isLoading: false });
    }
  },

  fetchCategories: async () => {
    try {
      const categories = await apiService.getCategories();
      set({ categories });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch categories';
      set({ error: errorMessage });
    }
  },

  fetchProductById: async (id: number) => {
    set({ isLoading: true, error: null });
    try {
      const product = await apiService.getProductById(id);
      set({ currentProduct: product, isLoading: false });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch product';
      set({ error: errorMessage, isLoading: false });
    }
  },

  searchProducts: async (query: string) => {
    set({ isLoading: true, error: null, searchQuery: query });
    try {
      const products = await apiService.searchProducts(query);
      set({ 
        products, 
        isLoading: false,
        totalPages: 1,
        currentPage: 0
      });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to search products';
      set({ error: errorMessage, isLoading: false });
    }
  },

  setSearchQuery: (query: string) => {
    set({ searchQuery: query });
  },

  setSelectedCategory: (categoryId: number | null) => {
    set({ selectedCategory: categoryId });
  },

  setSortBy: (sortBy: string) => {
    set({ sortBy });
  },

  clearError: () => {
    set({ error: null });
  },

  clearCurrentProduct: () => {
    set({ currentProduct: null });
  }
}));
