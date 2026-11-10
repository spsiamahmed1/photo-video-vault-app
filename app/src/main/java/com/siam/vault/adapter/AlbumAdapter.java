package com.siam.vault.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.*;

public class AlbumAdapter extends BaseAdapter {
    private final Context context;private final List<String> albums;
    public AlbumAdapter(Context c,List<String> values){context=c;albums=values;}
    @Override public int getCount(){return albums.size();}@Override public Object getItem(int p){return albums.get(p);}@Override public long getItemId(int p){return p;}
    @Override public View getView(int p,View v,ViewGroup parent){TextView t=v instanceof TextView?(TextView)v:new TextView(context);t.setPadding(24,20,24,20);t.setTextSize(16);t.setText(albums.get(p));return t;}
}
