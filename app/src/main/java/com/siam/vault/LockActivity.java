package com.siam.vault;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import com.siam.vault.database.AppDatabase;
import com.siam.vault.helper.PinHelper;
import com.siam.vault.helper.Ui;
import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {
    private TextView message; private EditText pin;
    @Override protected void onCreate(Bundle b){super.onCreate(b);Ui.secure(this);setContentView(build());if(PinHelper.hasBiometricEnabled(this))showBiometric();}
    private android.view.View build(){LinearLayout root=Ui.column(this);root.setGravity(android.view.Gravity.CENTER);Ui.title(root,"Calculator","আপনার ৪ সংখ্যার PIN লিখুন");pin=new EditText(this);pin.setSingleLine(true);pin.setGravity(android.view.Gravity.CENTER);pin.setTextSize(28);pin.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);pin.setHint("••••");Ui.add(root,pin,64);message=Ui.text(this,"",14,false);message.setTextColor(Ui.GREY);Ui.add(root,message,-2);Ui.add(root,Ui.button(this,"খুলুন",this::unlock),54);TextView bio=Ui.chip(this,"Fingerprint / Biometric",this::showBiometric);Ui.add(root,bio,48);TextView reset=Ui.chip(this,"PIN ভুলে গেছেন?",this::resetDialog);Ui.add(root,reset,48);return root;}
    private void unlock(){String p=pin.getText().toString();if(PinHelper.isLocked(this)){message.setText("৩ বার ভুল হয়েছে। ৩০ সেকেন্ড পরে চেষ্টা করুন।");return;}if(PinHelper.isFakePin(this,p)){PinHelper.clearFailures(this);open(true);return;}if(PinHelper.isRealPin(this,p)){PinHelper.clearFailures(this);open(false);return;}PinHelper.recordFailure(this);new AppDatabase(this).logAttempt();pin.setText("");message.setText(PinHelper.isLocked(this)?"ভুল PIN — ৩০ সেকেন্ড অপেক্ষা করুন":"ভুল PIN, আবার চেষ্টা করো");}
    private void open(boolean decoy){Intent i=new Intent(this,HomeActivity.class);i.putExtra("decoy",decoy);startActivity(i);finish();}
    private void showBiometric(){int result=BiometricManager.from(this).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);if(result!=BiometricManager.BIOMETRIC_SUCCESS){if(message!=null)message.setText("এই ডিভাইসে biometric unlock প্রস্তুত নয়।");return;}Executor ex=ContextCompat.getMainExecutor(this);BiometricPrompt prompt=new BiometricPrompt(this,ex,new BiometricPrompt.AuthenticationCallback(){@Override public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult r){PinHelper.clearFailures(LockActivity.this);open(false);}});BiometricPrompt.PromptInfo info=new BiometricPrompt.PromptInfo.Builder().setTitle("ভল্ট আনলক").setSubtitle("Fingerprint বা device credential ব্যবহার করুন").setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG).build();prompt.authenticate(info);}
    private void resetDialog(){EditText answer=new EditText(this);answer.setHint("তোমার প্রিয় খাবার কী?");new android.app.AlertDialog.Builder(this).setTitle("PIN রিসেট").setMessage("রিকভারি উত্তর লিখে নতুন PIN দিন").setView(answer).setPositiveButton("পরবর্তী",(d,w)->newPinDialog(answer.getText().toString())).setNegativeButton("বাতিল",null).show();}
    private void newPinDialog(String answer){EditText fresh=new EditText(this);fresh.setHint("নতুন ৪ সংখ্যার PIN");fresh.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);new android.app.AlertDialog.Builder(this).setTitle("নতুন PIN").setView(fresh).setPositiveButton("সেভ",(d,w)->{String p=fresh.getText().toString();if(!p.matches("\\d{4}")){Toast.makeText(this,"৪ সংখ্যার PIN দিন",Toast.LENGTH_SHORT).show();return;}if(PinHelper.resetWithAnswer(this,answer,p)){Toast.makeText(this,"PIN রিসেট হয়েছে",Toast.LENGTH_SHORT).show();}else Toast.makeText(this,"উত্তর মেলেনি",Toast.LENGTH_SHORT).show();}).setNegativeButton("বাতিল",null).show();}
}
