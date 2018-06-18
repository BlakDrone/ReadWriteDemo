package com.readwritedemo;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import adapter.CategoryAdapter;
import interfaces.OnClickListener;
import model.CategoryList;
import model.Status;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 20;

    AppCompatEditText etText;
    AppCompatButton btnSubmit, btnSelectCategory, btnAddCategory;
    Firebase mRootRef;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRefRetrive;
    String number = "", possibleEmail = "", selectedCategory = "";
    Gson gson;
    ArrayList<CategoryList> categoryLists = new ArrayList<>();
    CategoryAdapter categoryAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayList<String> SampleArrayList;
    ArrayAdapter<String> arrayAdapter;
    Pattern pattern;
    Account[] account;
    String[] StringArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        gson = new Gson();
        pattern = Patterns.EMAIL_ADDRESS;

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Statuses");
        myRefRetrive = database.getReference("Categories");
        mRootRef = new Firebase("https://readwritedemo-1dd84.firebaseio.com/");

        etText = (AppCompatEditText) findViewById(R.id.etText);
        btnSubmit = (AppCompatButton) findViewById(R.id.btnSubmit);
        btnSelectCategory = (AppCompatButton) findViewById(R.id.btnSelectCategory);
        btnAddCategory = (AppCompatButton) findViewById(R.id.btnAddCategory);

        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        Manifest.permission.GET_ACCOUNTS) || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.READ_PHONE_STATE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("GET ACCOUNT & READ PHONE STATE permission is necessary to Use this app!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.GET_ACCOUNTS,
                                            android.Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS,
                            Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
                }
            } else {
                takePicture();
            }
        } else {
            takePicture();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                myRef = database.getReference(selectedCategory);
                String id = myRef.push().getKey();
                Status status = new Status(id, etText.getText().toString(), number, possibleEmail);
                myRef.child(selectedCategory).child(id).setValue(status);
//                myRef.child(id).setValue(status);
            }
        });

        btnSelectCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category_Popup();
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = database.getReference("Categories");
                String id = myRef.push().getKey();
                CategoryList categoryList = new CategoryList(id, etText.getText().toString());
                myRef.child("Categories").child(id).setValue(categoryList);
            }
        });

        myRefRetrive.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                String s1 = gson.toJson(dataSnapshot.getValue());
                Log.e("string_S1", s1 + "");
//                sharedPrefs.save_Videos_Object(s1);
                JSONArray jsonArray_main = null;
                try {
                    jsonArray_main = new JSONArray(s1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = jsonArray_main;
                Log.e("string", jsonArray + "");
                categoryLists = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject objectCategory = jsonArray.getJSONObject(i);
                        categoryLists.add(new CategoryList(objectCategory.getString("categoryId"),
                                objectCategory.getString("categoryName")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
//                Iterator<String> keys = jsonObject.keys();
//                categoryLists = new ArrayList<>();
//
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    Log.e("KEY_NAME", key);
//                    category_list.add(new CategoryModel(key));
//                }
//
////                Log.e("Category_List", category_list.get(0).getCategory_name() + "   ===    "
////                        + category_list.get(1).getCategory_name());
//
//                videosCategoryAdapter = new VideosCategoryAdapter(VideosCategoryActivity.this, category_list);
//                rv_videoscategory.setAdapter(videosCategoryAdapter);
//
//                if (progressBar.getVisibility() == View.VISIBLE) {
//                    progressBar.setVisibility(View.GONE);
//                    tv_progress.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("", "Failed to read value.", error.toException());
            }
        });
    }

    private void takePicture() {
        number = getMyPhoneNO();
//        TextView tv = (TextView) findViewById(R.id.tv);
//        tv.setText(number);
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        Account[] accounts = AccountManager.get(MainActivity.this).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }
        Log.e("SampleArrayList_Email", possibleEmail);
    }

    private String getMyPhoneNO() {
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String mPhoneNumber = tMgr.getLine1Number();
        return mPhoneNumber;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                if (grantResults.length > 0) {
                    boolean GetAccountPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean ReadPhoneStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (GetAccountPermission && ReadPhoneStatePermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        takePicture();
                    } else {
//                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("GET ACCOUNT & READ PHONE STATE permission is necessary to Use this app!!!");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.GET_ACCOUNTS,
                                                android.Manifest.permission.READ_PHONE_STATE}, REQUEST_WRITE_PERMISSION);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    }
                }
        }
    }

    private void Category_Popup() {

        final Dialog feature_filter_popup = new Dialog(MainActivity.this);
        feature_filter_popup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        feature_filter_popup.setContentView(R.layout.popup_categorylist);
        feature_filter_popup.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(feature_filter_popup.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;

        RecyclerView rvCategoryListPopup = (RecyclerView) feature_filter_popup.findViewById(R.id.rvCategoryListPopup);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        rvCategoryListPopup.setLayoutManager(linearLayoutManager);
        categoryAdapter = new CategoryAdapter(MainActivity.this, this, categoryLists, new OnClickListener() {
            @Override
            public void onItemClicked(int position) {
                feature_filter_popup.dismiss();
                selectedCategory = categoryLists.get(position).getCategoryName();
                Toast.makeText(MainActivity.this, selectedCategory, Toast.LENGTH_LONG).show();
            }
        });
        rvCategoryListPopup.setAdapter(categoryAdapter);
        feature_filter_popup.show();
    }
}