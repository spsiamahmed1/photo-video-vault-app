package com.siam.vault.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.*;
import android.widget.*;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.FileHelper;
import java.util.*;

public class PhotoAdapter extends BaseAdapter {
    private final Context context;private final List<AppDatabase.Media> items;
    public PhotoAdapter(Context c,List<AppDatabase.Media> data){context=c;items=data;}
    @Override public int getCount(){return items.size();}@Override public Object getItem(int p){return items.get(p);}@Override public long getItemId(int p){return items.get(p).id;}
    @Override public View getView(int p,View v,ViewGroup parent){LinearLayout box;if(v instanceof LinearLayout)box=(LinearLayout)v;else{box=new LinearLayout(context);box.setOrientation(LinearLayout.VERTICAL);ImageView image=new ImageView(context);image.setId(android.R.id.icon);image.setScaleType(ImageView.ScaleType.CENTER_CROP);box.addView(image,new LinearLayout.LayoutParams(-1,160));TextView name=new TextView(context);name.setId(android.R.id.text1);box.addView(name);}AppDatabase.Media m=items.get(p);ImageView image=box.findViewById(android.R.id.icon);Bitmap b=FileHelper.thumbnail(context,m);if(b!=null)image.setImageBitmap(b);((TextView)box.findViewById(android.R.id.text1)).setText(m.name);return box;}
}
