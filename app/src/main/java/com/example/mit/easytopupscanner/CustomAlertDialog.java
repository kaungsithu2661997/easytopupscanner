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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class CustomAlertDialog implements DialogClickInterface, DialogInterface.OnClickListener {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static CustomAlertDialog mDialog;
    Dialog dialog;
    String stitle="";String smessage="";
    String data;
    public DialogClickInterface mDialogClickInterface;
    private int mDialogIdentifier;
    private Context mContext;

    private static final int PERMISSION_REQUEST_CODEs = 1;
    String[] operator=new String[2];
    public static CustomAlertDialog getInstance() {

        if (mDialog == null)
            mDialog = new CustomAlertDialog();

        return mDialog;

    }

    /**
     * Show confirmation dialog with two buttons
     *
     * @param pMessage
     * @param pPositiveButton
     * @param pNegativeButton
     * @param pContext
     * @param pDialogIdentifier
     */
    public void showConfirmDialog(String pTitle, String pMessage,
                                  String pPositiveButton, String pNegativeButton,
                                  Context pContext, final int pDialogIdentifier) {

        mDialogClickInterface = (DialogClickInterface) pContext;
        mDialogIdentifier = pDialogIdentifier;
        mContext = pContext;

        dialog = new Dialog(pContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(com.example.mit.easytopupscanner.R.layout.custom_confirm_dialog);


        //String myfont= MainActivity.fonttext;

//        if(myfont.equals("zaw")){
//
//        }
//        else{
//         stitle= "လူကြီးမင်း၏ငွေဖြည့်ကဒ်နံပါတ်ကိုအသုံးပြု၍ဖုန်းငွေဖြည့်သွင်းနိုင်ပါပီ";
//           smessage="Choose Simcard";
//        }
        stitle="Code Recognized";
        smessage= "Choose Simcard";
        TextView title = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.textTitle);
        title.setText(stitle);
        title.setVisibility(View.VISIBLE);


        EditText message_value = (EditText) dialog.findViewById(com.example.mit.easytopupscanner.R.id.textDialog);
        message_value.setText(pMessage);
        data = pMessage;
        ImageButton call = (ImageButton) dialog.findViewById(com.example.mit.easytopupscanner.R.id.call);
        ImageButton share = (ImageButton) dialog.findViewById(com.example.mit.easytopupscanner.R.id.share);
        ImageButton cancel = (ImageButton) dialog.findViewById(com.example.mit.easytopupscanner.R.id.cancel);


        dialog.setCancelable(false);
        dialog.show();      // if decline button is clicked, close the custom dialog
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                mDialogClickInterface.onClickCallButton(dialog, pDialogIdentifier);
                int operatorcount=MainActivity.operatorcount;
                if(operatorcount==1){
                    phonebill();
                }
                else {
                    showDialog();
                }
              // dialog.dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                mDialogClickInterface.onClickShareButton(dialog, pDialogIdentifier);
                shareText();
            //    dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                mDialogClickInterface.onClickCancelButton(dialog, pDialogIdentifier);
                dialog.dismiss();
            }
        });

    }

    private void phonebill() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        String encodeHash = Uri.encode("#");

        callIntent.setData(Uri.parse("tel:*123*" + data + encodeHash));
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mContext.startActivity(callIntent);
    }

    public void shareText() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = "" + data;
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Refill Card PIN Number");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        mContext.startActivity(Intent.createChooser(intent, "Choose sharing method"));
    }

    @Override
    public void onClick(DialogInterface pDialog, int pWhich) {

        switch (pWhich) {
            case DialogInterface.BUTTON1:
                mDialogClickInterface.onClickCallButton(pDialog, mDialogIdentifier);

                break;
            case DialogInterface.BUTTON2:
                mDialogClickInterface.onClickShareButton(pDialog, mDialogIdentifier);
                break;
            case DialogInterface.BUTTON3:
                mDialogClickInterface.onClickCancelButton(pDialog, mDialogIdentifier);
                break;
        }

    }

    public void showDialog() {
        operator=MainActivity.operator;
        final Dialog dialog = new Dialog(mContext);
        dialog.setTitle("Choose Simcard");
        dialog.setCancelable(true);
        dialog.setContentView(com.example.mit.easytopupscanner.R.layout.custom_dialogalertbox_opt);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView sim_one = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.sim_one_id);
        TextView sim_two = (TextView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.sim_two_id);
        ImageButton close=(ImageButton)dialog.findViewById(com.example.mit.easytopupscanner.R.id.close);
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
                call_balance(operator,0);
            }
        });
        sim_two.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                call_balance(operator,1);
            }
        });
        ImageView imageone=(ImageView)dialog.findViewById(com.example.mit.easytopupscanner.R.id.imageone);
        imageone.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                call_balance(operator,0);

            }
        });
        ImageView imagetwo=(ImageView)dialog.findViewById(com.example.mit.easytopupscanner.R.id.imagetwo);
        imagetwo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                call_balance(operator,1);

            }
        });
        sim_one.setText(operator[0]);
        sim_two.setText(operator[1]);

        CardView cardone = (CardView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.card_view_one);
        cardone.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                call_balance(operator,0);
            }
        });
        CardView cardtwo = (CardView) dialog.findViewById(com.example.mit.easytopupscanner.R.id.card_view_two);
        cardtwo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                call_balance(operator,1);

            }
        });
        dialog.show();

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public void call_balance(String []simSlotName,int simselected) {
        TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(Context.TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();
        String encodeHash = Uri.encode("#");
        String ussd = "*123*"+data;
        Intent intent = new Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("tel:" + ussd + encodeHash));
        intent.putExtra("com.android.phone.force.slot", true);
        intent.putExtra("Cdma_Supp", true);
        if (simselected== 0) {   //0 for sim1
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
        this.mContext.startActivity(intent);


    }

    @Override
    public void onClickCallButton(DialogInterface pDialog, int pDialogIntefier) {

    }

    @Override
    public void onClickShareButton(DialogInterface pDialog, int pDialogIntefier) {

    }

    @Override
    public void onClickCancelButton(DialogInterface pDialog, int pDialogIntefier) {

    }
}