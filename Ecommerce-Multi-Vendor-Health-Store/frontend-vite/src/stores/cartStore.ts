import { create } from 'zustand';
import { Cart, CartItem } from '../types/api';
import { apiService } from '../services/api';

interface CartState {
  cart: Cart | null;
  isLoading: boolean;
  error: string | null;
  
  // Actions
  fetchCart: () => Promise<void>;
  addToCart: (productId: number, quantity: number) => Promise<void>;
  updateCartItem: (itemId: number, quantity: number) => Promise<void>;
  removeFromCart: (itemId: number) => Promise<void>;
  clearCart: () => Promise<void>;
  clearError: () => void;
  getCartItemCount: () => number;
  getCartTotal: () => number;
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  isLoading: false,
  error: null,

  fetchCart: async () => {
    set({ isLoading: true, error: null });
    try {
      const cart = await apiService.getCart();
      set({ cart, isLoading: false });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to fetch cart';
      set({ error: errorMessage, isLoading: false });
    }
  },

  addToCart: async (productId: number, quantity: number) => {
    set({ isLoading: true, error: null });
    try {
      await apiService.addToCart(productId, quantity);
      // Refresh cart after adding item
      await get().fetchCart();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to add item to cart';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  updateCartItem: async (itemId: number, quantity: number) => {
    set({ isLoading: true, error: null });
    try {
      await apiService.updateCartItem(itemId, quantity);
      // Refresh cart after updating item
      await get().fetchCart();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to update cart item';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  removeFromCart: async (itemId: number) => {
    set({ isLoading: true, error: null });
    try {
      await apiService.removeFromCart(itemId);
      // Refresh cart after removing item
      await get().fetchCart();
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to remove item from cart';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  clearCart: async () => {
    set({ isLoading: true, error: null });
    try {
      await apiService.clearCart();
      set({ cart: null, isLoading: false });
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || 'Failed to clear cart';
      set({ error: errorMessage, isLoading: false });
      throw error;
    }
  },

  clearError: () => {
    set({ error: null });
  },

  getCartItemCount: () => {
    const cart = get().cart;
    return cart?.items.reduce((total, item) => total + item.quantity, 0) || 0;
  },

  getCartTotal: () => {
    const cart = get().cart;
    return cart?.totalAmount || 0;
  }
}));
