package com.amir.todone.Fragments;

import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amir.todone.Adapters.TaskRvAdapter;
import com.amir.todone.Domain.Task.Task;
import com.amir.todone.Domain.Task.TaskManager;
import com.amir.todone.R;

import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView todayRV, tomorrowRV, othersRV;
    private TextView txtTodayCount, txtTomorrowCount;
    private ConstraintLayout todayTitle, tomorrowTitle, othersTitle;

    private List<Task> todayTasks, tomorrowTasks;


    private TaskRvAdapter todayAdapter, tomorrowAdapter;

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

        TaskManager taskManager = TaskManager.getInstance(getContext());
        todayTasks = taskManager.geTodayTasks();
        tomorrowTasks = taskManager.geTomorrowTasks();

        if (todayTasks.size() > 0) {
            todayTitle.setVisibility(View.VISIBLE);
            txtTodayCount.setText("" + todayTasks.size());
            todayRV.setVisibility(View.VISIBLE);
            todayRV.setLayoutManager(new LinearLayoutManager(getContext()));
            todayAdapter = new TaskRvAdapter(getContext(), todayTasks);
            todayAdapter.setShow_date(false);
            todayRV.setAdapter(todayAdapter);
        }
        if (tomorrowTasks.size() > 0) {
            tomorrowTitle.setVisibility(View.VISIBLE);
            txtTomorrowCount.setText("" + tomorrowTasks.size());
            tomorrowRV.setVisibility(View.VISIBLE);
            tomorrowRV.setLayoutManager(new LinearLayoutManager(getContext()));
            tomorrowAdapter = new TaskRvAdapter(getContext(), tomorrowTasks);
            tomorrowRV.setAdapter(tomorrowAdapter);
        }

        return v;
    }
}