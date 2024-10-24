package com.example.bestStore.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.bestStore.model.Product;
import com.example.bestStore.model.dto.ProductDTO;
import com.example.bestStore.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({ "", "/" })
    public String showProductList(Model model) {
        List<Product> products = this.productService.fetchAllProduct();
        model.addAttribute("products", products);
        return "products/index";

    }

    @GetMapping("/create")
    public String showCreatePage(Model model) {
        ProductDTO productDTO = new ProductDTO();
        model.addAttribute("productDTO", productDTO);

        return "products/CreateProduct";
    }

    @PostMapping("/create")
    public String postCreateProduct(
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result

    ) {

        if (productDTO.getImageFile().isEmpty()) {
            result.addError(new FieldError("productDto", "imageFile", "The image file is required"));
        }

        if (result.hasErrors()) {
            return "products/CreateProduct";
        }

        MultipartFile image = productDTO.getImageFile();
        Date createAt = new Date();
        String storageFileName = createAt.getTime() + "-" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images/";
            Path updaloadPath = Paths.get(uploadDir);
            if (!Files.exists(updaloadPath)) {
                Files.createDirectories(updaloadPath);
            }

            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            System.out.println("Exeption: " + ex.getMessage());
        }

        Product product = new Product();
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setCategory(productDTO.getCategory());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setCreateAt(createAt);
        product.setImageFileName(storageFileName);

        this.productService.saveProduct(product);
        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(Model model, @RequestParam long id) {

        try {
            Product product = (Product) this.productService.findById(id).get();
            model.addAttribute("product", product);

            ProductDTO productDTO = new ProductDTO();
            productDTO.setName(product.getName());
            productDTO.setBrand(product.getBrand());
            productDTO.setCategory(product.getCategory());
            productDTO.setPrice(product.getPrice());
            productDTO.setDescription(product.getDescription());

            model.addAttribute("productDTO", productDTO);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return "redirect:/products";
        }

        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(
            Model model,
            @RequestParam long id,
            @Valid @ModelAttribute ProductDTO productDTO,
            BindingResult result

    ) {
        try {
            Product product = (Product) this.productService.findById(id).get();
            model.addAttribute("product", product);
            if (result.hasErrors()) {
                return "products/EditProduct";
            }

            if (!productDTO.getImageFile().isEmpty()) {
                String uploadDir = "public/images/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());

                try {
                    Files.delete(oldImagePath);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                MultipartFile image = productDTO.getImageFile();
                Date createAt = new Date();
                String storageFileName = createAt.getTime() + "-" + image.getOriginalFilename();
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                            StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(storageFileName);
            }

            product.setName(productDTO.getName());
            product.setBrand(productDTO.getBrand());
            product.setCategory(productDTO.getCategory());
            product.setPrice(productDTO.getPrice());
            product.setDescription(productDTO.getDescription());

            this.productService.saveProduct(product);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "redirect:/products";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam long id) {
        Product product = (Product) this.productService.findById(id).get();
        Path imagePath = Paths.get("public/images/" + product.getImageFileName());
        try {
            Files.delete(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.productService.deleteProduct(product);

        return "redirect:/products";
    }

}
