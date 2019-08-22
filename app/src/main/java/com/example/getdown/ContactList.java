package com.example.getdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactList extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST = 1;
    List<HashMap<String, String>> aList;
    SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactList.this, AddContactForm.class);
                startActivityForResult(intent, ADD_CONTACT_REQUEST);
            }
        });

        loadContactList();

        String[] from = {"listview_image", "listview_title"};//, "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title};//, R.id.listview_item_short_description};

        simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_activity, from, to);
        ListView androidListView = findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if(extras != null){
                    ContactInfo contactInfo = (ContactInfo) extras.getSerializable("NEW_CONTACT");
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("listview_title", contactInfo.getName());
                    //hm.put("listview_description", "This is a short description.");
                    hm.put("listview_image", Integer.toString(R.drawable.ic_profile_pic));
                    aList.add(hm);
                    simpleAdapter.notifyDataSetChanged();
                    saveContactList();
                }
            }
        }
    }

    private void saveContactList(){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String json = gson.toJson(aList);
        editor.putString("CONTACT_LIST", json);
        editor.apply();
    }

    private void loadContactList(){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_PREFERENCES", MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString("CONTACT_LIST", null);
        Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {}.getType();
        aList = gson.fromJson(json, type);

        if(aList == null){
            aList = new ArrayList<>();
        }
    }
}
