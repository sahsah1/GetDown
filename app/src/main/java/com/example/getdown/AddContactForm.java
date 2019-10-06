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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

public class AddContactForm extends AppCompatActivity {

    private static final String TAG = "AddContactForm";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    static final int LOCATION_REQUEST = 1;  // The request code

    private EditText mContactName;
    private EditText mAddress;
    private EditText mPhone;
    private Button mSaveContact;

    private LatLng mPlace;

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
                if(mAddress.getText().toString().isEmpty()){
                    Toast.makeText(AddContactForm.this,"You must choose an address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mContactName.getText().toString().isEmpty()){
                    Toast.makeText(AddContactForm.this,"You must fill the contact's name.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mPhone.getText().toString().isEmpty()){
                    Toast.makeText(AddContactForm.this,"You must fill the contact's phone number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(AddContactForm.this, ContactList.class);
                final ContactInfo contactInfo = new ContactInfo(mContactName.getText().toString(),
                        mPlace,mPhone.getText().toString());
                Bundle bundle = new Bundle();
                //Serializer.serialize(contactInfo, "contactInfo.dat");
                bundle.putParcelable("NEW_CONTACT", contactInfo);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOCATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    mPlace = (LatLng) extras.getParcelable("CHOSEN_LOCATION");
                    mAddress.setText(extras.getString("CHOSEN_ADDRESS"));
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
