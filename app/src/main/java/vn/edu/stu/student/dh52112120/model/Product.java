package vn.edu.stu.student.dh52112120.model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Product implements Serializable {
    private  int id;
    private String name;
    private Category category;
    private Bitmap image;
    private float price;
    private String describe;

    public Product() {
    }

    public Product(int id, String name, Category category, Bitmap image) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public Product(int id, String name, Category category, Bitmap image, float price, String describe) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.image = image;
        this.price = price;
        this.describe = describe;
    }
}
