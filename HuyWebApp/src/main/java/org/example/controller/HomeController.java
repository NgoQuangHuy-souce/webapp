package org.example.controller;

import org.example.model.Category;
import org.example.model.Product;
import org.example.service.CartService;
import org.example.service.CategoryService;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @Autowired
    public HomeController(ProductService productService, CategoryService categoryService, CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredProducts = productService.getAllProducts(); // Trong thực tế, bạn có thể lọc sản phẩm nổi bật
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("featuredProducts", featuredProducts);
        model.addAttribute("categories", categories);
        model.addAttribute("cartItemCount", cartService.getItemCount());

        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("cartItemCount", cartService.getItemCount());
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("cartItemCount", cartService.getItemCount());
        return "contact";
    }
}