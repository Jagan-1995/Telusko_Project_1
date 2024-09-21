package dev.jagan.telusko_project_1.services;

import dev.jagan.telusko_project_1.models.Product;
import dev.jagan.telusko_project_1.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private ProductRepository productRepository;

    public ExcelService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public List<Product> processExcelFile(MultipartFile file) throws Exception {
        List<Product> productList = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet("Product");

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Product product = new Product();
                product.setName(row.getCell(0).getStringCellValue());
                product.setPrice(row.getCell(1).getNumericCellValue());
                product.setQuantity((int) row.getCell(2).getNumericCellValue());
                productList.add(product);
            }
        } catch (Exception e) {
            throw new Exception("Failed to process Excel file: " + e.getMessage());
        }
        productRepository.saveAll(productList);
        return productList;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

}
