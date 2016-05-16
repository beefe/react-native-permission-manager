package com.beefe.permissionmanger;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog.Calls;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;

/**
 * Created by heng on 16/5/13.
 */
public class CallLogUtil {

    private Context context;

    public CallLogUtil(Context context) {
        this.context = context;
    }

    public WritableArray getCallLogs() {
        WritableArray callLogArray = Arguments.createArray();
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(allCalls, null, null, null, null);
        ArrayList<CallLogInfo> infoArrayList = readCallLog(cursor);
        if (infoArrayList != null) {
            for (CallLogInfo info : infoArrayList) {
                callLogArray.pushMap(infoToMap(info));
            }
        }
        return callLogArray;
    }

    private WritableMap infoToMap(CallLogInfo info) {
        WritableMap map = Arguments.createMap();
        map.putString("number", info.getNumber());
        map.putString("cachedName", info.getCachedName());
        map.putString("duration", info.getDuration());
        map.putString("date", info.getDate());
        map.putString("location", info.getLocation());
        map.putString("callType", info.getCallType());
        return map;
    }

    private ArrayList<CallLogInfo> readCallLog(Cursor cursor) {
        ArrayList<CallLogInfo> infoArrayList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                CallLogInfo callLogInfo = new CallLogInfo();

                String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
                callLogInfo.setNumber(number);

                String cachedName = cursor.getString(cursor.getColumnIndex(Calls.CACHED_NAME));
                callLogInfo.setCachedName(cachedName);

                String duration = cursor.getString(cursor.getColumnIndex(Calls.DURATION));
                callLogInfo.setDuration(duration);

                String date = cursor.getString(cursor.getColumnIndex(Calls.DATE));
                callLogInfo.setDate(date);

                String location = cursor.getString(cursor.getColumnIndex(Calls.GEOCODED_LOCATION));
                callLogInfo.setLocation(location);

                String callType = "";
                int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE)));
                switch (type) {
                    case Calls.OUTGOING_TYPE:
                        callType = "呼出";
                        break;
                    case Calls.INCOMING_TYPE:
                        callType = "来电";
                        break;
                    case Calls.MISSED_TYPE:
                        callType = "未接来电";
                        break;
                }
                callLogInfo.setCallType(callType);
                infoArrayList.add(callLogInfo);
            }
            cursor.close();
            return infoArrayList;
        }
        return null;
    }

    private class CallLogInfo {
        private String number;
        private String cachedName;
        private String duration;
        private String date;
        private String location;
        private String callType;

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public String getCachedName() {
            return cachedName;
        }

        public void setCachedName(String cachedName) {
            this.cachedName = cachedName;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getCallType() {
            return callType;
        }

        public void setCallType(String callType) {
            this.callType = callType;
        }

    }
}
