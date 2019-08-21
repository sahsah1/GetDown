package com.example.getdown;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactList extends AppCompatActivity {

    static final int ADD_CONTACT_REQUEST = 1;
    List<HashMap<String, String>> aList;
    SimpleAdapter simpleAdapter;

//    String[] listviewTitle = new String[]{
//            "ListView Title 1", "ListView Title 2", "ListView Title 3", "ListView Title 4",
//            "ListView Title 5", "ListView Title 6", "ListView Title 7", "ListView Title 8",
//    };
//
//    int[] listviewImage = new int[]{
//            R.drawable.ic_profile_pic, R.drawable.ic_profile_pic, R.drawable.ic_profile_pic, R.drawable.ic_profile_pic,
//            R.drawable.ic_profile_pic, R.drawable.ic_profile_pic, R.drawable.ic_profile_pic, R.drawable.ic_profile_pic,
//    };
//
//    String[] listviewShortDescription = new String[]{
//            "Android ListView Short Description", "Android ListView Short Description", "Android ListView Short Description", "Android ListView Short Description",
//            "Android ListView Short Description", "Android ListView Short Description", "Android ListView Short Description", "Android ListView Short Description",
//    };


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

        aList = new ArrayList<HashMap<String, String>>();

//        for (int i = 0; i < 8; i++) {
//            HashMap<String, String> hm = new HashMap<String, String>();
//            hm.put("listview_title", listviewTitle[i]);
//            hm.put("listview_description", listviewShortDescription[i]);
//            hm.put("listview_image", Integer.toString(listviewImage[i]));
//            aList.add(hm);
//        }

        String[] from = {"listview_image", "listview_title", "listview_description"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

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
                    hm.put("listview_description", "This is a short description.");
                    hm.put("listview_image", Integer.toString(R.drawable.ic_profile_pic));
                    aList.add(hm);
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        }
    }
}
