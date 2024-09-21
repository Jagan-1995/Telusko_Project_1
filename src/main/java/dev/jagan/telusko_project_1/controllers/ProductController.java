package dev.jagan.telusko_project_1.controllers;

import dev.jagan.telusko_project_1.models.Product;
import dev.jagan.telusko_project_1.services.ExcelService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileWriter;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ExcelService excelService;

    public ProductController(ExcelService excelService) {
        this.excelService = excelService;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try {
            excelService.processExcelFile(file);
            return ResponseEntity.ok("File uploaded and data stored successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> downloadProductData() {
        try {
            List<Product> productList = excelService.getAllProducts();
            String jsonFilePath = "products.json";
            try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
                fileWriter.write(productList.toString()); // Simple JSON representation
            }
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.json")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(productList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error occurred while downloading data.");
        }
    }
}
