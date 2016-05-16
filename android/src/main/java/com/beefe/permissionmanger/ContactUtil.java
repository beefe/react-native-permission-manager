package com.beefe.permissionmanger;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Contactables;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.text.TextUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by heng on 16/5/12.
 */
public class ContactUtil {

    private Context context;

    public ContactUtil(Context context) {
        this.context = context;
    }

    public WritableArray getContacts() {
        WritableArray contacts = Arguments.createArray();

        Map<Integer, Contact> ownerMap = getOwner();
        Map<Integer, Contact> othersMap = getOthers();

        for (Contact owner : ownerMap.values()) {
            contacts.pushMap(owner.toMap());
        }
        for (Contact others : othersMap.values()) {
            contacts.pushMap(others.toMap());
        }

        return contacts;
    }

    private static final List<String> OWNER_PROJECTION = new ArrayList<String>() {
        {
            add(Data.MIMETYPE);
            add(ContactsContract.Profile.DISPLAY_NAME);
            add(Contactables.PHOTO_URI);
            add(StructuredName.DISPLAY_NAME);
            add(StructuredName.GIVEN_NAME);
            add(StructuredName.MIDDLE_NAME);
            add(StructuredName.FAMILY_NAME);
            add(Phone.NUMBER);
            add(Phone.TYPE);
            add(Phone.LABEL);
            add(Email.DATA);
            add(Email.ADDRESS);
            add(Email.TYPE);
            add(Email.LABEL);
        }
    };

    private static final List<String> OTHERS_PROJECTION = new ArrayList<String>() {{
        add(Data.CONTACT_ID);
        addAll(OWNER_PROJECTION);
    }};

    private Map<Integer, Contact> getOwner() {
        Cursor cursor = context.getContentResolver().query(
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, Contacts.Data.CONTENT_DIRECTORY),
                OWNER_PROJECTION.toArray(new String[OWNER_PROJECTION.size()]),
                null,
                null,
                null
        );
        return getContacts(true, cursor);
    }

    private Map<Integer, Contact> getOthers() {
        Cursor cursor = context.getContentResolver().query(
                Data.CONTENT_URI,
                OTHERS_PROJECTION.toArray(new String[OTHERS_PROJECTION.size()]),
                Data.MIMETYPE + "=? OR " + Data.MIMETYPE + "=? OR " + Data.MIMETYPE + "=?",
                new String[]{Email.CONTENT_ITEM_TYPE, Phone.CONTENT_ITEM_TYPE, StructuredName.CONTENT_ITEM_TYPE},
                null
        );
        return getContacts(false, cursor);
    }

    private Map<Integer, Contact> getContacts(boolean isOwner, Cursor cursor) {
        if (cursor != null) {
            Map<Integer, Contact> map = new LinkedHashMap<>();
            while (cursor.moveToNext()) {
                int contactId = isOwner ? cursor.getColumnIndex(Data.CONTACT_ID) : cursor.getInt(cursor.getColumnIndex(Data.CONTACT_ID));
                if (!map.containsKey(contactId)) {
                    map.put(contactId, new Contact(contactId));
                }
                Contact contact = map.get(contactId);
                String name = cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME));
                if (!TextUtils.isEmpty(name) && TextUtils.isEmpty(contact.displayName)) {
                    contact.displayName = name;
                }

                String photoUri = cursor.getString(cursor.getColumnIndex(Contactables.PHOTO_URI));
                if (!TextUtils.isEmpty(photoUri)) {
                    contact.photoUri = photoUri;
                }

                String mimeType = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
                switch (mimeType) {
                    case StructuredName.CONTENT_ITEM_TYPE:
                        contact.givenName = cursor.getString(cursor.getColumnIndex(StructuredName.GIVEN_NAME));
                        contact.middleName = cursor.getString(cursor.getColumnIndex(StructuredName.MIDDLE_NAME));
                        contact.familyName = cursor.getString(cursor.getColumnIndex(StructuredName.FAMILY_NAME));
                        break;
                    case Phone.CONTENT_ITEM_TYPE:
                        String number = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));
                        if (!TextUtils.isEmpty(number)) {
                            int type = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
                            String label;
                            switch (type) {
                                case Phone.TYPE_HOME:
                                    label = "home";
                                    break;
                                case Phone.TYPE_WORK:
                                    label = "work";
                                    break;
                                case Phone.TYPE_MOBILE:
                                    label = "mobile";
                                    break;
                                default:
                                    label = "other";
                            }
                            contact.phones.add(new Contact.Item(label, number));
                        }
                        break;
                    case Email.CONTENT_ITEM_TYPE:
                        String address = cursor.getString(cursor.getColumnIndex(Email.ADDRESS));
                        if (!TextUtils.isEmpty(address)) {
                            int type = cursor.getInt(cursor.getColumnIndex(Email.TYPE));
                            String label;
                            switch (type) {
                                case Email.TYPE_HOME:
                                    label = "home";
                                    break;
                                case Email.TYPE_WORK:
                                    label = "work";
                                    break;
                                case Email.TYPE_MOBILE:
                                    label = "mobile";
                                    break;
                                case Email.TYPE_CUSTOM:
                                    String customLabel = cursor.getString(cursor.getColumnIndex(Email.LABEL));
                                    label = TextUtils.isEmpty(customLabel) ? "custom" : customLabel;
                                    break;
                                default:
                                    label = "other";
                            }
                            contact.emails.add(new Contact.Item(label, address));
                        }
                        break;
                }
            }
            cursor.close();
            return map;
        }
        return null;
    }

    private static class Contact {

        private final int contactId;
        private String displayName;
        private String givenName = "";
        private String middleName = "";
        private String familyName = "";
        private String photoUri;
        private List<Item> emails = new ArrayList<>();
        private List<Item> phones = new ArrayList<>();

        public Contact(int contactId) {
            this.contactId = contactId;
        }

        public WritableMap toMap() {
            WritableMap contactMap = Arguments.createMap();
            contactMap.putInt("recordID", contactId);
            contactMap.putString("givenName", TextUtils.isEmpty(givenName) ? displayName : givenName);
            contactMap.putString("middleName", middleName);
            contactMap.putString("familyName", familyName);
            contactMap.putString("thumbnailPath", photoUri == null ? "" : photoUri);

            WritableArray phoneNumberArray = Arguments.createArray();
            for (Item item : phones) {
                WritableMap phoneNumberMap = Arguments.createMap();
                phoneNumberMap.putString("number", item.value);
                phoneNumberMap.putString("label", item.label);
                phoneNumberArray.pushMap(phoneNumberMap);
            }
            contactMap.putArray("phoneNumbers", phoneNumberArray);

            WritableArray emailAddressArray = Arguments.createArray();
            for (Item item : emails) {
                WritableMap emailAddressMap = Arguments.createMap();
                emailAddressMap.putString("email", item.value);
                emailAddressMap.putString("label", item.label);
                emailAddressArray.pushMap(emailAddressMap);
            }
            contactMap.putArray("emailAddresses", emailAddressArray);

            return contactMap;
        }

        public static class Item {
            public String label;
            public String value;

            public Item(String label, String value) {
                this.label = label;
                this.value = value;
            }
        }
    }

}
