package dev.jagan.telusko_project_1.repositories;

import dev.jagan.telusko_project_1.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

}
