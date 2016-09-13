package com.yan.mrefreshlayout;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.widget.TextView;

import com.yan.pullmorerefreshlayout.PullMoreRefreshLayout;
import com.yan.pullmorerefreshlayout.PullMoreRefreshState;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> stringList = new ArrayList<>();
    StringAdapter stringAdapter;
    PullMoreRefreshLayout pullMoreRefreshLayout;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        pullMoreRefreshLayout = (PullMoreRefreshLayout) findViewById(R.id.pm_freshlayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        stringAdapter = new StringAdapter(stringList);
        recyclerView.setAdapter(stringAdapter);
        stringList.add("亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲");
        stringList.add("张张张张赞赞赞赞赞赞张张张张赞赞赞赞赞赞张张张张张张张张张张张张");
        stringList.add("淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡");
        stringList.add("笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨笨吧");
        stringList.add("呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃呃");

        // pullMoreRefreshLayout.setFooterView(new PullMoreHeader(this));

        pullMoreRefreshLayout.setOnRefreshListener(onRefreshListener);
        pullMoreRefreshLayout.setOnLoadMoreListener(onLoadMoreListener);
    }

    PullMoreRefreshLayout.OnRefreshListener onRefreshListener = new PullMoreRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            stringList.clear();
            stringList.add("亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲");
            stringList.add("张张张张赞赞赞赞赞赞张张张张赞赞赞赞赞赞张张张张张张张张张张张张");
            stringList.add("淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡");

            pullMoreRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullMoreRefreshLayout.executeComplete();
                    stringAdapter.notifyDataSetChanged();
                }
            }, 2000);
        }
    };

    PullMoreRefreshLayout.OnLoadMoreListener onLoadMoreListener = new PullMoreRefreshLayout.OnLoadMoreListener() {
        @Override
        public void onLoadMore() {
            stringList.add("亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲亲");
            stringList.add("张张张张赞赞赞赞赞赞张张张张赞赞赞赞赞赞张张张张张张张张张张张张");
            stringList.add("淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡的淡淡");
            pullMoreRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pullMoreRefreshLayout.executeComplete();
                    stringAdapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(stringList.size() - 2);
                }
            }, 2000);
        }
    };

}
