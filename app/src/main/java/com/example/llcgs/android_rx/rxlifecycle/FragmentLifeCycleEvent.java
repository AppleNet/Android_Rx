package com.example.llcgs.android_rx.rxlifecycle;

/**
 * com.example.llcgs.android_rx.base.ActivityLifeCycleEvent
 *
 * @author liulongchao
 * @since 2017/6/12
 */


public enum FragmentLifeCycleEvent implements LifeCycleEvent {
    ATTACH,
    CREATE,
    CREATE_VIEW,
    START,
    RESUME,
    PAUSE,
    STOP,
    DESTROY_VIEW,
    DESTROY,
    DETACH
}
