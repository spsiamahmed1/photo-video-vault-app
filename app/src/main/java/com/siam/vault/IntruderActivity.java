package com.siam.vault;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.Ui;
import java.text.SimpleDateFormat;
import java.util.*;

public class IntruderActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);LinearLayout root=Ui.column(this);LinearLayout top=Ui.row(this);top.addView(Ui.chip(this,"Back",this::finish));top.addView(Ui.text(this,"Break-In Report",24,true));Ui.add(root,top,-2);Ui.add(root,Ui.text(this,"ভুল PIN প্রচেষ্টার সময় স্থানীয়ভাবে রাখা হয়। কোনো গোপন selfie তোলা হয় না।",14,false),-2);ScrollView sc=new ScrollView(this);LinearLayout list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);sc.addView(list);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));SimpleDateFormat fmt=new SimpleDateFormat("d MMM yyyy, h:mm:ss a",Locale.getDefault());List<Long> times=new AppDatabase(this).attempts();if(times.isEmpty()){TextView empty=Ui.text(this,"কোনো ভুল PIN প্রচেষ্টা নেই",16,false);empty.setGravity(Gravity.CENTER);Ui.add(list,empty,120);}else for(Long time:times){TextView t=Ui.text(this,"Failed unlock   •   "+fmt.format(new Date(time)),14,false);Ui.card(list,t);}setContentView(root);}
}
