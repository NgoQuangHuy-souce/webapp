package org.example.controller;

import org.example.model.*;
import org.example.service.CartService;
import org.example.service.CategoryService;
import org.example.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    @Autowired
    public CheckoutController(CartService cartService, OrderService orderService, CategoryService categoryService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String checkout(Model model, HttpSession session) {
        if (cartService.isEmpty()) {
            return "redirect:/cart";
        }

        Map<Product, Integer> cartItems = cartService.getCartItems();
        BigDecimal total = cartService.getTotal();
        int itemCount = cartService.getItemCount();
        List<Category> allCategories = categoryService.getAllCategories();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("cartItemCount", itemCount);
        model.addAttribute("allCategories", allCategories);

        return "checkout";
    }

    @PostMapping("/process")
    public String processCheckout(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipCode,
            @RequestParam String country,
            @RequestParam String paymentMethod,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra giỏ hàng trống
            if (cartService.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Your cart is empty");
                return "redirect:/cart";
            }

            // Lấy thông tin người dùng
            User user = (User) session.getAttribute("user");
            Long userId = (user != null) ? user.getId() : null;

            // Nếu người dùng chưa đăng nhập, bạn có thể tạo một người dùng tạm thời hoặc yêu cầu đăng nhập
            if (userId == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please login to checkout");
                return "redirect:/login?redirect=checkout";
            }

            // Chuẩn bị thông tin đơn hàng
            String fullAddress = address + ", " + city + ", " + state + ", " + zipCode + ", " + country;
            Map<Product, Integer> cartItems = cartService.getCartItems();

            // Chuyển đổi từ Map<Product, Integer> sang List<OrderItem>
            List<OrderItem> orderItems = new ArrayList<>();
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();

                OrderItem orderItem = new OrderItem();
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());

                orderItems.add(orderItem);
            }

            // Tạo đơn hàng
            Order order = orderService.createOrder(userId, orderItems, fullAddress);

            // Lưu thông tin đơn hàng vào session
            session.setAttribute("lastOrderId", order.getId());

            // Xóa giỏ hàng
            cartService.clearCart();

            return "redirect:/checkout/confirmation";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error processing your order: " + e.getMessage());
            return "redirect:/checkout";
        }
    }

    @GetMapping("/confirmation")
    public String confirmation(Model model, HttpSession session) {
        Long orderId = (Long) session.getAttribute("lastOrderId");
        if (orderId == null) {
            return "redirect:/";
        }

        Order order = orderService.getOrderById(orderId);
        List<Category> allCategories = categoryService.getAllCategories();

        model.addAttribute("order", order);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("cartItemCount", 0); // Giỏ hàng đã được xóa

        return "order-confirmation";
    }
}