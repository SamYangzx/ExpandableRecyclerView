package com.lanmeng.functiontest;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hgdendi.expandablerecycleradapter.BaseExpandableRecyclerViewAdapter;
import com.hgdendi.expandablerecycleradapter.ViewProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Context mContext;
    List<SampleGroupBean> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initData();
        initView();

    }

    private void initView() {
        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SampleAdapter adapter = new SampleAdapter(dataList);
        adapter.setEmptyViewProducer(new ViewProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new DefaultEmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.empty, parent, false)
                );
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder) {

            }
        });
        adapter.setHeaderViewProducer(new ViewProducer() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
                return new DefaultEmptyViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.header, parent, false)
                );
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder) {

            }
        }, false);

        adapter.setListener(new BaseExpandableRecyclerViewAdapter.ExpandableRecyclerViewOnClickListener<SampleGroupBean, SampleChildBean>() {
            @Override
            public boolean onGroupLongClicked(SampleGroupBean groupItem) {
                return false;
            }

            @Override
            public boolean onInterceptGroupExpandEvent(SampleGroupBean groupItem, boolean isExpand) {
                return false;
            }

            @Override
            public void onGroupClicked(SampleGroupBean groupItem) {
                Toast.makeText(mContext, String.format(Locale.getDefault(), "group=%s", groupItem.getName()) , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildClicked(SampleGroupBean groupItem, SampleChildBean childItem) {
                handleBeanClick(groupItem, childItem);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void handleBeanClick(SampleGroupBean groupItem, SampleChildBean childItem) {
        Toast.makeText(mContext, String.format(Locale.getDefault(), "group=%s, child=%s", groupItem.getName(), childItem.getName()) , Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        dataList = new ArrayList<>(10);
        List<SampleChildBean> childList = new ArrayList<>();
        childList.add(new SampleChildBean("StringTest"));
        dataList.add(new SampleGroupBean(childList, "Java"));

        childList = new ArrayList<>();
        childList.add(new SampleChildBean("root"));
        dataList.add(new SampleGroupBean(childList, "Debug"));
    }
}
