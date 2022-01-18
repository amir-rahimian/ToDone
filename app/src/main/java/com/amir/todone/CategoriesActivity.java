package com.amir.todone;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.amir.todone.Adapters.CategoryListAdapter;
import com.amir.todone.Dialogs.AppDialog;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView categoryListView;
    private CategoryListAdapter categoryListAdapter;
    private List<Category> categoryList;
    private TextView txtCategoryHint;
    private EditText edtAddCategory;
    private Button btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        toolbar = findViewById(R.id.showTaskToolbar);
        categoryListView = findViewById(R.id.categoryList);
        txtCategoryHint = findViewById(R.id.txtCategoryPickerHint);
        edtAddCategory = findViewById(R.id.edtAddCategory);
        btnAdd = findViewById(R.id.btnAdd);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.categories));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        categoryList = CategoryManager.getInstance(this).getAllCategories();
        categoryListAdapter = new CategoryListAdapter(this, R.layout.add_category_layout, categoryList,
                new CategoryListAdapter.onSelectListener() {
                    @Override
                    public void done(Category category) {
                        if (category.getTaskCount() != 0) {
                            Intent intent = new Intent(CategoriesActivity.this, ShowTasksActivity.class);
                            intent.putExtra("is_forCategory", true);
                            intent.putExtra("category", category);
                            startActivity(intent);
                        }else {
                            Toast.makeText(CategoriesActivity.this, getResources().getString(R.string.noTask_toSee), Toast.LENGTH_SHORT).show();
                        }
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
            txtCategoryHint.setText(R.string.noCat_addOne);
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
    }

    private void addCategory(Category category) {
        if (categoryList.size() < 10 && !Exists(category)) {
            CategoryManager.getInstance(this).createCategory(category);
            categoryList.add(category);
            if (categoryList.size() == 1) {
                txtCategoryHint.setText(R.string.longPress_fot_edit_delete);
            }
            edtAddCategory.setText("");
            categoryListAdapter.notifyDataSetChanged();
            categoryListView.smoothScrollToPosition(categoryList.size() - 1);
        } else {
            Toast.makeText(this, getResources().getString(R.string.category_limit), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean Exists(Category category){
        for (Category c :
                categoryList) {
            if (c.getName().equals(category.getName())){
                Toast.makeText(this,  R.string.cat_exist, Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private void editCategory(int position) {
        // TODo : edit
        AppDialog appDialog = new AppDialog();
        appDialog.setTitle(getString(R.string.edit_category));
        appDialog.setMassage(getString(R.string.change_catName));
        appDialog.setInput(categoryList.get(position).getName(), getString(R.string.cat_name), null,
                newName -> {
                    CategoryManager.getInstance(CategoriesActivity.this)
                            .editCategoryName(categoryList.get(position), newName);
                    categoryListAdapter.notifyDataSetChanged();
                });
        appDialog.setOkButton(getString(R.string.change), null);
        appDialog.setHint(getString(R.string.try_use_short_names));
        appDialog.show(getSupportFragmentManager(), "EditCategory");
    }

    private void deleteCategory(int position) {
        AppDialog appDialog = new AppDialog();
        appDialog.setTitle(getString(R.string.delete_category));
        appDialog.setMassage(getString(R.string.sure_delete_category));
        appDialog.setCheckBox(getString(R.string.also_del_itsTasks), false,
                new AppDialog.onCheckResult() {
                    @Override
                    public void checkResult(boolean is_check) {
                        CategoryManager.getInstance(CategoriesActivity.this).deleteCategory(categoryList.get(position),is_check);
                        categoryList.remove(position);
                        if (categoryList.size() == 0) {
                            txtCategoryHint.setText(R.string.noCat_addOne);
                        }
                        categoryListAdapter.notifyDataSetChanged();
                    }
                });
        appDialog.setOkButton(getString(R.string.delete),null);
        appDialog.setCancelButton(getString(R.string.cancel), null);
        appDialog.show(getSupportFragmentManager(),"DeleteCategory");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}