package com.example.llcgs.android_rx.rxjava;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.llcgs.android_rx.R;

/**
 * com.example.llcgs.android_rx.rxjava.MyAdapter
 *
 * @author liulongchao
 * @since 2017/6/12
 */


public class MyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public MyAdapter() {
        super(R.layout.item_reyclerview);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.textView4, item);
    }
}
