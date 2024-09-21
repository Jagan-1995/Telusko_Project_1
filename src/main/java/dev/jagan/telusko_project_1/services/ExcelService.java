package dev.jagan.telusko_project_1.services;

import dev.jagan.telusko_project_1.models.Product;
import dev.jagan.telusko_project_1.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
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
            Sheet sheet = workbook.getSheet("Product"); // Assumes the sheet name is "Product"

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row

                Product product = new Product();

                // Read 'name' field (Column 1)
                Cell nameCell = row.getCell(0);
                if (nameCell != null) {
                    if (nameCell.getCellType() == CellType.STRING) {
                        product.setName(nameCell.getStringCellValue());
                    } else if (nameCell.getCellType() == CellType.NUMERIC) {
                        product.setName(String.valueOf((int) nameCell.getNumericCellValue())); // Convert numeric to string
                    } else {
                        throw new Exception("Invalid data format in 'Name' column");
                    }
                } else {
                    throw new Exception("Name column is empty");
                }

                // Read 'price' field (Column 2)
                Cell priceCell = row.getCell(1);
                if (priceCell != null) {
                    try {
                        if (priceCell.getCellType() == CellType.NUMERIC) {
                            product.setPrice(priceCell.getNumericCellValue());
                        } else if (priceCell.getCellType() == CellType.STRING) {
                            // Clean up the price string by removing unwanted characters
                            String priceString = priceCell.getStringCellValue().replaceAll("[^\\d.]", "");
                            product.setPrice(Double.parseDouble(priceString));
                        } else {
                            throw new Exception("Invalid data format in 'Price' column");
                        }
                    } catch (NumberFormatException e) {
                        throw new Exception("Invalid numeric format in 'Price' column");
                    }
                } else {
                    throw new Exception("Price column is empty");
                }

                // Read 'quantity' field (Column 3)
                Cell quantityCell = row.getCell(2);
                if (quantityCell != null && quantityCell.getCellType() == CellType.NUMERIC) {
                    product.setQuantity((int) quantityCell.getNumericCellValue());
                } else {
                    throw new Exception("Invalid data format in 'Quantity' column");
                }

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
