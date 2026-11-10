package com.siam.vault;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.siam.vault.helper.PinHelper;
import com.siam.vault.helper.Ui;

public class SetPinActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);setContentView(build());}
    private android.view.View build(){
        LinearLayout root=Ui.column(this);root.setGravity(android.view.Gravity.CENTER_VERTICAL);
        Ui.title(root,"নতুন PIN দিন","আপনার ভল্ট সুরক্ষিত রাখতে ৪ সংখ্যার PIN সেট করুন");
        EditText pin=field("আসল PIN (৪ সংখ্যা)"), confirm=field("আবার PIN দিন"), fake=field("নকল PIN (ঐচ্ছিক)"), answer=field("তোমার প্রিয় খাবার কী? — উত্তর");
        pin.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);confirm.setInputType(pin.getInputType());fake.setInputType(pin.getInputType());answer.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        Ui.add(root,pin,56);Ui.add(root,confirm,56);Ui.add(root,fake,56);Ui.add(root,Ui.text(this,"PIN ভুলে গেলে এই উত্তর দিয়ে রিসেট করতে পারবেন।",13,false),-2);Ui.add(root,answer,56);
        TextView save=Ui.button(this,"নিরাপদে শুরু করুন",()->{
            String p=pin.getText().toString(),c=confirm.getText().toString(),f=fake.getText().toString(),a=answer.getText().toString();
            if(!p.matches("\\d{4}")){pin.setError("৪ সংখ্যার PIN দিন");return;}if(!p.equals(c)){confirm.setError("PIN মিলছে না");return;}if(!f.isEmpty()&&!f.matches("\\d{4}")){fake.setError("নকল PIN ৪ সংখ্যার হতে হবে");return;}if(!f.isEmpty()&&f.equals(p)){fake.setError("নকল PIN আসল PIN-এর মতো হতে পারবে না");return;}if(a.trim().length()<2){answer.setError("একটি উত্তর দিন");return;}
            try{PinHelper.setInitial(this,p,f,a);startActivity(new Intent(this,MainActivity.class));finish();}catch(Exception e){Toast.makeText(this,"PIN সংরক্ষণ করা যায়নি",Toast.LENGTH_LONG).show();}
        });Ui.add(root,save,54);
        ScrollView scroll=new ScrollView(this);scroll.addView(root);return scroll;
    }
    private EditText field(String hint){EditText e=new EditText(this);e.setSingleLine(true);e.setHint(hint);e.setTextSize(16);e.setPadding(Ui.dp(this,14),0,Ui.dp(this,14),0);e.setBackground(Ui.shape(-1,Ui.dp(this,14),0xFFF1F1F1));return e;}
}
