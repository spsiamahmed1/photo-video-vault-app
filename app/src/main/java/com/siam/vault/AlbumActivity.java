package com.siam.vault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.Ui;

public class AlbumActivity extends AppCompatActivity {
    private LinearLayout list;private AppDatabase db;
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);db=new AppDatabase(this);LinearLayout root=Ui.column(this);LinearLayout top=Ui.row(this);top.addView(Ui.chip(this,"Back",this::finish));top.addView(Ui.text(this,"Albums",24,true));Ui.add(root,top,-2);Ui.add(root,Ui.text(this,"Import করার সময় Album-এর নাম লিখুন; নতুন নাম দিলে নতুন Album তৈরি হবে।",14,false),-2);Ui.add(root,Ui.button(this,"নতুন Album-এ ছবি Import করুন",()->startActivity(new Intent(this,PhotoActivity.class))),52);ScrollView s=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);s.addView(list);root.addView(s,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);load();}
    private void load(){list.removeAllViews();for(String album:db.albums()){TextView t=Ui.text(this,album,17,true);t.setOnClickListener(v->{Intent i=new Intent(this,PhotoActivity.class);i.putExtra("album",album);startActivity(i);});Ui.card(list,t);}}
}
