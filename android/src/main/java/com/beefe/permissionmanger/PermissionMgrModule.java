package com.permissiontest;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;


/**
 * Created by heng on 16/5/12.
 */
public class PermissionMgrModule extends ReactContextBaseJavaModule {

    private static final String REACT_CLASS = "RCTPermissionManager";


    public PermissionMgrModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void getAll(Callback callback) {
        ContactUtil contactUtil = new ContactUtil(getReactApplicationContext());
        WritableArray contacts = contactUtil.getContacts();
        if (callback != null) {
            callback.invoke(null, contacts);
        }
    }

    @ReactMethod
    public void getCallLog(Callback callback) {
        CallLogUtil callLogUtil = new CallLogUtil(getReactApplicationContext());
        WritableArray callLogs = callLogUtil.getCallLogs();
        if (callback != null) {
            callback.invoke(null, callLogs);
        }
    }
}
