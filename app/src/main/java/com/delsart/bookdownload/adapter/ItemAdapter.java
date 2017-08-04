package com.delsart.bookdownload.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delsart.bookdownload.R;
import com.delsart.bookdownload.bean.NovelBean;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<NovelBean> mList;

    public ItemAdapter() {
        mList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NovelBean bean = mList.get(position);
        holder.mTextViewName.setText(bean.getName());
        holder.mTextViewInfo.setText(bean.getShowText());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void clear() {
        int size = mList.size();
        mList.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void add(List<NovelBean> beans) {
        int size = mList.size();
        mList.addAll(beans);
        notifyItemRangeInserted(size, beans.size());
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextViewName;
        private TextView mTextViewInfo;
        private Context mContent;
        public MyViewHolder(View itemView) {
            super(itemView);
            mContent = itemView.getContext();
            mTextViewInfo = (TextView) itemView.findViewById(R.id.text_view_info);
            mTextViewName = (TextView) itemView.findViewById(R.id.text_view_name);
        }
    }
}
