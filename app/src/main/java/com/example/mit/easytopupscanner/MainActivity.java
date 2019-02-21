package com.example.mit.easytopupscanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.InterstitialAd;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogClickInterface {
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    String operator_data = "";
    String dialog_title;
    MenuItem zaw_menu, uni_menu;public static int operatorcount=0;
    public static String operator[] = new String[2];
    private static final int PERMISSION_REQUEST_CODE = 1;
    int count = 0;
    public static String fonttext = "zaw";
    private static final int REQUEST = 112;
    private Menu menu;
    int flag = 0;
    String zaw_uni_menu = "zaw";
    String[] gridViewStrings;
    String a, b = "";
    Toolbar toolbar;
    private InterstitialAd interstitialAd;
    int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 50;
    //    public static final int request_id_multiple_permissions = 10;
    public static int[] gridViewImages = {
            com.example.mit.easytopupscanner.R.drawable.pincode,
            com.example.mit.easytopupscanner.R.drawable.qr,
            com.example.mit.easytopupscanner.R.drawable.balance,
            com.example.mit.easytopupscanner.R.drawable.sim,
    };
    private static final int REQUEST_PHONE_CALL = 108;
    private static final int REQUEST_PHONE_State = 107;
    AdView adView;

    GridView gridView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case com.example.mit.easytopupscanner.R.id.simcard_one:
                    call_menu(operator, 0);
                    return true;
                case com.example.mit.easytopupscanner.R.id.simcard_two:
                    if(operatorcount==1){
                        Toast.makeText(getApplicationContext(),"Your device has one Simcard",Toast.LENGTH_LONG);
                    }
                    else {
                        call_menu(operator, 1);
                    }
                    return true;

            }
            return false;
        }
    };

    public String getSimMenuUSSD(String val) {
        String ussd = "";
        if (val.equals("Ooredoo")) {
            ussd = "*133";
        }
        if (val.equals("MYTEL")) {
            ussd = "*966";
        }
        if (val.equals("TM") || (val.equals("Telenor") || (val.equals("TELENOR")))) {
            ussd = "*979";
        }
        if (val.equals("MPT")) {
            ussd = "*106";

        }
        return ussd;
    }


    public void LoadAdsMode() {

        adView = new AdView(this, getString(com.example.mit.easytopupscanner.R.string.facebook_banner), AdSize.BANNER_HEIGHT_50);
        LinearLayout adContainers = (LinearLayout) findViewById(com.example.mit.easytopupscanner.R.id.banner_container);
        adContainers.addView(adView);
        adView.loadAd();
        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            public void onLoggingImpression(Ad ad) {
                //
            }
        });
        adView.loadAd();

    }

    public void phonecall(String pno) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String encodeHash = Uri.encode("#");

        callIntent.setData(Uri.parse("tel:" + pno + encodeHash));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);

   }
    public void initialize() {
        //request permissions

        preferences = getApplicationContext().getSharedPreferences("mypref", 0);
        editor = preferences.edit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.mit.easytopupscanner.R.layout.activity_scrolling);
       toolbar = (Toolbar) findViewById(com.example.mit.easytopupscanner.R.id.toolbar);
       toolbar.setTitle(getString(com.example.mit.easytopupscanner.R.string.app_name));
        setSupportActionBar(toolbar);
        initialize();
        LoadAdsMode();
        checkOperator();


       final String myfont= preferences.getString("myfont","zaw");


        if(myfont.equals("zaw")){
           gridViewStrings = getResources().getStringArray(com.example.mit.easytopupscanner.R.array.zawgyi_gridview);
           //dialog_title=getString(com.example.mit.easytopupscanner.R.string.selectsim);

       }
       else{
           gridViewStrings = getResources().getStringArray(com.example.mit.easytopupscanner.R.array.unicode_gridview);
        //   dialog_title=getString(com.example.mit.easytopupscanner.R.string.selectsim_uni);
       }
       dialog_title="Choose SimCard";

        FloatingActionButton fab = (FloatingActionButton) findViewById(com.example.mit.easytopupscanner.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, HelpActivity.class);
                i.putExtra("key",zaw_uni_menu);
                startActivity(i);
            }
        });
        gridView = (GridView) findViewById(com.example.mit.easytopupscanner.R.id.grid);
        gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    startActivity(new Intent(MainActivity.this, OcrCaptureActivity.class));
                            }
                            else if (i == 1) {
                              new IntentIntegrator(MainActivity.this).setCaptureActivity(ScannerActivity.class).initiateScan();
                                             }
                                             else if (i == 3) {

                    if(operatorcount==1){
                       phonenumber(operator[0]);
                    }else {
                        showDialog("pno");
                    }
                                                               } else if (i == 2) {

                    if(operatorcount==1){
                        phonecall("*124");
                    }else {
                        showDialog("balance");
                    }

                }
            }
        });
        BottomNavigationView navigation = (BottomNavigationView) findViewById(com.example.mit.easytopupscanner.R.id.navigation);
        Menu menu=navigation.getMenu();
        MenuItem simone=menu.findItem(com.example.mit.easytopupscanner.R.id.simcard_one);

        checkOperator();
       simone.setTitle(a);
        MenuItem simtwo=menu.findItem(com.example.mit.easytopupscanner.R.id.simcard_two);
       if(a.equals("TM") || a.equals("Telenor")|| a.equals("TELENOR")) {
           simone.setTitle("Telenor");
       }
       else
       {
           simone.setTitle(a);
       }
       if(operatorcount==1) {
           simtwo.setTitle("None");
       }
       else
       {
           simtwo.setTitle(b);
       }
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private void phonenumber(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String encodeHash = Uri.encode("#");
         String ussd=getUSSD(0);
        callIntent.setData(Uri.parse("tel:" + ussd + encodeHash));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call_menu(String[] simSlotName, int simselected) {
        TelecomManager telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
        String ussd = getSimMenuUSSD(simSlotName[simselected]);
        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String encodeHash = Uri.encode("#");
        intent.setData(Uri.parse("tel:" + ussd + encodeHash));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected == 0) {   //0 for sim1
            for (String s : simSlotName)
                intent.putExtra(s, 0); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));

        } else {
            for (String s : simSlotName)
                intent.putExtra(s, 1); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));

        }
        startActivity(intent);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.example.mit.easytopupscanner.R.menu.main_menu, menu);
        zaw_menu=menu.findItem(com.example.mit.easytopupscanner.R.id.zawgyiid);
        uni_menu=menu.findItem(com.example.mit.easytopupscanner.R.id.unicode);

        String myfont= preferences.getString("myfont","zaw");

        if(myfont.equals("zaw")){
            zaw_menu.setChecked(true);
        }
        else{
            uni_menu.setChecked(true);
        }

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case com.example.mit.easytopupscanner.R.id.zawgyiid:
                changeFont("zaw");
                gridViewStrings = getResources().getStringArray(com.example.mit.easytopupscanner.R.array.zawgyi_gridview);
                gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages));
                item.setChecked(true);
                zaw_uni_menu="zaw";
                fonttext="zaw";
                return true;
            case com.example.mit.easytopupscanner.R.id.unicode:
                gridViewStrings = getResources().getStringArray(com.example.mit.easytopupscanner.R.array.unicode_gridview);
                gridView.setAdapter(new CustomAndroidGridViewAdapter(this, gridViewStrings, gridViewImages));
                changeFont("uni");
                item.setChecked(true);
                zaw_uni_menu="uni";
                fonttext="uni";
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void changeFont(String value) {
        editor.putString("myfont", value);
        editor.commit();
    }


    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }



    public void showDialog(final String value) {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Choose SimCard");
        dialog.setCancelable(true);
        dialog.setContentView(com.example.mit.easytopupscanner.R.layout.custom_dialogalertbox_opt);
        TextView sim_one = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.sim_one_id);
        TextView sim_two = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.sim_two_id);
        checkOperator();
        sim_one.setText(a);
        sim_two.setText(b);

        ImageButton close = (ImageButton) dialog.findViewById(com.example.mit.easytopupscanner.R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        sim_one.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (value.equals("balance")) {
                    call_balance(operator, 0);
                    dialog.dismiss();
                } else {
                    call_pno(operator, 0);
                    dialog.dismiss();
                }
            }
        });
        sim_two.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (value.equals("balance")) {
                    call_balance(operator, 1);
                    dialog.dismiss();
                } else {
                    call_pno(operator, 1);
                    dialog.dismiss();
                }
            }
        });
        ImageView imageone = (ImageView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.imageone);
        imageone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (value.equals("balance")) {
                    call_balance(operator, 0);   dialog.dismiss();
                } else {
                    call_pno(operator, 0);   dialog.dismiss();
                }

            }
        });
        ImageView imagetwo = (ImageView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.imagetwo);
        imagetwo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (value.equals("balance")) {
                    call_balance(operator, 1);   dialog.dismiss();
                } else {
                    call_pno(operator, 1);   dialog.dismiss();
                }
            }
        });

        CardView cardone = (CardView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.card_view_one);
        cardone.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (value.equals("balance")) {
                    call_balance(operator, 0);   dialog.dismiss();
                } else {
                    call_pno(operator, 0);   dialog.dismiss();
                }
            }
        });
        CardView cardtwo = (CardView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.card_view_two);
        cardtwo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (value.equals("balance")) {
                    call_balance(operator, 1);   dialog.dismiss();
                } else {
                    call_pno(operator, 1);   dialog.dismiss();
                }
            }
        });
        dialog.show();

    }

    private String getUSSD(int num) {
        String ussd = "";
        if (operator[num].equals("Ooredoo")) {

            ussd = "*133*6*2";
        }
        if (operator[num].equals("MYTEL")) {
            //*966*6*1*1#
            ussd = "*88";
        }
        if (operator[num].equals("MPT")) {
            ussd = "*88";
        }
        if (operator[num].equals("TM")||operator[num].equals("TELENOR")||operator[num].equals("Telenor")) {
            return "*97";
        }
        return ussd;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call_pno(String[] simSlotName, int simselected) {
        TelecomManager telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
        String ussd = getUSSD(simselected);
        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String encodeHash = Uri.encode("#");
        intent.setData(Uri.parse("tel:" + ussd + encodeHash));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected == 0) {   //0 for sim1
            for (String s : simSlotName)
                intent.putExtra(s, 0); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));

        } else {
            for (String s : simSlotName)
                intent.putExtra(s, 1); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));

        }
        startActivity(intent);


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call_balance(String[] simSlotName, int simselected) {
        TelecomManager telecomManager = (TelecomManager) this.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();

        String encodeHash = Uri.encode("#");
        String ussd = "*" + "124" + encodeHash;
        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + ussd));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected == 0) {   //0 for sim1
            for (String s : simSlotName)
                intent.putExtra(s, 0); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));

        } else {


            for (String s : simSlotName)
                intent.putExtra(s, 1); //0 or 1 according to sim.......

            if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1)
                intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));

        }
        startActivity(intent);


    }

    public void checkOperator() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<SubscriptionInfo> subscription = SubscriptionManager.from(getApplicationContext()).getActiveSubscriptionInfoList();
         operatorcount=subscription.size();
           for (int i = 0; i < subscription.size(); i++) {
                SubscriptionInfo info = subscription.get(i);

                operator[i] = info.getCarrierName() + "";
            }
            a=operator[0];
            b=operator[1];
            operator_data = operator[0];

        }

    }

    @Override
    public void onClickCallButton(DialogInterface pDialog, int pDialogIntefier) {
    }

    @Override
    public void onClickShareButton(DialogInterface pDialog, int pDialogIntefier) {
   }

    @Override
    public void onClickCancelButton(DialogInterface pDialog, int pDialogIntefier) {
        if (pDialogIntefier == 0)
            pDialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //We will get scan results here
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //check for null
        if (result != null) {
            if (result.getContents() == null) {
           //     Toast.makeText(this, "Scan Cancelled", Toast.LENGTH_LONG).show();
            } else {
                //show dialogue with result
                String text = result.getContents();
                String substring = "";
                if (text.contains("*123*")) {

                    String ss = text.replace("*123*", "");
                    substring = ss.replace("#", "");
                } else {
                    substring = result.getContents();
                }
                //     Toast.makeText(getApplicationContext(), substring+"",Toast.LENGTH_LONG).show();
                CustomAlertDialog.getInstance().showConfirmDialog("Code Recoginized", substring, "Ok", "Cancel", this, 0);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
