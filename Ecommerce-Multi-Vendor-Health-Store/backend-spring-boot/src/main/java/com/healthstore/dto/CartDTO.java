package com.healthstore.dto;

import lombok.Data;
import java.util.List;

/**
 * Data Transfer Object for Cart entity.
 * Used to transfer cart data between the client and server.
 */
@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private List<CartItemDTO> cartItems;
}
