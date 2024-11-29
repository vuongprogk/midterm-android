package vn.edu.stu.student.dh52112120.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String productName;
    private int categoryId;
    private String imageUrl; // Changed from String image
    private double price;
    private String description;

    public Product() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }



    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product(int id, String productName, int categoryId, String imageUrl, double price, String description) {
        this.id = id;
        this.productName = productName;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }
}
