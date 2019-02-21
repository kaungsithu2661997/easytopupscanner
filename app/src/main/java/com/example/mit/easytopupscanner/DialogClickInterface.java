package com.example.mit.easytopupscanner;
import android.content.DialogInterface;

/**
 * Created by sayosoft on 26/7/16.
 */
public interface DialogClickInterface {

    public void onClickCallButton(DialogInterface pDialog, int pDialogIntefier);
    public void onClickShareButton(DialogInterface pDialog, int pDialogIntefier);
    public void onClickCancelButton(DialogInterface pDialog, int pDialogIntefier);
}