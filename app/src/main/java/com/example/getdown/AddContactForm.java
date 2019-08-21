package com.example.getdown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class AddContactForm extends AppCompatActivity {

    private static final String TAG = "AddContactForm";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    static final int LOCATION_REQUEST = 1;  // The request code

    private EditText mContactName;
    private EditText mAddress;
    private EditText mPhone;
    private Button mSaveContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact_form);

        mContactName = (EditText) findViewById(R.id.editText_addFriendName);
        mAddress = (EditText) findViewById(R.id.editText_chooseLocation);
        mPhone = (EditText) findViewById(R.id.editText_phoneNumber);
        mSaveContact = (Button) findViewById(R.id.button_save_contact);

        if(isServicesOK()){
            init();
        }
    }

    private void init(){
        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContactForm.this, MapsActivity.class);
                startActivityForResult(intent, LOCATION_REQUEST);
            }
        });

        mSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContactForm.this, ContactList.class);
                ContactInfo contactInfo = new ContactInfo(mContactName.getText().toString(),
                        mAddress.getText().toString(),mPhone.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putSerializable("NEW_CONTACT", contactInfo);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if(extras != null){
                    String address = extras.getString("CHOSEN_LOCATION");
                    mAddress.setText(address);
                }
            }
        }
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AddContactForm.this);

        if(available == ConnectionResult.SUCCESS){
            // Everything is fine and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // An error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: An error occurred, but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AddContactForm.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't make map requests.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
