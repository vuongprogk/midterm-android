package vn.edu.stu.student.dh52112120;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import vn.edu.stu.student.dh52112120.adapter.ProductAdapter;
import vn.edu.stu.student.dh52112120.dbhelper.DatabaseHelper;
import vn.edu.stu.student.dh52112120.model.Product;

public class ListProduct extends AppCompatActivity {
    public static final int REQUEST_ADD_PRODUCT = 1;
    public static final int REQUEST_EDIT_PRODUCT = 2;
    Toolbar toolbar;
    Product selectedProduct = null;
    private ListView productListView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private Button btnAddProduct;
    private DatabaseHelper dbHelper;


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
        loadProducts();
        addEvens();
    }

    private void addControls() {
        toolbar = findViewById(R.id.tool_product);
        setSupportActionBar(toolbar);
        productListView = findViewById(R.id.product_list_view);
        btnAddProduct = findViewById(R.id.btn_add_product);
        dbHelper = new DatabaseHelper(ListProduct.this);
        productList = dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(ListProduct.this, R.layout.product_item_list, productList);
        productListView.setAdapter(productAdapter);
    }

    private void addEvens() {
        btnAddProduct.setOnClickListener(view -> processAdd());
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProduct = productList.get(i);
                processUpdate();
            }
        });
        productListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProduct = productList.get(i);
                confirmDeleteProduct(selectedProduct);
                return true;
            }
        });
    }

    private void processUpdate() {
        if (dbHelper.getAllCategories().isEmpty()) {
            Toast.makeText(ListProduct.this, R.string.no_category, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent editIntent = new Intent(ListProduct.this, UpsertViewProduct.class);
        editIntent.putExtra("product", selectedProduct);
        launcher.launch(editIntent);
    }

    private void processAdd() {
        if (dbHelper.getAllCategories().isEmpty()) {
            Toast.makeText(ListProduct.this, R.string.no_category, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent editIntent = new Intent(ListProduct.this, UpsertViewProduct.class);
        launcher.launch(editIntent);
    }

    private void loadProducts() {
        productList = dbHelper.getAllProducts();
        productAdapter = new ProductAdapter(ListProduct.this, R.layout.product_item_list, productList);
        productListView.setAdapter(productAdapter);
    }

    private void confirmDeleteProduct(final Product product) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_product)
                .setMessage(R.string.delete_product_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete product from database
                        long result = dbHelper.deleteProduct(product.getId());

                        if (result > 0) {
                            // Remove from list and refresh adapter
                            productList.remove(product);
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
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
        if(item.getItemId() == R.id.mnu_product){
            Intent intent = new Intent(getApplicationContext(), ListProduct.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnu_category) {
            Intent intent = new Intent(getApplicationContext(), ListCategory.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (item.getItemId() == R.id.mnu_about) {
            Intent intent = new Intent(getApplicationContext(), About.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if (o.getResultCode() == REQUEST_ADD_PRODUCT) {
                        Product product = (Product) o.getData().getSerializableExtra("product");
                        long result = dbHelper.addProduct(product);
                        if (result > 0) {
                            Toast.makeText(getApplicationContext(), R.string.product_save_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.product_save_fail, Toast.LENGTH_SHORT).show();
                        }

                    } else if (o.getResultCode() == REQUEST_EDIT_PRODUCT) {
                        Product product = (Product) o.getData().getSerializableExtra("product");
                        dbHelper.updateProduct(product);
                    }
                    loadProducts();
                }
            }
    );
}