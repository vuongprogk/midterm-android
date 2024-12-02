package vn.edu.stu.student.dh52112120.adapter;

import android.app.Activity;
import android.content.Context;
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
import vn.edu.stu.student.dh52112120.model.Product;

public class SpinnerAdapter extends ArrayAdapter<Category> {
    private Activity context;
    private int res;
    private List<Category> list;
    public SpinnerAdapter(@NonNull Activity context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.context = context;
        this.res = resource;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Inflate the custom layout
        LayoutInflater inflater = this.context.getLayoutInflater();
        View view = inflater.inflate(this.res, null);

        Category category = list.get(position);
        TextView spn_category = view.findViewById(R.id.spn_category);
        spn_category.setText(category.getName());



        return view;
    }
}
