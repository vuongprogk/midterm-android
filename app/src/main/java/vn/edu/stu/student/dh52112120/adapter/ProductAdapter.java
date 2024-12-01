package vn.edu.stu.student.dh52112120.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.stu.student.dh52112120.R;
import vn.edu.stu.student.dh52112120.dbhelper.DatabaseHelper;
import vn.edu.stu.student.dh52112120.model.Category;
import vn.edu.stu.student.dh52112120.model.Product;


public class ProductAdapter extends ArrayAdapter<Product> {
    DatabaseHelper helper;
    private Activity context;
    private int res;
    private List<Product> productList;

    public ProductAdapter(@NonNull Activity context, int resource, @NonNull List<Product> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
        this.productList = objects;
        helper = new DatabaseHelper(context);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.res, null);

        // Get references to the views in the layout
        TextView tvId = view.findViewById(R.id.tv_product_id);
        TextView tvName = view.findViewById(R.id.tv_product_name);
        TextView tvCategory = view.findViewById(R.id.tv_product_category);
        ImageView ivProduct = view.findViewById(R.id.iv_product_image);

        // Get the current product
        Product currentProduct = productList.get(position);

        // Set the data
        tvId.setText(String.format("%s %d", tvId.getText().toString(), currentProduct.getId()));
        tvName.setText(String.format("%s %s", tvName.getText().toString(), currentProduct.getProductName()));
        Category category = helper.getCategoryById(currentProduct.getCategoryId());
        if(category != null){
            tvCategory.setText(String.format("%s %s", tvCategory.getText().toString(), category.getName()));
        }else {
            tvCategory.setText(R.string.not_found);
        }

        // Set the image
        if (currentProduct.getImageUrl() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentProduct.getImageUrl());
            ivProduct.setImageBitmap(bitmap);
        } else {
            // Set a default image if no image is available
            ivProduct.setImageResource(R.drawable.default_product_image);
        }

        return view;
    }
}
