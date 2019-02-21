package com.example.mit.easytopupscanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;

public class HelpActivity extends AppCompatActivity {
    com.facebook.ads.InterstitialAd interstitial;
    Ad adfacebook;Typeface ttf_mm3;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mit.easytopupscanner.R.layout.help);
        ImageView imageView = (ImageView) findViewById(com.example.mit.easytopupscanner.R.id.myanmar);
        Glide.with(getApplicationContext()).load(com.example.mit.easytopupscanner.R.raw.mmflag).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).crossFade().into(imageView);
        interstitial = new com.facebook.ads.InterstitialAd(HelpActivity.this, getString(com.example.mit.easytopupscanner.R.string.facebook_intertisial));
        interstitial.setAdListener(new AbstractAdListener() {
            public void onAdLoaded(Ad ad) {
                adfacebook = ad;
            }
        });
        interstitial.loadAd();


        EditText useguide=(EditText)findViewById(com.example.mit.easytopupscanner.R.id.use_guide);
        EditText feature=(EditText)findViewById(com.example.mit.easytopupscanner.R.id.feature);
        TextView wayone=(TextView)findViewById(com.example.mit.easytopupscanner.R.id.wayone);
        TextView waytwo=(TextView)findViewById(com.example.mit.easytopupscanner.R.id.waytwo);
        TextView waythree=(TextView)findViewById(com.example.mit.easytopupscanner.R.id.waythree);
        Intent i=getIntent();
         initialize();
        String myfont= preferences.getString("myfont","zaw");

        if(myfont.equals("zaw")){
          wayone.setText("(1) camera ေ႐ွ႕တြင္ လူႀကီးမင္း၏ခဲျခစ္ထားေသာ ေငြျဖည့္ကဒ္ကို လက္ျဖင့္ကိုင္ထားေရြ႔ေသာ္လည္းေကာင္း တစ္စံုတစ္ခုေပၚတြင္တင္ထား၍ေသာ္လည္းေကာင္းထား႐ွိေပးပါ");
            feature.setText("ေငြျဖည့္ကဒ္ စကင္နာ Apkေလးမွာပါဝင္တဲ့ လုပ္ေဆာင္ခ်က္ေတြကေတာ့\n" +
                    "..\n" +
                    "ေငြျဖည့္ကဒ္အားခဲျခစ္ၿပီး အလြယ္တကူကုတ္နံပါတ္အား စကင္ဖတ္ေပးႏိုင္ျခင္း\n" +
                    "က်ဴအာကုတ္ပါ႐ွိေသာ ေငြျဖည့္ကတ္မ်ားကိုလည္းစကင္ဖတ္၍အျမန္ေငြျဖည့္ေပးႏိုင္ျခင္း\n" +
                    "ေငြလက္က်န္စစ္ေဆးႏိုင္ျခင္း\n" +
                    "Mynumberသိႏိုင္ျခင္း\n" +
                    "Operator Iconတစ္ခုစီကိုေရြးခ်ယ္၍ ၎operator ၏serviceမ်ားအား ယခုapkမွလုပ္ေဆာငိႏိုင္ျခင္း\n" +
                    "Eg..Package ဝယ္ယူျခင္းစေသာလုပ္ေဆာင္ခ်က္မ်ားျဖစ္ပါတယ္\n");
            useguide.setText("ေငြျဖည့္ကဒ္စကင္နာ အပလီေကး႐ွင္းေလးဟာ ZawgyI Unicodeႏွစ္မ်ိဳးစလံုးကိုေထာက္ပံ့ေပးပါတယ္\n" +
                    "\n" +
                    "Qr Scanမွာ Barcodeနဲ႔ေရာေနတတ္ပါတယ္ဒါေၾကာင့္qr အကြက္အားတိၾကေအာင္ခ်ိန္ေပးေစခ်င္ပါတယ္\n" +
                    "\n" +
                    "Permission Allow ျပလုပ္ေပးဖိူ႔လိုအပ္ပါမယ္");
            waytwo.setText("(2) လက္ကိုညိမ္ညိမ္ထားပါ Scannerေပၚတြင္ Pin Numberကို ၾကည္လည္ျပတ္သားေအာင္ခ်ိန္ထားေပးပါ");
            waythree.setText("(3)Scanဖတ္ၿပီးေသာအခါMessage Boxေလးေပၚလာပါလိမ့္မည္");
        }
        else{
            useguide.setText("ငွေဖြည့်ကဒ်စကင်နာ အပလီကေးရှင်းလေးဟာ ZawgyI Unicodeနှစ်မျိုးစလုံးကိုထောက်ပံ့ပေးပါတယ်\n" +
                    "\n" +
                    "Qr Scanမှာ Barcodeနဲ့ရောနေတတ်ပါတယ်ဒါကြောင့်qr အကွက်အားတိကြအောင်ချိန်ပေးစေချင်ပါတယ်\n" +
                    "\n" +
                    "Permission Allow ပြလုပ်ပေးဖိူ့လိုအပ်ပါမယ်");
            feature.setText("ငွေဖြည့်ကဒ် စကင်နာ Apkလေးမှာပါဝင်တဲ့ လုပ်ဆောင်ချက်တွေကတော့\n" +
                    "..\n" +
                    "ငွေဖြည့်ကဒ်အားခဲခြစ်ပြီး အလွယ်တကူကုတ်နံပါတ်အား စကင်ဖတ်ပေးနိုင်ခြင်း\n" +
                    "ကျူအာကုတ်ပါရှိသော ငွေဖြည့်ကတ်များကိုလည်းစကင်ဖတ်၍အမြန်ငွေဖြည့်ပေးနိုင်ခြင်း\n" +
                    "ငွေလက်ကျန်စစ်ဆေးနိုင်ခြင်း\n" +
                    "Mynumberသိနိုင်ခြင်း\n" +
                    "Operator Iconတစ်ခုစီကိုရွေးချယ်၍ ၎င်းoperator ၏serviceများအား ယခုapkမှလုပ်ဆောငိနိုင်ခြင်း");
            wayone.setText("(1) camera ရှေ့တွင် လူကြီးမင်း၏ခဲခြစ်ထားသော ငွေဖြည့်ကဒ်ကို လက်ဖြင့်ကိုင်ထားရွေ့သော်လည်းကောင်း တစ်စုံတစ်ခုပေါ်တွင်တင်ထား၍သော်လည်းကောင်းထားရှိပေးပါ");
            waytwo.setText("(2) လက်ကိုညိမ်ညိမ်ထားပါ Scannerပေါ်တွင် Pin Numberကို ကြည်လည်ပြတ်သားအောင်ချိန်ထားပေးပါ");
            waythree.setText("(3)Scanဖတ်ပြီးသောအခါMessage Boxလေးပေါ်လာပါလိမ့်မည်");
            }

    }

    private void initialize() {

            //request permissions

            preferences = getApplicationContext().getSharedPreferences("mypref", 0);
            editor = preferences.edit();

    }

    @Override
    public void onBackPressed() {
        if (adfacebook == interstitial) {
            interstitial.show();
        }
        super.onBackPressed();
    }
}