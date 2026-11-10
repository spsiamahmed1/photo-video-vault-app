package com.siam.vault;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.Ui;
import java.text.SimpleDateFormat;
import java.util.*;

public class HomeActivity extends AppCompatActivity {
    private boolean decoy;
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);decoy=getIntent().getBooleanExtra("decoy",false);setContentView(build());}
    private android.view.View build(){ScrollView scroll=new ScrollView(this);LinearLayout root=Ui.column(this);root.setPadding(Ui.dp(this,18),Ui.dp(this,12),Ui.dp(this,18),Ui.dp(this,28));scroll.addView(root);
        LinearLayout header=Ui.row(this);TextView title=Ui.text(this,"আমার ভল্ট",26,true);header.addView(title,new LinearLayout.LayoutParams(0,-2,1));if(!decoy){ImageButton bell=new ImageButton(this);bell.setImageResource(R.drawable.ic_bell);bell.setContentDescription("Break-In Report");bell.setBackgroundColor(Color.TRANSPARENT);bell.setOnClickListener(v->go(IntruderActivity.class));header.addView(bell,new LinearLayout.LayoutParams(Ui.dp(this,40),Ui.dp(this,40)));ImageButton profile=new ImageButton(this);profile.setImageResource(R.drawable.ic_user);profile.setContentDescription("Profile");profile.setBackgroundColor(Color.TRANSPARENT);profile.setOnClickListener(v->go(ProfileActivity.class));header.addView(profile,new LinearLayout.LayoutParams(Ui.dp(this,40),Ui.dp(this,40)));}Ui.add(root,header,-2);
        if(decoy){TextView empty=Ui.text(this,"কোনো আইটেম নেই",16,false);empty.setGravity(Gravity.CENTER);Ui.add(root,empty,300);return scroll;}
        Ui.add(root,Ui.text(this,new SimpleDateFormat("EEEE, d MMMM",Locale.forLanguageTag("bn")).format(new Date()),14,false),-2);
        LinearLayout cal=Ui.row(this);Calendar c=Calendar.getInstance();c.add(Calendar.DAY_OF_MONTH,-(c.get(Calendar.DAY_OF_WEEK)-1));String[] days={"Su","Mo","Tu","We","Th","Fr","Sa"};for(int i=0;i<7;i++){final Calendar day=(Calendar)c.clone();day.add(Calendar.DAY_OF_MONTH,i);boolean today=day.get(Calendar.YEAR)==Calendar.getInstance().get(Calendar.YEAR)&&day.get(Calendar.DAY_OF_YEAR)==Calendar.getInstance().get(Calendar.DAY_OF_YEAR);LinearLayout cell=new LinearLayout(this);cell.setGravity(Gravity.CENTER);cell.setOrientation(LinearLayout.VERTICAL);TextView d=Ui.text(this,days[i],11,false);d.setTextColor(Ui.GREY);TextView num=Ui.text(this,new SimpleDateFormat("d",Locale.getDefault()).format(day.getTime()),14,true);num.setGravity(Gravity.CENTER);if(today){num.setTextColor(Color.WHITE);num.setBackground(Ui.shape(Ui.ORANGE,Ui.dp(this,18),0));}cell.addView(d);cell.addView(num,new LinearLayout.LayoutParams(Ui.dp(this,34),Ui.dp(this,34)));cal.addView(cell,new LinearLayout.LayoutParams(0,-2,1));}Ui.add(root,cal,-2);
        long used=new AppDatabase(this).totalBytes();Ui.card(root,new com.siam.vault.helper.StorageRingView(this,used));
        Ui.title(root,"তোমার নিরাপদ জায়গা","মিডিয়া encrypted internal storage-এ থাকে");
        addAction(root,"Photos","ছবি দেখুন বা Import করুন",PhotoActivity.class);addAction(root,"Videos","ভিডিও দেখুন বা Import করুন",VideoActivity.class);addAction(root,"Albums","অ্যালবাম অনুযায়ী সাজান",AlbumActivity.class);addAction(root,"Secret Notes","এনক্রিপ্টেড নোট",NotesActivity.class);addAction(root,"Backup & Restore","পাসফ্রেজ-সুরক্ষিত ব্যাকআপ",BackupActivity.class);addAction(root,"Break-In Report","ভুল PIN প্রচেষ্টার সময় দেখুন",IntruderActivity.class);
        LinearLayout nav=Ui.row(this);nav.setGravity(Gravity.CENTER);nav.setPadding(8,8,8,8);nav.setBackground(Ui.shape(Color.WHITE,Ui.dp(this,28),0xFFF1F1F1));String[] labels={"Home","Photos","＋","Videos","Profile"};for(int i=0;i<labels.length;i++){final int tab=i;TextView n=Ui.text(this,labels[i],i==2?22:12,true);n.setGravity(Gravity.CENTER);if(i==2){n.setTextColor(Color.WHITE);n.setBackground(Ui.shape(Ui.ORANGE,Ui.dp(this,30),0));}else n.setTextColor(i==0?Ui.ORANGE:Ui.GREY);n.setOnClickListener(v->{if(tab==1)go(PhotoActivity.class);else if(tab==2)go(PhotoActivity.class);else if(tab==3)go(VideoActivity.class);else if(tab==4)go(ProfileActivity.class);});nav.addView(n,new LinearLayout.LayoutParams(0,Ui.dp(this,48),1));}Ui.add(root,nav,-2);return scroll;}
    private void addAction(LinearLayout root,String title,String sub,Class<?> cls){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);TextView t=Ui.text(this,title,17,true);TextView s=Ui.text(this,sub,13,false);s.setTextColor(Ui.GREY);box.addView(t);box.addView(s);box.setOnClickListener(v->go(cls));Ui.card(root,box);}
    private void go(Class<?> c){Intent i=new Intent(this,c);if(decoy)i.putExtra("decoy",true);startActivity(i);}
}
