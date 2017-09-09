package com.example.utsav.savebulk;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    File sdcard;
    File file;
    BufferedReader br;
    String line="";
    String fname;
    String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button save=(Button) findViewById(R.id.button);
        Button delete=(Button)findViewById(R.id.button2);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContact();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletecontact();
            }
        });

    }

    private void addContact() {

        try {
            sdcard = Environment.getExternalStorageDirectory();


            file = new File(sdcard, "groupcontacts.txt");

            StringBuilder text = new StringBuilder();


            br = new BufferedReader(new FileReader(file));
        }catch (Exception e) {
            Toast.makeText(this,"File Not Found",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        try{
            while ((line = br.readLine()) != null) {
                String[] temp=line.split(" ");
                number=temp[temp.length-1];
                if(temp.length>2){
                   fname=temp[0]+temp[1];
                }
                else{
                    fname=temp[0];
                }

                ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
                operationList.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());


                operationList.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(StructuredName.GIVEN_NAME, "GroupAdd")
                        .withValue(StructuredName.FAMILY_NAME, fname)
                        .build());

                operationList.add(ContentProviderOperation.newInsert(Data.CONTENT_URI)
                        .withValueBackReference(Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_MOBILE)
                        .build());


                try{
                    ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, operationList);
                    System.out.println(results);
                }catch(Exception e){
                    e.printStackTrace();
                }



            }
            Toast.makeText(this,"Contacts Added",Toast.LENGTH_LONG).show();
        }catch (Exception e){}




    }
    private void deletecontact() {
        ContentResolver mContentResolver = getContentResolver();
        mContentResolver.delete(
                ContactsContract.RawContacts.CONTENT_URI,
                ContactsContract.Contacts.DISPLAY_NAME
                        + " like ?",
                new String[] { "GroupAdd" + '%'});
        Toast.makeText(this,"Contacts Deleted",Toast.LENGTH_LONG).show();

    }


}
