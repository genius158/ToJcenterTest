package com.yan.mrefreshlayout;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;

import com.yan.pullmorerefreshlayout.PullMoreRefreshState;

/**
 * 自定义header或footer
 */
class PullMoreHeader extends TextView implements PullMoreRefreshState {

    public PullMoreHeader(Context context) {
        super(context);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void onCallRelease() {
        setText("释放刷新");
    }

    @Override
    public void onCallDrag() {
        setText("下拉");
    }

    @Override
    public void onExecute() {
        setText("刷新中...");
    }

    @Override
    public void onFinish() {
        setText("刷新完成");

    }
}
