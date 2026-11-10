package com.siam.vault.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.core.content.ContextCompat;
import com.siam.vault.R;

public final class Ui {
    public static final int ORANGE=Color.rgb(255,122,0), DARK=Color.rgb(26,26,26), GREY=Color.rgb(138,138,138), BG=Color.rgb(248,249,250);
    private Ui(){}
    public static void secure(Activity a){a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);}
    public static Typeface font(Context c){return ContextCompat.getFont(c,R.font.hind_siliguri);}
    public static TextView text(Context c,String s,int sp,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextColor(DARK);t.setTextSize(sp);t.setTypeface(font(c),bold?Typeface.BOLD:Typeface.NORMAL);return t;}
    public static GradientDrawable shape(int color,int radius,int stroke){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);if(stroke!=0)d.setStroke(1,stroke);return d;}
    public static LinearLayout column(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(dp(c,20),dp(c,14),dp(c,20),dp(c,20));l.setBackgroundColor(BG);return l;}
    public static LinearLayout row(Context c){LinearLayout l=new LinearLayout(c);l.setOrientation(LinearLayout.HORIZONTAL);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    public static TextView button(Context c,String label,Runnable onClick){TextView b=text(c,label,16,true);b.setGravity(Gravity.CENTER);b.setTextColor(Color.WHITE);b.setPadding(dp(c,14),dp(c,12),dp(c,14),dp(c,12));b.setBackground(shape(ORANGE,dp(c,16),0));b.setElevation(dp(c,3));b.setOnClickListener(v->onClick.run());return b;}
    public static TextView chip(Context c,String label,Runnable onClick){TextView b=text(c,label,14,true);b.setGravity(Gravity.CENTER);b.setTextColor(ORANGE);b.setPadding(dp(c,12),dp(c,9),dp(c,12),dp(c,9));b.setBackground(shape(0xFFFFF3E6,dp(c,14),0));b.setOnClickListener(v->onClick.run());return b;}
    public static void add(LinearLayout parent,View child,int height){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,height<0?height:dp(parent.getContext(),height));p.bottomMargin=dp(parent.getContext(),10);parent.addView(child,p);}
    public static int dp(Context c,int v){return Math.round(v*c.getResources().getDisplayMetrics().density);}
    public static void title(LinearLayout root,String title,String subtitle){TextView t=text(root.getContext(),title,25,true);add(root,t,-2);if(subtitle!=null){TextView s=text(root.getContext(),subtitle,14,false);s.setTextColor(GREY);add(root,s,-2);}}
    public static void card(LinearLayout root,View content){LinearLayout box=new LinearLayout(root.getContext());box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(root.getContext(),16),dp(root.getContext(),14),dp(root.getContext(),16),dp(root.getContext(),14));box.setBackground(shape(Color.WHITE,dp(root.getContext(),24),0xFFF1F1F1));box.setElevation(dp(root.getContext(),3));box.addView(content);add(root,box,-2);}
    public static void space(LinearLayout root,int h){View v=new View(root.getContext());add(root,v,h);}
}
