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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.amir.todone.R;
import com.amir.todone.Adapters.CategoryListAdapter;
import com.amir.todone.Domain.Category;

import java.util.List;

public class CategoryPickerDialog extends DialogFragment {

    public interface onSelectListener {
        void done(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private onSelectListener onSelectListener;

    private ListView categoryListView;
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
                try {
                    addCategory(new Category(edtAddCategory.getText().toString().trim()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setView(view);
        return builder.create();
    }

    private void addCategory(Category category) {
        if (categoryList.size() < 10) {
            categoryList.add(category);
            edtAddCategory.setText("");
            categoryListAdapter.notifyDataSetChanged();
            categoryListView.smoothScrollToPosition(categoryList.size() - 1);
        }else {
            Toast.makeText(getContext(), "You have reached the limit. Delete or edit one", Toast.LENGTH_SHORT).show();
        }
    }

    private void editCategory(int position) {
        // TODo : edit
        CustomDialog customDialog = new CustomDialog(context);
        customDialog.setTitle("Edit category");
        customDialog.setMassage("change your category name");
        customDialog.setInput(categoryList.get(position).getName(), "Category name", null,
                newName ->{
                    categoryList.get(position).editname(newName);
                    categoryListAdapter.notifyDataSetChanged();
                });
        customDialog.setOkButton("Change", null);
        customDialog.setHint("Try to use short names with meaning");
        customDialog.show(requireActivity().getSupportFragmentManager(), "EditCategory");
    }

    private void deleteCategory(int position) {
        categoryList.remove(position);
        categoryListAdapter.notifyDataSetChanged();
    }
}
