package com.example.getdown;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class AddFriendForm extends AppCompatActivity {

    private static final String TAG = "AddFriendForm";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private EditText mFriendName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_form);

        mFriendName = (EditText) findViewById(R.id.editText_addFriendName);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("FRIEND_NAME");
            mFriendName.setText(value);
        }

        if(isServicesOK()){
            init();
        }
    }

    private void init(){
        EditText et = findViewById(R.id.editText_chooseLocation);
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddFriendForm.this, MapsActivity.class);
                intent.putExtra("FRIEND_NAME", mFriendName.getText());
                startActivity(intent);
            }
        });
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(AddFriendForm.this);

        if(available == ConnectionResult.SUCCESS){
            // Everything is fine and user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            // An error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: An error occurred, but we can fix it.");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(AddFriendForm.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't make map requests.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
