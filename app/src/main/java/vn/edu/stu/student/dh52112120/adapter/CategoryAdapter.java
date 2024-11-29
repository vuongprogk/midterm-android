package vn.edu.stu.student.dh52112120.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.stu.student.dh52112120.R;
import vn.edu.stu.student.dh52112120.model.Category;

public class CategoryAdapter extends ArrayAdapter<Category> {
    private Activity context;
    private int res;
    private List<Category> categoryList;

    public CategoryAdapter(@NonNull Activity context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
        this.categoryList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the custom layout
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.res, null);

        // Get references to the views in the layout
        TextView tvId = view.findViewById(R.id.tv_category_id);
        TextView tvName = view.findViewById(R.id.tv_category_name);


        // Get the current product
        Category category = categoryList.get(position);

        // Set the data
        tvId.setText(String.valueOf(category.getId()));
        tvName.setText(category.getName());


        return view;
    }

}
