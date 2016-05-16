package com.beefe.permissionmanger;

import android.location.Location;
import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


/**
 * Created by heng on 16/5/12.
 */
public class PermissionMgrModule extends ReactContextBaseJavaModule implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String REACT_CLASS = "RCTPermissionManager";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Represents a geographical location.
     */
    protected Location mLastLocation;

    private Callback locationCallback;

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

    @ReactMethod
    public void getLocation(Callback callback) {
        this.locationCallback = callback;
        buildGoogleApiClient();
        connect();
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getReactApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    @ReactMethod
    public void disconnect() {
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (locationCallback != null) {
            if (mLastLocation != null) {
                WritableMap map = Arguments.createMap();
                map.putDouble("latitude", mLastLocation.getLatitude());
                map.putDouble("longitude", mLastLocation.getLongitude());
                locationCallback.invoke(null, map);
            } else {
                locationCallback.invoke("No location detected. Make sure location is enabled on the device", null);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        if (locationCallback != null) {
            locationCallback.invoke("Connection failed: ConnectionResult.getErrorCode() = \" + result.getErrorCode()", null);
        }
    }
}
