package com.siam.vault.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.siam.vault.R;
import com.siam.vault.database.AppDatabase;
import java.util.*;

public class VideoAdapter extends BaseAdapter {
    private final Context context;private final List<AppDatabase.Media> items;
    public VideoAdapter(Context c,List<AppDatabase.Media> data){context=c;items=data;}
    @Override public int getCount(){return items.size();}@Override public Object getItem(int p){return items.get(p);}@Override public long getItemId(int p){return items.get(p).id;}
    @Override public View getView(int p,View v,ViewGroup parent){LinearLayout row;if(v instanceof LinearLayout)row=(LinearLayout)v;else{row=new LinearLayout(context);row.setGravity(Gravity.CENTER_VERTICAL);ImageView icon=new ImageView(context);icon.setId(android.R.id.icon);icon.setImageResource(R.drawable.ic_video);row.addView(icon,new LinearLayout.LayoutParams(48,48));TextView name=new TextView(context);name.setId(android.R.id.text1);row.addView(name,new LinearLayout.LayoutParams(0,-2,1));}AppDatabase.Media m=items.get(p);((TextView)row.findViewById(android.R.id.text1)).setText(m.name);return row;}
}
