package com.amir.todone.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amir.todone.R;
import com.amir.todone.Domain.Category.Category;

import java.util.List;

public class CategoryListAdapter extends ArrayAdapter<Category> {

    public interface onSelectListener {
        void done(Category category);
        void delete(int position);
        void edit(int position);
    }

    private Context context;
    private int resource;
    private onSelectListener onSelectListener;
    public CategoryListAdapter(@NonNull Context context, int resource , List<Category> categories , onSelectListener onSelectListener) {
        super(context, resource , categories);
        this.context = context;
        this.resource = resource;
        this.onSelectListener  = onSelectListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(resource, parent,false);

        Category category = getItem(position);


        TextView txtCategory = convertView.findViewById(R.id.txtCategoryName);
        TextView txtCategoryCount = convertView.findViewById(R.id.txtCategoryCount);

        convertView.getRootView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.delete){
                            onSelectListener.delete(position);
                        }else {
                            onSelectListener.edit(position);
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        convertView.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectListener.done(category);
            }
        });

        txtCategory.setText(category.getName());
        txtCategoryCount.setText(category.getTaskCount()+"");

        return convertView;
    }
}
