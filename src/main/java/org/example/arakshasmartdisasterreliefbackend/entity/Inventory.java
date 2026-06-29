package org.example.arakshasmartdisasterreliefbackend.entity;


import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private Integer count;

    private Integer total;

    private String unit;

    private Integer allocated;

    private Integer minStock;

    public Inventory() {
    }

    public Inventory(Long id, String name, String category,
                     Integer count, Integer total,
                     String unit, Integer allocated,
                     Integer minStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.count = count;
        this.total = total;
        this.unit = unit;
        this.allocated = allocated;
        this.minStock = minStock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getAllocated() {
        return allocated;
    }

    public void setAllocated(Integer allocated) {
        this.allocated = allocated;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }
}
