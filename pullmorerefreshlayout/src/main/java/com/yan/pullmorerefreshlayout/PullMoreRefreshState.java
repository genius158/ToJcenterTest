package com.yan.pullmorerefreshlayout;


public interface PullMoreRefreshState {
    void onCallRelease();

    void onCallDrag();

    void onExecute();

    void onFinish();
}
