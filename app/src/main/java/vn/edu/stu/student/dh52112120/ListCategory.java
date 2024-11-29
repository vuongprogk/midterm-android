package vn.edu.stu.student.dh52112120;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import vn.edu.stu.student.dh52112120.adapter.CategoryAdapter;
import vn.edu.stu.student.dh52112120.dbhelper.DatabaseHelper;
import vn.edu.stu.student.dh52112120.model.Category;

public class ListCategory extends AppCompatActivity {

    Toolbar toolbar;
    DatabaseHelper helper;
    private ListView listview_categories;
    private Button btn_add, btn_update, btn_delete;
    private CategoryAdapter adapter;
    private List<Category> category_list;
    private Category selected_category = null;
    private EditText edt_category_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addControls();
        addEvents();
    }


    private void addControls() {
        toolbar = findViewById(R.id.tool_category);
        setSupportActionBar(toolbar);
        helper = new DatabaseHelper(ListCategory.this);
        category_list = helper.getAllCategories();
        adapter = new CategoryAdapter(ListCategory.this, R.layout.category_item_list, category_list);
        listview_categories = findViewById(R.id.listview_categories);
        listview_categories.setAdapter(adapter);
        btn_add = findViewById(R.id.btn_add);
        btn_update = findViewById(R.id.btn_update);
        btn_delete = findViewById(R.id.btn_delete);
        edt_category_name = findViewById(R.id.edt_category_name);
    }

    private void addEvents() {
        // ListView item selection
        listview_categories.setOnItemClickListener((parent, view, position, id) -> {
            updateViewData(position);
            btn_update.setEnabled(true);
            btn_delete.setEnabled(true);
        });

        // Add Button Click Listener
        btn_add.setOnClickListener(v -> processAdd());

        // Update Button Click Listener
        btn_update.setOnClickListener(v -> {
            if (selected_category != null) {
                processUpdate(selected_category);
            } else {
                Toast.makeText(this, R.string.check_category, Toast.LENGTH_SHORT).show();
            }
        });

        // Delete Button Click Listener
        btn_delete.setOnClickListener(v -> {
            if (selected_category != null) {
//                deleteProduct(selectedCategory);
                confirmDeleteProduct(selected_category);
            } else {
                Toast.makeText(this, R.string.check_category_delete, Toast.LENGTH_SHORT).show();
            }
        });

        // Initially disable update and delete buttons
        btn_update.setEnabled(false);
        btn_delete.setEnabled(false);
    }
    private void confirmDeleteProduct(final Category category) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_delete_category)
                .setMessage(R.string.delete_category_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Delete product from database
                        long result = helper.deleteCategory(category.getId());

                        if (result > 0) {
                            // Remove from list and refresh adapter
                            category_list.remove(category);
                            adapter.notifyDataSetChanged();
                        }else {
                            Toast.makeText(getApplicationContext(), R.string.delete_fail, Toast.LENGTH_SHORT).show();
                        }
                        updateText("");
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
    private void updateViewData(int position) {
        if (position >= 0 & position < category_list.size()) {
            selected_category = category_list.get(position);
            updateText(selected_category.getName());
        }
    }
    

    private void processUpdate(Category selectedCategory) {
        String name = edt_category_name.getText().toString();
        selectedCategory.setName(name);
        helper.updateCategory(selectedCategory);
        updateText("");
        display();
    }


    private void processAdd() {
        String name = edt_category_name.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.miss_field, Toast.LENGTH_SHORT).show();
            return;
        }
        Category category = new Category();
        category.setName(name);
        helper.addCategory(category);
        updateText("");
        display();
    }

    private void display() {
        category_list = helper.getAllCategories();
        adapter = new CategoryAdapter(ListCategory.this, R.layout.category_item_list, category_list);
        listview_categories = findViewById(R.id.listview_categories);
        listview_categories.setAdapter(adapter);
    }

    private void updateText(String text) {
        edt_category_name.setText(text);
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
}