package vn.edu.stu.student.dh52112120.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import vn.edu.stu.student.dh52112120.model.Category;
import vn.edu.stu.student.dh52112120.model.Product;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Info
    private static final String DATABASE_NAME = "Product";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_PRODUCT = "products";

    // Category Table Columns
    private static final String KEY_CATEGORY_ID = "id";
    private static final String KEY_CATEGORY_NAME = "category_name";

    // Product Table Columns
    private static final String KEY_PRODUCT_ID = "id";
    private static final String KEY_PRODUCT_NAME = "product_name";
    private static final String KEY_PRODUCT_CATEGORY_ID = "category_id";
    private static final String KEY_PRODUCT_IMAGE = "image";
    private static final String KEY_PRODUCT_PRICE = "price";
    private static final String KEY_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Category Table
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CATEGORY_NAME + " TEXT)";
        db.execSQL(CREATE_CATEGORY_TABLE);

        // Create Product Table
        String CREATE_PRODUCT_TABLE = "CREATE TABLE " + TABLE_PRODUCT + "("
                + KEY_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_PRODUCT_NAME + " TEXT, "
                + KEY_PRODUCT_CATEGORY_ID + " INTEGER, "
                + KEY_PRODUCT_IMAGE + " TEXT, "
                + KEY_PRODUCT_PRICE + " REAL, "
                + KEY_PRODUCT_DESCRIPTION + " TEXT, "
                + "FOREIGN KEY(" + KEY_PRODUCT_CATEGORY_ID + ") REFERENCES "
                + TABLE_CATEGORY + "(" + KEY_CATEGORY_ID + "))";
        db.execSQL(CREATE_PRODUCT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);

        // Create tables again
        onCreate(db);
    }

    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, category.getName());
        return db.insert(TABLE_CATEGORY, null, values);
    }

    public Category getCategoryById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORY,
                new String[]{KEY_CATEGORY_ID, KEY_CATEGORY_NAME},
                KEY_CATEGORY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Category category = new Category(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CATEGORY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME))
            );
            cursor.close();
            return category;
        }

        return null;
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category category = new Category(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_CATEGORY_NAME))
                );
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return categoryList;
    }

    public int updateCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_NAME, category.getName());

        return db.update(TABLE_CATEGORY, values,
                KEY_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(category.getId())});
    }

    public long deleteCategory(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CATEGORY, KEY_CATEGORY_ID + " = ?",
                new String[]{String.valueOf(id)});

    }

    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_NAME, product.getProductName());
        values.put(KEY_PRODUCT_CATEGORY_ID, product.getId());

        values.put(KEY_PRODUCT_IMAGE, product.getImageUrl());

        values.put(KEY_PRODUCT_PRICE, product.getPrice());
        values.put(KEY_PRODUCT_DESCRIPTION, product.getDescription());

        return db.insert(TABLE_PRODUCT, null, values);
    }

    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRODUCT,
                new String[]{KEY_PRODUCT_ID, KEY_PRODUCT_NAME, KEY_PRODUCT_CATEGORY_ID,
                        KEY_PRODUCT_IMAGE, KEY_PRODUCT_PRICE, KEY_PRODUCT_DESCRIPTION},
                KEY_PRODUCT_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            Product product = new Product(
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_NAME)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_CATEGORY_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_IMAGE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRODUCT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_DESCRIPTION))
            );
            cursor.close();
            return product;
        }

        return null;
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Product product = new Product(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRODUCT_CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_IMAGE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_PRODUCT_PRICE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_PRODUCT_DESCRIPTION))
                );
                productList.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return productList;
    }


    public int updateProduct(Product product) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRODUCT_NAME, product.getProductName());
        values.put(KEY_PRODUCT_CATEGORY_ID, product.getId());
        values.put(KEY_PRODUCT_IMAGE, product.getImageUrl());
        values.put(KEY_PRODUCT_PRICE, product.getPrice());
        values.put(KEY_PRODUCT_DESCRIPTION, product.getDescription());

        return db.update(TABLE_PRODUCT, values,
                KEY_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(product.getId())});
    }

    public long deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCT, KEY_PRODUCT_ID + " = ?",
                new String[]{String.valueOf(id)});

    }

}
