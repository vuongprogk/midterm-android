package vn.edu.stu.student.dh52112120;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import vn.edu.stu.student.dh52112120.dbhelper.DatabaseHelper;
import vn.edu.stu.student.dh52112120.model.Category;
import vn.edu.stu.student.dh52112120.model.Product;

public class UpsertViewProduct extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etProductName, etPrice, etDescription;
    private Spinner spinnerCategory;
    private ImageView ivProductImage;
    private Button btnSelectImage, btnSave, btnAdd;
    private String filePath = "";
    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            if (selectedImageUri != null) {
                                filePath = getFilePathFromUri(selectedImageUri);
                                Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                                ivProductImage.setImageBitmap(bitmap);
                            }
                        }
                    }
                }
            }
    );
    private DatabaseHelper dbHelper;
    private Product existingProduct;
    private int PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upsert_view_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        getIntentData();
        addEvents();
        checkStoragePermission();

    }

    private void getIntentData() {
        Intent intent = getIntent();
        existingProduct = (Product) intent.getSerializableExtra("product");

        if (existingProduct != null) {
            etProductName.setText(existingProduct.getProductName());
            etPrice.setText(String.valueOf(existingProduct.getPrice()));
            etDescription.setText(existingProduct.getDescription());
            Bitmap bitmap = BitmapFactory.decodeFile(existingProduct.getImageUrl());
            ivProductImage.setImageBitmap(bitmap);

        }else {
            btnSave.setEnabled(false);
        }
    }

    private void addEvents() {
        // Image selection listener
        btnSelectImage.setOnClickListener(v -> openImagePicker());

        // Save button listener
        btnSave.setOnClickListener(v -> saveProduct(false));
        btnAdd.setOnClickListener(v -> saveProduct(true));
    }

    private void addControls() {
        dbHelper = new DatabaseHelper(UpsertViewProduct.this);
        etProductName = findViewById(R.id.etProductName);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        ivProductImage = findViewById(R.id.ivProductImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSave = findViewById(R.id.btnSave);
        btnAdd = findViewById(R.id.btnAdd);

        List<Category> categories = dbHelper.getAllCategories();

        // Create adapter for category IDs
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);

        // Set existing category if editing
        if (existingProduct != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).getId() == existingProduct.getCategoryId()) {
                    spinnerCategory.setSelection(i);
                    break;
                }
            }
        }
    }

    private void openImagePicker() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePicker.launch(intent);
    }

    private void saveProduct(boolean add) {
        // Validate inputs
        String name = etProductName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryId = selectedCategory.getId();

        if (name.isEmpty() || priceStr.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, R.string.miss_field, Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.invalid_price, Toast.LENGTH_SHORT).show();
            return;
        }

        Product product = new Product();
        product.setProductName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategoryId(categoryId);
        if (!filePath.isEmpty()) {

            product.setImageUrl(filePath);
        }else {
            product.setImageUrl(existingProduct.getImageUrl());
        }

        int responseCode = ListProduct.REQUEST_ADD_PRODUCT;
        if (existingProduct != null && !add) {
            // Update existing product
            product.setId(existingProduct.getId());
            responseCode = ListProduct.REQUEST_EDIT_PRODUCT;
        }
        Intent intent = new Intent();
        intent.putExtra("product", product);
        setResult(responseCode, intent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(
                R.menu.menu_main,
                menu
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnu_product) {
            Intent intent = new Intent(getApplicationContext(), ListProduct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnu_category) {
            Intent intent = new Intent(getApplicationContext(), ListCategory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnu_about) {
            Intent intent = new Intent(getApplicationContext(), About.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private String getFilePathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(columnIndex);
            cursor.close();
            return filePath;
        }
        return "";
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, R.string.storage_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }
}