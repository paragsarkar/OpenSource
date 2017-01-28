package com.OpenSource.engine.utils;

import android.content.Intent;
import android.net.Uri;

import com.OpenSource.engine.application.AppInstance;

import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EBean;


/**
 * Created by parag_sarkar on 27-01-2017.
 */



@EBean
public class PhoneDialer {



    @App
    AppInstance appInstance;

    public void launchPhoneDialerForMobile(String phoneNumber) {
        Intent dialIntent = new Intent();
        dialIntent.setAction(Intent.ACTION_DIAL);
        dialIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //"tel:+91valid mobile number" all indian mobile numbers covered numbers
        dialIntent.setData(Uri.parse("tel:+91" + phoneNumber));
        appInstance.startActivity(dialIntent);

    }

    public void launchPhoneDialerForFixedLine(String phoneNumber) {
        Intent dialIntent = new Intent();
        dialIntent.setAction(Intent.ACTION_DIAL);
        //"tel:+91 std code valid mobile number" all indian fixed line numbers covered numbers
        // std code is "without 0 prefix" bangalore example "tel:+918045674567"
        dialIntent.setData(Uri.parse("tel:"+phoneNumber));
    }

    public void callMobilePhone(String phoneNumber) {
        //not implemented
        //ACTION_DIAL discouraged by google...programmatically need permissions also
        //let's first do just dialer launch
    }

}
