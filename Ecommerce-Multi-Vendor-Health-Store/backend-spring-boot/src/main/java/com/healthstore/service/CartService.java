package com.healthstore.service;

import com.healthstore.model.Cart;
import com.healthstore.model.CartItem;
import com.healthstore.model.Product;
import com.healthstore.model.User;
import com.healthstore.repository.CartItemRepository;
import com.healthstore.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for handling shopping cart-related business logic.
 */
@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, 
                      CartItemRepository cartItemRepository, 
                      ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }

    /**
     * Creates a new shopping cart for a given user.
     * @param user The user for whom the cart is to be created.
     * @return The newly created cart.
     */
    public Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }

    /**
     * Finds a user's cart. If the user doesn't have one, it creates a new one.
     * @param user The user whose cart is to be found or created.
     * @return The user's cart.
     */
    public Cart findOrCreateCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> createCartForUser(user));
    }

    /**
     * Adds a product to a user's cart.
     * If the product is already in the cart, it updates the quantity.
     * @param user The user whose cart to modify.
     * @param productId The ID of the product to add.
     * @param quantity The quantity of the product.
     * @return The updated cart.
     * @throws RuntimeException if the product is not found.
     */
    public Cart addProductToCart(User user, Long productId, int quantity) {
        Cart cart = findOrCreateCart(user);
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if product already exists in cart
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            // Update quantity if product already in cart
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    /**
     * Retrieves the cart for a specific user.
     * @param user The user whose cart to retrieve.
     * @return The user's cart.
     * @throws RuntimeException if the cart is not found.
     */
    @Transactional(readOnly = true)
    public Cart getUserCart(User user) {
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getEmail()));
    }

    /**
     * Removes an item from the user's cart.
     * @param user The user whose cart to modify.
     * @param productId The ID of the product to remove.
     * @return The updated cart.
     */
    public Cart removeItemFromCart(User user, Long productId) {
        Cart cart = getUserCart(user);
        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        if (removed) {
            cartRepository.save(cart);
        }
        return cart;
    }

    /**
     * Updates the quantity of an item in the cart.
     * @param user The user whose cart to modify.
     * @param productId The ID of the product to update.
     * @param quantity The new quantity.
     * @return The updated cart.
     * @throws RuntimeException if the product is not in the cart.
     */
    public Cart updateCartItemQuantity(User user, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeItemFromCart(user, productId);
        }

        Cart cart = getUserCart(user);
        CartItem item = cart.getItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Product not found in cart"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
        return cartRepository.save(cart);
    }

    /**
     * Clears all items from the user's cart.
     * @param user The user whose cart to clear.
     * @return The cleared cart.
     */
    public Cart clearCart(User user) {
        Cart cart = getUserCart(user);
        cart.getItems().clear();
        return cartRepository.save(cart);
    }
}
