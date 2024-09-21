package dev.jagan.telusko_project_1.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends BaseModel{
    private String name;
    private Double price;
    private Integer quantity;
}
