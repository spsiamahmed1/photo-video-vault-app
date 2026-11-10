package com.siam.vault;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.EncryptionHelper;
import com.siam.vault.helper.Ui;
import java.nio.charset.StandardCharsets;

public class NotesActivity extends AppCompatActivity {
    private AppDatabase db;private LinearLayout list;
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);db=new AppDatabase(this);render();}
    private void render(){LinearLayout root=Ui.column(this);LinearLayout top=Ui.row(this);top.addView(Ui.chip(this,"Back",this::finish));top.addView(Ui.text(this,"Secret Notes",24,true));Ui.add(root,top,-2);Ui.add(root,Ui.text(this,"আপনার নোট AES-GCM দিয়ে encrypted থাকে।",14,false),-2);Ui.add(root,Ui.button(this,"＋  নতুন Note",()->edit(null)),52);ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);load();}
    private void load(){list.removeAllViews();for(AppDatabase.EncryptedNote n:db.notes()){String title="Private note";try{title=new String(EncryptionHelper.unseal(n.titleBytes),StandardCharsets.UTF_8);}catch(Exception ignored){}TextView t=Ui.text(this,title,17,true);t.setOnClickListener(v->edit(n));t.setOnLongClickListener(v->{new AlertDialog.Builder(this).setTitle("নোট মুছবেন?").setNegativeButton("না",null).setPositiveButton("মুছুন",(d,w)->{db.deleteNote(n.id);load();}).show();return true;});Ui.card(list,t);}}
    private void edit(AppDatabase.EncryptedNote n){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);EditText title=new EditText(this);title.setHint("শিরোনাম");EditText body=new EditText(this);body.setHint("গোপন নোট লিখুন");body.setMinLines(5);body.setGravity(Gravity.TOP);body.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);box.addView(title);box.addView(body);if(n!=null){try{title.setText(new String(EncryptionHelper.unseal(n.titleBytes),StandardCharsets.UTF_8));body.setText(new String(EncryptionHelper.unseal(n.bodyBytes),StandardCharsets.UTF_8));}catch(Exception e){Toast.makeText(this,"নোট পড়া যায়নি",Toast.LENGTH_SHORT).show();}}
        new AlertDialog.Builder(this).setTitle(n==null?"নতুন Note":"Note সম্পাদনা").setView(box).setNegativeButton("বাতিল",null).setPositiveButton("Encrypted Save",(d,w)->{try{String t=title.getText().toString().trim(),b=body.getText().toString();if(t.isEmpty())t="Private note";db.saveNote(n==null?0:n.id,EncryptionHelper.seal(t.getBytes(StandardCharsets.UTF_8)),EncryptionHelper.seal(b.getBytes(StandardCharsets.UTF_8)));load();}catch(Exception e){Toast.makeText(this,"নোট সেভ হয়নি",Toast.LENGTH_SHORT).show();}}).show();}
}
