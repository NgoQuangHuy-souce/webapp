package org.example.service;

import org.example.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    private final ProductService productService;
    private Map<Long, Integer> cartItems = new HashMap<>();  // productId -> quantity

    @Autowired
    public CartService(ProductService productService) {
        this.productService = productService;
    }

    public void addProduct(Long productId, Integer quantity) {
        // Kiểm tra sản phẩm có tồn tại
        Product product = productService.getProductById(productId);

        // Kiểm tra số lượng tồn kho
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Thêm vào giỏ hàng
        if (cartItems.containsKey(productId)) {
            cartItems.put(productId, cartItems.get(productId) + quantity);
        } else {
            cartItems.put(productId, quantity);
        }
    }

    public void updateProductQuantity(Long productId, Integer quantity) {
        // Kiểm tra sản phẩm có trong giỏ hàng
        if (!cartItems.containsKey(productId)) {
            throw new IllegalArgumentException("Product not in cart");
        }

        // Kiểm tra số lượng tồn kho
        Product product = productService.getProductById(productId);
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available");
        }

        // Cập nhật số lượng
        if (quantity <= 0) {
            cartItems.remove(productId);
        } else {
            cartItems.put(productId, quantity);
        }
    }

    public void removeProduct(Long productId) {
        cartItems.remove(productId);
    }

    public void clearCart() {
        cartItems.clear();
    }

    public Map<Product, Integer> getCartItems() {
        Map<Product, Integer> items = new HashMap<>();

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = productService.getProductById(entry.getKey());
            items.put(product, entry.getValue());
        }

        return items;
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : cartItems.entrySet()) {
            Product product = productService.getProductById(entry.getKey());
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(entry.getValue()));
            total = total.add(itemTotal);
        }

        return total;
    }

    public int getItemCount() {
        return cartItems.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isEmpty() {
        return cartItems.isEmpty();
    }
}