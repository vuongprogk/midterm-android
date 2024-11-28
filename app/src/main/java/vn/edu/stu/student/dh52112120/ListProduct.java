package vn.edu.stu.student.dh52112120;

import android.app.AlertDialog;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.stu.student.dh52112120.adapter.ProductAdapter;
import vn.edu.stu.student.dh52112120.model.Category;
import vn.edu.stu.student.dh52112120.model.Product;

public class ListProduct extends AppCompatActivity {

    private ListView listViewProducts;
    private Spinner spinnerCategories;
    private Button btnAdd, btnUpdate, btnDelete;
    private ProductAdapter productAdapter;
    private List<Product> originalProductList;
    private List<Product> filteredProductList;
    private List<Category> categoryList;
    private Product selectedProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        getData();
        addEvents();
    }

    private void getData() {

        // Create product and category lists
        originalProductList = new ArrayList<>();
        categoryList = new ArrayList<>();

        // Create categories
        Category allCategory = new Category(0, "All Categories");
        Category electronicsCategory = new Category(1, "Electronics");
        Category clothingCategory = new Category(2, "Clothing");
        Category sportsCategory = new Category(3, "Sports");

        categoryList.add(allCategory);
        categoryList.add(electronicsCategory);
        categoryList.add(clothingCategory);
        categoryList.add(sportsCategory);

        // Add sample products
        originalProductList.add(new Product(
                1,
                "Smartphone",
                electronicsCategory,
                BitmapFactory.decodeResource(getResources(), R.drawable.default_product_image)
        ));

        originalProductList.add(new Product(
                2,
                "T-Shirt",
                clothingCategory,
                BitmapFactory.decodeResource(getResources(), R.drawable.default_product_image)
        ));

        // Setup Category Spinner
        List<String> categoryNames = categoryList.stream()
                .map(Category::getName)
                .collect(Collectors.toList());

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryNames
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(categoryAdapter);

        // Initial setup with all products
        filteredProductList = new ArrayList<>(originalProductList);
        productAdapter = new ProductAdapter(ListProduct.this, R.layout.item_list,filteredProductList);
        listViewProducts.setAdapter(productAdapter);

        // Set Spinner Item Selection Listener
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterProducts(categoryList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                filterProducts(categoryList.get(0));
            }
        });

        // ListView item selection
        listViewProducts.setOnItemClickListener((parent, view, position, id) -> {
            selectedProduct = filteredProductList.get(position);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });

        // Add Button Click Listener
        btnAdd.setOnClickListener(v -> showProductDialog(null));

        // Update Button Click Listener
        btnUpdate.setOnClickListener(v -> {
            if (selectedProduct != null) {
                showProductDialog(selectedProduct);
            } else {
                Toast.makeText(this, "Please select a product to update", Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Button Click Listener
        btnDelete.setOnClickListener(v -> {
            if (selectedProduct != null) {
                deleteProduct(selectedProduct);
            } else {
                Toast.makeText(this, "Please select a product to delete", Toast.LENGTH_SHORT).show();
            }
        });

        // Initially disable update and delete buttons
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }



    private void addControls() {
        // Initialize Views
        listViewProducts = findViewById(R.id.listview_products);
        spinnerCategories = findViewById(R.id.spinner_categories);
        btnAdd = findViewById(R.id.btn_add);
        btnUpdate = findViewById(R.id.btn_update);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void addEvents() {

    }
    private void showProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_product, null);
        builder.setView(dialogView);

        EditText etId = dialogView.findViewById(R.id.et_product_id);
        EditText etName = dialogView.findViewById(R.id.et_product_name);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinner_product_category);

        // Setup category spinner
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categoryList.stream()
                        .filter(c -> c.getId() != 0)
                        .map(Category::getName)
                        .collect(Collectors.toList())
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        // If updating an existing product
        if (product != null) {
            builder.setTitle("Update Product");
            etId.setText(String.valueOf(product.getId()));
            etId.setEnabled(false);
            etName.setText(product.getName());

            // Set selected category
            int categoryPosition = categoryList.stream()
                    .filter(c -> c.getId() == product.getCategory().getId())
                    .map(categoryList::indexOf)
                    .findFirst()
                    .orElse(0);
            spinnerCategory.setSelection(categoryPosition);
        } else {
            builder.setTitle("Add Product");
            etId.setEnabled(true);
        }

        builder.setPositiveButton(product != null ? "Update" : "Add", (dialog, which) -> {
            try {
                int id = Integer.parseInt(etId.getText().toString());
                String name = etName.getText().toString();
                Category selectedCategory = categoryList.get(
                        spinnerCategory.getSelectedItemPosition() + 1
                );

                if (product == null) {
                    // Add new product
                    Product newProduct = new Product(
                            id,
                            name,
                            selectedCategory,
                            BitmapFactory.decodeResource(getResources(), R.drawable.default_product_image)
                    );
                    addProduct(newProduct);
                } else {
                    // Update existing product
                    product.setName(name);
                    product.setCategory(selectedCategory);
                    updateProduct(product);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }

    private void addProduct(Product product) {
        originalProductList.add(product);
        filterProducts(spinnerCategories.getSelectedItemPosition() == 0
                ? categoryList.get(0)
                : categoryList.get(spinnerCategories.getSelectedItemPosition()));
        Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show();
    }

    private void updateProduct(Product product) {
        // Find and update the product in the original list
        for (int i = 0; i < originalProductList.size(); i++) {
            if (originalProductList.get(i).getId() == product.getId()) {
                originalProductList.set(i, product);
                break;
            }
        }

        filterProducts(spinnerCategories.getSelectedItemPosition() == 0
                ? categoryList.get(0)
                : categoryList.get(spinnerCategories.getSelectedItemPosition()));
        Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
    }

    private void deleteProduct(Product product) {
        originalProductList.remove(product);
        filterProducts(spinnerCategories.getSelectedItemPosition() == 0
                ? categoryList.get(0)
                : categoryList.get(spinnerCategories.getSelectedItemPosition()));

        // Reset selection
        selectedProduct = null;
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        Toast.makeText(this, "Product Deleted", Toast.LENGTH_SHORT).show();
    }

    private void filterProducts(Category selectedCategory) {
        if (selectedCategory.getId() == 0) {
            // All Categories selected
            filteredProductList = new ArrayList<>(originalProductList);
        } else {
            // Filter products by selected category
            filteredProductList = originalProductList.stream()
                    .filter(product -> product.getCategory().getId() == selectedCategory.getId())
                    .collect(Collectors.toList());
        }

        // Update the adapter with filtered list
        productAdapter = new ProductAdapter(ListProduct.this, R.layout.item_list,filteredProductList);
        listViewProducts.setAdapter(productAdapter);
    }
}