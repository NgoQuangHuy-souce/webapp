package org.example.controller;

import org.example.model.Category;
import org.example.model.Product;
import org.example.service.CartService;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartWebController {

    private final CartService cartService;
    private final CategoryService categoryService;

    @Autowired
    public CartWebController(CartService cartService, CategoryService categoryService) {
        this.cartService = cartService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String viewCart(Model model) {
        Map<Product, Integer> cartItems = cartService.getCartItems();
        BigDecimal total = cartService.getTotal();
        int itemCount = cartService.getItemCount();
        List<Category> allCategories = categoryService.getAllCategories();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("cartItemCount", itemCount);
        model.addAttribute("allCategories", allCategories);

        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.addProduct(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Product added to cart successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCartItem(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            RedirectAttributes redirectAttributes) {

        try {
            cartService.updateProductQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Cart updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(
            @RequestParam Long productId,
            RedirectAttributes redirectAttributes) {

        cartService.removeProduct(productId);
        redirectAttributes.addFlashAttribute("successMessage", "Product removed from cart");

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        cartService.clearCart();
        redirectAttributes.addFlashAttribute("successMessage", "Cart cleared successfully");

        return "redirect:/cart";
    }
}