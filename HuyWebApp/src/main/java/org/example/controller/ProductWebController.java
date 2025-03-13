package org.example.controller;

import org.example.model.Category;
import org.example.model.Product;
import org.example.service.CartService;
import org.example.service.CategoryService;
import org.example.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/products")
public class ProductWebController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @Autowired
    public ProductWebController(ProductService productService, CategoryService categoryService, CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @GetMapping
    public String getAllProducts(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        List<Product> products = productService.getAllProducts();
        List<Category> allCategories = categoryService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", 1); // Trong thực tế, tính toán số trang dựa trên tổng số sản phẩm
        model.addAttribute("cartItemCount", cartService.getItemCount());

        return "products";
    }

    @GetMapping("/{id}")
    public String getProductDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        List<Category> allCategories = categoryService.getAllCategories();

        // Trong thực tế, tìm kiếm sản phẩm liên quan dựa trên danh mục hoặc các thuộc tính khác
        List<Product> relatedProducts = productService.getProductsByCategory(product.getCategory().getId())
                .stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .collect(Collectors.toList());

        model.addAttribute("product", product);
        model.addAttribute("relatedProducts", relatedProducts);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("cartItemCount", cartService.getItemCount());

        return "product-detail";
    }

    @GetMapping("/category/{categoryId}")
    public String getProductsByCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        List<Category> allCategories = categoryService.getAllCategories();

        Category currentCategory = categoryService.getCategoryById(categoryId);

        model.addAttribute("products", products);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("categoryName", currentCategory.getName());
        model.addAttribute("cartItemCount", cartService.getItemCount());

        return "products";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, Model model) {
        List<Product> products = productService.searchProducts(keyword);
        List<Category> allCategories = categoryService.getAllCategories();

        model.addAttribute("products", products);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("keyword", keyword);
        model.addAttribute("cartItemCount", cartService.getItemCount());

        return "products";
    }
}