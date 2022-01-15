package com.amir.todone.Dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.R;
import com.amir.todone.Adapters.CategoryListAdapter;
import com.amir.todone.Domain.Category.Category;

import java.util.List;

public class CategoryPickerDialog extends DialogFragment {

    public interface onSelectListener {
        void done(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private onSelectListener onSelectListener;

    private ListView categoryListView;
    private TextView txtCategoryHint;
    private Button btnAdd;
    private EditText edtAddCategory;
    private CategoryListAdapter categoryListAdapter;

    public CategoryPickerDialog(Context context, List<Category> categoryList, CategoryPickerDialog.onSelectListener onSelectListener) {
        this.context = context;
        this.categoryList = categoryList;
        this.onSelectListener = onSelectListener;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_MaterialComponents_BottomSheetDialog);
        View view = getLayoutInflater().inflate(R.layout.category_picker_dialog, null);
        categoryListView = view.findViewById(R.id.categoryList);
        btnAdd = view.findViewById(R.id.btnAdd);
        txtCategoryHint = view.findViewById(R.id.txtCategoryPickerHint);
        edtAddCategory = view.findViewById(R.id.edtAddCategory);


        categoryListAdapter = new CategoryListAdapter(context, R.layout.add_category_layout, categoryList,
                new CategoryListAdapter.onSelectListener() {
                    @Override
                    public void done(Category category) {
                        onSelectListener.done(category);
                        dismiss();
                    }

                    @Override
                    public void delete(int position) {
                        deleteCategory(position);
                    }

                    @Override
                    public void edit(int position) {
                        editCategory(position);
                    }
                });
        categoryListView.setAdapter(categoryListAdapter);
        if (categoryList.size() == 0) {
            txtCategoryHint.setText("No Categories. add one :");
        }
        edtAddCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (start == 0 && before == 0 && count > 0) {
                    btnAdd.setEnabled(true);
                    btnAdd.animate().alpha(1.0f);
                }
                if (start == 0 && before > 0 && count == 0) {
                    btnAdd.setEnabled(false);
                    btnAdd.animate().alpha(0.5f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory(new Category(edtAddCategory.getText().toString().trim()));
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private void addCategory(Category category) {
        if (categoryList.size() < 10 && !Exists(category)) {
            CategoryManager.getInstance(context).createCategory(category);
            categoryList.add(category);
            if (categoryList.size() == 1) {
                txtCategoryHint.setText("Long press on items inorder to Edit or Delete category or add new one :");
            }
            edtAddCategory.setText("");
            categoryListAdapter.notifyDataSetChanged();
            categoryListView.smoothScrollToPosition(categoryList.size() - 1);
        } else {
            Toast.makeText(getContext(), "You have reached the limit. Delete or edit one", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean Exists(Category category){
        for (Category c :
                categoryList) {
            if (c.getName().equals(category.getName())){
                Toast.makeText(getContext(), "This category already exists!", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void editCategory(int position) {
        // TODo : edit
        AppDialog appDialog = new AppDialog();
        appDialog.setTitle("Edit category");
        appDialog.setMassage("change your category name");
        appDialog.setInput(categoryList.get(position).getName(), "Category name", null,
                newName -> {
                    CategoryManager.getInstance(context)
                            .editCategoryName(categoryList.get(position), newName);
                    categoryListAdapter.notifyDataSetChanged();
                });
        appDialog.setOkButton("Change", null);
        appDialog.setHint("Try to use short names with meaning");
        appDialog.show(requireActivity().getSupportFragmentManager(), "EditCategory");
    }

    private void deleteCategory(int position) {
        AppDialog appDialog = new AppDialog();
        appDialog.setTitle("Delete Category");
        appDialog.setMassage("Are you sure you want to delete " + categoryList.get(position).getName() + " Category ?");
        appDialog.setCheckBox("Also delete its Task", false,
                new AppDialog.onCheckResult() {
                    @Override
                    public void checkResult(boolean is_check) {
                        CategoryManager.getInstance(context).deleteCategory(categoryList.get(position), is_check);
                        categoryList.remove(position);
                        if (categoryList.size() == 0) {
                            txtCategoryHint.setText("No Categories. add one :");
                        }
                        categoryListAdapter.notifyDataSetChanged();
                    }
                });
        appDialog.setOkButton("Delete", null);
        appDialog.setCancelButton("Cancel", null);
        appDialog.show(requireActivity().getSupportFragmentManager(), "DeleteCategory");
    }
}
