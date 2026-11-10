package com.siam.vault.helper;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import java.util.Locale;

public class StorageRingView extends View {
    private final Paint ring=new Paint(Paint.ANTI_ALIAS_FLAG),text=new Paint(Paint.ANTI_ALIAS_FLAG);private final long used;private final float density;
    public StorageRingView(Context c,long bytes){super(c);used=Math.max(0,bytes);density=getResources().getDisplayMetrics().density;ring.setStyle(Paint.Style.STROKE);ring.setStrokeCap(Paint.Cap.ROUND);ring.setStrokeWidth(9*density);ring.setColor(0xFFFF7A00);text.setColor(0xFF1A1A1A);text.setTextAlign(Paint.Align.CENTER);text.setTypeface(Typeface.create("sans-serif-medium",Typeface.BOLD));setContentDescription(String.format(Locale.US,"Storage %.2f gigabytes used of 10 gigabytes",used/(1024d*1024*1024)));}
    @Override protected void onMeasure(int w,int h){int size=Math.round(116*density);setMeasuredDimension(resolveSize(size,w),resolveSize(size,h));}
    @Override protected void onDraw(Canvas c){super.onDraw(c);float cx=getWidth()/2f,cy=getHeight()/2f,r=Math.min(cx,cy)-14*density;ring.setColor(0xFFF1F1F1);c.drawArc(cx-r,cy-r,cx+r,cy+r,-90,360,false,ring);ring.setColor(0xFFFF7A00);float sweep=(float)Math.min(1d,used/(10d*1024*1024*1024))*360f;c.drawArc(cx-r,cy-r,cx+r,cy+r,-90,sweep,false,ring);text.setTextSize(15*density);String value=String.format(Locale.US,"%.2f GB",used/(1024d*1024*1024));c.drawText(value,cx,cy+2*density,text);text.setTextSize(11*density);text.setColor(0xFF8A8A8A);c.drawText("of 10 GB",cx,cy+21*density,text);}
}
