package com.healthstore.service;

import com.healthstore.model.Cart;
import com.healthstore.model.CartItem;
import com.healthstore.model.Product;
import com.healthstore.model.User;
import com.healthstore.repository.CartItemRepository;
import com.healthstore.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class for handling shopping cart-related business logic.
 */
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ProductService productService) {
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
        Optional<Cart> existingCart = cartRepository.findByUser(user);
        return existingCart.orElseGet(() -> createCartForUser(user));
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
        Optional<Product> optionalProduct = productService.getProductById(productId);

        if (optionalProduct.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        Product product = optionalProduct.get();
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setProduct(product);
            newCartItem.setQuantity(quantity);
            cart.getCartItems().add(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        return cartRepository.save(cart);
    }

    /**
     * Retrieves the cart for a specific user.
     * @param user The user whose cart to retrieve.
     * @return The user's cart.
     * @throws RuntimeException if the cart is not found.
     */
    public Cart getUserCart(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + user.getEmail()));
    }
}
