package com.siam.vault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.helper.BackupHelper;
import com.siam.vault.helper.Ui;

public class BackupActivity extends AppCompatActivity {
    private static final int EXPORT=71,IMPORT=72;private int mode;
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);LinearLayout root=Ui.column(this);LinearLayout top=Ui.row(this);top.addView(Ui.chip(this,"Back",this::finish));top.addView(Ui.text(this,"Backup & Restore",23,true));Ui.add(root,top,-2);Ui.title(root,"আপনার vault-এর portable backup","Backup পাসফ্রেজ দিয়ে encrypted; নতুন ফোনে restore করতে একই পাসফ্রেজ লাগবে।");Ui.card(root,Ui.text(this,"Media export হওয়ার সময় ডিভাইসের Keystore দিয়ে decrypt করে passphrase-encrypted archive তৈরি হয়। Restore হলে ফাইলগুলো নতুন ডিভাইসের Keystore key দিয়ে আবার encrypt হবে। পাসফ্রেজ ভুলে গেলে backup খোলা যাবে না।",14,false));Ui.space(root,16);Ui.add(root,Ui.button(this,"Encrypted Backup তৈরি করুন",this::create),54);Ui.add(root,Ui.button(this,"Backup থেকে Restore করুন",this::choose),54);Ui.add(root,Ui.text(this,"Backup-এ Photos ও Videos অন্তর্ভুক্ত। Secret Notes এই build-এ export হয় না।",13,false),-2);setContentView(root);}
    private void create(){mode=EXPORT;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/octet-stream");i.putExtra(Intent.EXTRA_TITLE,"photo-video-vault-backup.pvv");startActivityForResult(i,EXPORT);}
    private void choose(){mode=IMPORT;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("*/*");startActivityForResult(i,IMPORT);}
    @Override protected void onActivityResult(int r,int result,Intent data){super.onActivityResult(r,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;passwordDialog(data.getData(),r==EXPORT);}
    private void passwordDialog(Uri uri,boolean exporting){EditText pass=new EditText(this);pass.setSingleLine(true);pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);pass.setHint("কমপক্ষে ৮ অক্ষরের পাসফ্রেজ");new android.app.AlertDialog.Builder(this).setTitle(exporting?"Backup পাসফ্রেজ":"Restore পাসফ্রেজ").setView(pass).setNegativeButton("Cancel",null).setPositiveButton("চালিয়ে যান",(d,w)->{String value=pass.getText().toString();if(value.length()<8){Toast.makeText(this,"কমপক্ষে ৮ অক্ষরের পাসফ্রেজ দিন",Toast.LENGTH_LONG).show();return;}progress(exporting,uri,value);}).show();}
    private void progress(boolean exporting,Uri uri,String pass){ProgressBar bar=new ProgressBar(this);android.app.AlertDialog dialog=new android.app.AlertDialog.Builder(this).setTitle(exporting?"Backup তৈরি হচ্ছে":"Restore হচ্ছে").setView(bar).setCancelable(false).create();dialog.show();new Thread(()->{try{int count=exporting?BackupHelper.export(this,uri,pass):BackupHelper.restore(this,uri,pass);runOnUiThread(()->{dialog.dismiss();Toast.makeText(this,(exporting?"Encrypted backup তৈরি হয়েছে — ":"মিডিয়া restore হয়েছে — ")+count+"টি item",Toast.LENGTH_LONG).show();});}catch(Exception e){runOnUiThread(()->{dialog.dismiss();Toast.makeText(this,exporting?"Backup তৈরি ব্যর্থ হয়েছে":"Restore ব্যর্থ — পাসফ্রেজ বা ফাইল পরীক্ষা করুন",Toast.LENGTH_LONG).show();});}}).start();}
}
