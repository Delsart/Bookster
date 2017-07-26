package com.delsart.bookdownload.listandadapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.delsart.bookdownload.R;
import com.ms.square.android.expandabletextview.ExpandableTextView;

public class mListAdapter extends BaseQuickAdapter<mlist,BaseViewHolder>
 {
     public mListAdapter() {
         super(R.layout.listlayout);
     }

     @Override
     protected void convert(BaseViewHolder viewHolder, mlist item) {
         viewHolder.setText(R.id.name, item.getname())
                 .setText(R.id.time, item.gettime());
         ExpandableTextView expTv1 = (ExpandableTextView) viewHolder.getView(R.id.expand_text_view);
         expTv1.setText(item.getinfo());

     }


 }

