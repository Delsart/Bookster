package com.delsart.bookdownload.listandadapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.delsart.bookdownload.R;

public class mListAdapter extends BaseQuickAdapter<mlist,BaseViewHolder>
 {
     public mListAdapter() {
         super(R.layout.listlayout);
     }

     @Override
     protected void convert(BaseViewHolder viewHolder, mlist item) {
         String s=item.getinfo();
         if (s.length()>200){
             s=s.substring(0,200)+"   >>点击查看更多";
         }
         viewHolder.setText(R.id.name, item.getname())
                 .setText(R.id.time, item.gettime())
                 .setText(R.id.info,s);

     }


 }

