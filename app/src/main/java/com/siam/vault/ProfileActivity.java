package com.siam.vault;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.PinHelper;
import com.siam.vault.helper.Ui;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);render();}
    private void render(){LinearLayout root=Ui.column(this);LinearLayout top=Ui.row(this);top.addView(Ui.chip(this,"Back",this::finish));top.addView(Ui.text(this,"Profile",24,true));Ui.add(root,top,-2);LinearLayout user=Ui.row(this);TextView avatar=Ui.text(this,"S",26,true);avatar.setGravity(android.view.Gravity.CENTER);avatar.setTextColor(-1);avatar.setBackground(Ui.shape(Ui.ORANGE,Ui.dp(this,30),0));user.addView(avatar,new LinearLayout.LayoutParams(Ui.dp(this,60),Ui.dp(this,60)));TextView name=Ui.text(this,"Siam",20,true);name.setPadding(14,0,0,0);user.addView(name);Ui.card(root,user);
        LinearLayout premium=new LinearLayout(this);premium.setOrientation(LinearLayout.VERTICAL);TextView p=Ui.text(this,"Upgrade to Premium",19,true);p.setTextColor(Ui.ORANGE);TextView desc=Ui.text(this,"Extra storage and premium tools — subscription setup is not enabled in this release.",13,false);premium.addView(p);premium.addView(desc);Ui.card(root,premium);
        Ui.title(root,"Preferences",null);Switch bio=new Switch(this);bio.setText("Fingerprint / Biometric unlock");bio.setChecked(PinHelper.hasBiometricEnabled(this));bio.setOnCheckedChangeListener((v,checked)->{PinHelper.setBiometricEnabled(this,checked);Toast.makeText(this,checked?"Biometric unlock চালু":"Biometric unlock বন্ধ",Toast.LENGTH_SHORT).show();});Ui.add(root,bio,56);
        long bytes=new AppDatabase(this).totalBytes();TextView storage=Ui.text(this,String.format(Locale.US,"Storage used: %.2f GB",bytes/(1024.0*1024*1024)),15,false);Ui.card(root,storage);
        item(root,"Account",()->new android.app.AlertDialog.Builder(this).setTitle("Local account").setMessage("এই release-এ profile ডিভাইসেই থাকে; কোনো online account বা sync নেই।").setPositiveButton("ঠিক আছে",null).show());item(root,"Recycle Bin",()->new android.app.AlertDialog.Builder(this).setTitle("Recycle Bin").setItems(new String[]{"Photo Recycle Bin","Video Recycle Bin"},(d,w)->{Intent i=new Intent(this,w==0?PhotoActivity.class:VideoActivity.class);i.putExtra("bin",true);startActivity(i);}).show());item(root,"Backup & Restore",()->startActivity(new Intent(this,BackupActivity.class)));item(root,"Break-In Report",()->startActivity(new Intent(this,IntruderActivity.class)));item(root,"Secret Notes",()->startActivity(new Intent(this,NotesActivity.class)));item(root,"Help & Privacy",()->new android.app.AlertDialog.Builder(this).setTitle("Privacy").setMessage("মিডিয়া device-এর app-private storage-এ AES-GCM দিয়ে encrypted থাকে। অ্যাপ কোনো Internet permission ব্যবহার করে না। ভুল PIN-এর সময় সংরক্ষণ হয়; গোপনে ছবি তোলা হয় না।").setPositiveButton("ঠিক আছে",null).show());item(root,"Lock Now",()->{startActivity(new Intent(this,MainActivity.class));finish();});setContentView(root);}
    private void item(LinearLayout root,String label,Runnable action){TextView t=Ui.text(this,label+"   ›",16,true);t.setPadding(4,12,4,12);t.setOnClickListener(v->action.run());Ui.card(root,t);}
}
