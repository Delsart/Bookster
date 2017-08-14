package com.delsart.bookdownload.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.delsart.bookdownload.R;
import com.delsart.bookdownload.bean.NovelBean;

/**
 * Created by Delsart on 2017/8/1.
 */

public class MyItemAdapter extends BaseQuickAdapter<NovelBean, BaseViewHolder> {
private int color;
    public MyItemAdapter(int color) {
        super(R.layout.item_list);
        this.color=color;
    }

    @Override
    protected void convert(BaseViewHolder viewHolder, NovelBean bean) {
        viewHolder.setText(R.id.text_view_name, bean.getName())
                .setText(R.id.text_view_info, bean.getShowText())
                .setBackgroundColor(R.id.text_view_name,color)
                .setBackgroundColor(R.id.text_view_info,color);
    }
}