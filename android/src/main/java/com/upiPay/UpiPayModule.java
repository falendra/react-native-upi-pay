package com.upiPay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class UpiPayModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private static final int REQUEST_CODE = 123;
    private final Gson gson = new Gson();
    private Callback successHandler;
    private Callback failureHandler;
    private String FAILURE = "FAILURE";

    public UpiPayModule(ReactApplicationContext reactContext) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
    }

    @Override
    public String getName() {
        return "UpiPay";
    }

    @ReactMethod
    public void intializePayment(ReadableMap config,ReadableMap paymentApp, Callback successHandler, Callback failureHandler) {
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        
        
        //int GOOGLE_PAY_REQUEST_CODE;

        //String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
        //GOOGLE_PAY_REQUEST_CODE = 123;
        
        String PACKAGE_NAME = paymentApp.hasKey("PACKAGE_NAME") ?
        paymentApp.getString("PACKAGE_NAME") : "com.google.android.apps.nbu.paisa.user";
        Context currentContext = getCurrentActivity().getApplicationContext();
        boolean isAppInstalled = appInstalledOrNot(PACKAGE_NAME,currentContext);

        if(isAppInstalled) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(config.getString("upiString")));
            intent.setPackage(PACKAGE_NAME);
            getCurrentActivity().startActivityForResult(intent, 123);
             
        }else{
            final JSONObject responseData = new JSONObject();
                try {
                    responseData.put("message", "UPI supporting app not installed");
                    responseData.put("status", FAILURE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                this.failureHandler.invoke(gson.toJson(responseData));
        }
        
    }

    private boolean appInstalledOrNot(String uri,Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private boolean isCallable(Intent intent, Context context) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        final JSONObject responseData = new JSONObject();
        try {
            if (data == null) {
                responseData.put("status", FAILURE);
                responseData.put("message", "No action taken");
                this.failureHandler.invoke(gson.toJson(responseData));
                return;
            }

            if (requestCode == REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                if (data.getStringExtra("Status").equals("SUCCESS") || data.getStringExtra("Status").equals("Success") ) {
                    responseData.put("status", data.getStringExtra("Status"));
                    responseData.put("message", bundle.getString("response"));
                    this.successHandler.invoke(gson.toJson(responseData));

                } else {
                    responseData.put("status", data.getStringExtra("Status"));
                    responseData.put("message", bundle.getString("response"));
                    this.failureHandler.invoke(gson.toJson(responseData));      
                }
            } else {
                responseData.put("message", "Request Code Mismatch");
                responseData.put("status", FAILURE);
                this.failureHandler.invoke(gson.toJson(responseData));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}
