package com.amir.todone.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amir.todone.Adapters.TaskRvAdapter;
import com.amir.todone.Dialogs.AppDialog;
import com.amir.todone.Domain.Category.Category;
import com.amir.todone.Domain.Category.CategoryManager;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.R;
import com.amir.todone.ShowTasksActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private RecyclerView todayRV, tomorrowRV, othersRV;
    private TextView  txtTodayCount, txtTomorrowCount;
    private ConstraintLayout  todayTitle, tomorrowTitle, othersTitle;
    private LinearLayout addNewLayout;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private NestedScrollView homeSV;

    private List<Task> todayTasks, tomorrowTasks, otherTasks;


    private TaskRvAdapter todayAdapter, tomorrowAdapter, otherAdapter;
    private boolean[] have = {false, false, false};

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        addNewLayout = v.findViewById(R.id.addNewLayout);
        homeSV = v.findViewById(R.id.homeSV);

        todayRV = v.findViewById(R.id.todayRV);
        todayRV.setVisibility(View.GONE);
        tomorrowRV = v.findViewById(R.id.tomorrowRV);
        tomorrowRV.setVisibility(View.GONE);
        othersRV = v.findViewById(R.id.othersRV);
        othersRV.setVisibility(View.GONE);

        txtTodayCount = v.findViewById(R.id.txtTodayCount);
        txtTomorrowCount = v.findViewById(R.id.txtTomorrowCount);
        todayTitle = v.findViewById(R.id.todayTitle);
        todayTitle.setVisibility(View.GONE);
        tomorrowTitle = v.findViewById(R.id.tomorrowTitle);
        tomorrowTitle.setVisibility(View.GONE);
        othersTitle = v.findViewById(R.id.othersTitle);
        othersTitle.setVisibility(View.GONE);

        collapsingToolbarLayout = v.findViewById(R.id.homeCollapsingToolbar);
        appBarLayout = v.findViewById(R.id.homeAppbar);

        loadTasks();

        homeSV.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            private boolean lastTodayVisible;
            private boolean lastTomorrowVisible;
            private boolean lastOthersVisible;

            @Override
            public void onScrollChanged() {
                Rect scrollBounds = new Rect();
                homeSV.getHitRect(scrollBounds);
                if (todayTitle.getLocalVisibleRect(scrollBounds)) {
                    if (!lastTodayVisible) setTitle("Your Tasks");
                } else {
                    if (lastTodayVisible) setTitle("Today");
                    else {
                        if (tomorrowTitle.getLocalVisibleRect(scrollBounds)) {
                            if (!lastTomorrowVisible) if (have[0]) setTitle("Today"); else setTitle("Your Tasks");
                        } else {
                            if (lastTomorrowVisible) setTitle("Tomorrow");
                            else {
                                if (othersTitle.getLocalVisibleRect(scrollBounds)) {
                                    if (!lastOthersVisible) if (have[1]) setTitle("Your Tasks"); else setTitle("Your Tasks");
                                } else if (lastOthersVisible) setTitle("Others");
                            }
                        }
                    }
                }
                lastTodayVisible = todayTitle.getLocalVisibleRect(scrollBounds);
                lastTomorrowVisible = tomorrowTitle.getLocalVisibleRect(scrollBounds);
                lastOthersVisible = othersTitle.getLocalVisibleRect(scrollBounds);
            }
        });

        return v;
    }

    private void setTitle(String title) {
        if (!have[0] && !have[1] && !have[2])
            collapsingToolbarLayout.setTitle("You have no Task !");
        else
            collapsingToolbarLayout.setTitle(title);

    }

    public void loadTasks() {
        TaskManager taskManager = TaskManager.getInstance(getContext());
        todayTasks = taskManager.geTodayTasks();
        tomorrowTasks = taskManager.geTomorrowTasks();
        otherTasks = taskManager.geOtherTasks();

        if (todayTasks.size() > 0) {
            have[0] = true;
            todayTitle.setVisibility(View.VISIBLE);
            txtTodayCount.setText("" + todayTasks.size());
            todayRV.setVisibility(View.VISIBLE);
            todayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            todayAdapter = new TaskRvAdapter(getContext(), todayTasks);
            todayAdapter.setShow_date(false);
            todayAdapter.setTaskListener(new TaskRvAdapter.TaskListener() {
                @Override
                public void onChangeTaskDone(int position, boolean isChecked) {
                    if (isChecked) {
                        TaskManager.getInstance(getContext()).taskDone(todayTasks.get(position));
                    } else {
                        TaskManager.getInstance(getContext()).taskUnDone(todayTasks.get(position));
                    }
                }

                @Override
                public void onTaskClick(int position) {
                    // Todo : Do Something
                }

                @Override
                public void onCategoryClick(int position) {
                    Category category = CategoryManager.getInstance(getContext()).getCategoryById(todayTasks.get(position).getCategory_id());
                    AppDialog dialog = new AppDialog();
                    dialog.setTitle(category.getName());
                    dialog.setMassage("Do you want to see all tasks of this category?");
                    dialog.setCancelButton("No",null);
                    dialog.setOkButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ShowTasksActivity.class);
                            intent.putExtra("is_forCategory", true);
                            intent.putExtra("category", category);
                            startActivity(intent);
                        }
                    });
                    dialog.show(requireActivity().getSupportFragmentManager(),"Open category");
                }
            });
            todayRV.setAdapter(todayAdapter);
        }
        if (tomorrowTasks.size() > 0) {
            have[1] = true;
            tomorrowTitle.setVisibility(View.VISIBLE);
            txtTomorrowCount.setText("" + tomorrowTasks.size());
            tomorrowRV.setVisibility(View.VISIBLE);
            tomorrowRV.setLayoutManager(new LinearLayoutManager(getContext()));
            tomorrowAdapter = new TaskRvAdapter(getContext(), tomorrowTasks);
            todayAdapter.setTaskListener(new TaskRvAdapter.TaskListener() {
                @Override
                public void onChangeTaskDone(int position, boolean isChecked) {
                    if (isChecked) {
                        TaskManager.getInstance(getContext()).taskDone(tomorrowTasks.get(position));
                    } else {
                        TaskManager.getInstance(getContext()).taskUnDone(tomorrowTasks.get(position));
                    }
                }

                @Override
                public void onTaskClick(int position) {
                    // Todo : Do Something
                }

                @Override
                public void onCategoryClick(int position) {
                    Category category = CategoryManager.getInstance(getContext()).getCategoryById(tomorrowTasks.get(position).getCategory_id());
                    AppDialog dialog = new AppDialog();
                    dialog.setTitle(category.getName());
                    dialog.setMassage("Do you want to see all tasks of this category?");
                    dialog.setCancelButton("No",null);
                    dialog.setOkButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ShowTasksActivity.class);
                            intent.putExtra("is_forCategory", true);
                            intent.putExtra("category", category);
                            startActivity(intent);
                        }
                    });
                    dialog.show(requireActivity().getSupportFragmentManager(),"Open category");
                }
            });
            tomorrowRV.setAdapter(tomorrowAdapter);
        }
        if (otherTasks.size() > 0) {
            have[2] = true;
            if (have[0] || have[1])
                othersTitle.setVisibility(View.VISIBLE);
            othersRV.setVisibility(View.VISIBLE);
            othersRV.setLayoutManager(new LinearLayoutManager(getContext()));
            otherAdapter = new TaskRvAdapter(getContext(), otherTasks);
            todayAdapter.setTaskListener(new TaskRvAdapter.TaskListener() {
                @Override
                public void onChangeTaskDone(int position, boolean isChecked) {
                    if (isChecked) {
                        TaskManager.getInstance(getContext()).taskDone(otherTasks.get(position));
                    } else {
                        TaskManager.getInstance(getContext()).taskUnDone(otherTasks.get(position));
                    }
                }

                @Override
                public void onTaskClick(int position) {
                    // Todo : Do Something
                }

                @Override
                public void onCategoryClick(int position) {
                    Category category = CategoryManager.getInstance(getContext()).getCategoryById(otherTasks.get(position).getCategory_id());
                    AppDialog dialog = new AppDialog();
                    dialog.setTitle(category.getName());
                    dialog.setMassage("Do you want to see all tasks of this category?");
                    dialog.setCancelButton("No",null);
                    dialog.setOkButton("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(), ShowTasksActivity.class);
                            intent.putExtra("is_forCategory", true);
                            intent.putExtra("category", category);
                            startActivity(intent);
                        }
                    });
                    dialog.show(requireActivity().getSupportFragmentManager(),"Open category");
                }
            });
            othersRV.setAdapter(otherAdapter);
        }
        if (have[0]||have[1]||have[2]){
            setTitle("Your Tasks");
        }else {
            setTitle("You have no Task !");
            addNewLayout.setVisibility(View.VISIBLE);
        }
    }
    public void reLoadTasks() {
        TaskManager taskManager = TaskManager.getInstance(getContext());
        todayTasks.clear();
        todayTasks.addAll(taskManager.geTodayTasks());
        tomorrowTasks.clear();
        tomorrowTasks.addAll(taskManager.geTodayTasks());
        otherTasks.clear();
        otherTasks.addAll(taskManager.geTodayTasks());

        todayAdapter.notifyDataSetChanged();
        tomorrowAdapter.notifyDataSetChanged();
        otherAdapter.notifyDataSetChanged();

    }
}