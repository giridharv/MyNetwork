package com.example.giridhar.mynetwork;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener,View.OnFocusChangeListener{
EditText etname,etmailid,etpassword,etyear,etcity;
    Spinner spcountry,spstate;
    Button btcityset,btregister;
    private static int reqCode=22;
    double latitudeValue,longitudeValue;
    String countrySelected,stateSelected;
    ArrayList<String> countryList;
    ArrayList<String> stateList;
    static boolean movedToNext=false;
    ArrayList<Address> addressList= new ArrayList<>();
    //boolean existingUser=false;
    PersonDetails personDetails;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        etname=(EditText)findViewById(R.id.editText3);
        etmailid=(EditText)findViewById(R.id.editText5);
        etpassword=(EditText)findViewById(R.id.editText4);
        etyear=(EditText)findViewById(R.id.editText6);
        etcity=(EditText)findViewById(R.id.editText7);
        spcountry=(Spinner)findViewById(R.id.spinner);
        spstate=(Spinner)findViewById(R.id.spinner2);
        btcityset=(Button)findViewById(R.id.citySet);
        btregister=(Button)findViewById(R.id.registerUser);
        firebaseAuth=FirebaseAuth.getInstance();
        etname.setOnFocusChangeListener(this);
        if(savedInstanceState!=null && movedToNext==true)
        {
            etname.setText(savedInstanceState.getString("nickname"));
            etpassword.setText(savedInstanceState.getString("password"));
            etmailid.setText(savedInstanceState.getString("mail"));
            etcity.setText(savedInstanceState.getString("city"));
            etyear.setText(savedInstanceState.getInt("year"));
        }


    }

    @Override
    protected void onStart() {
        btcityset.setOnClickListener(this);
        btregister.setOnClickListener(this);
        spcountry.setOnItemSelectedListener(this);
        spstate.setOnItemSelectedListener(this);
        loadCountryList();
        super.onStart();
    }

    @Override
    public void onClick(View v)
    {
      switch(v.getId())
      {
          case R.id.citySet:
              movedToNext=true;
              Intent i= new Intent(this,ContainerForMaps.class);
              startActivityForResult(i,reqCode);
              break;
          case R.id.registerUser:
             try {
                  formSubmit();
              } catch (IOException e) {
                  e.printStackTrace();
              }
                 //loadForTemp();
              break;
       }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        movedToNext=false;
        if(resultCode==22)
        {
            switch (resultCode)
            {
                case RESULT_OK:
                    latitudeValue=data.getDoubleExtra("latitude",0.0);
                    longitudeValue=data.getDoubleExtra("longitude",0.0);
                   // System.out.println(latitudeValue + "" +longitudeValue);

                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this,"No latitude longitude values selected",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public void loadCountryList()
    {
        //countryList.clear();
        countryList= new ArrayList<>();
        countryList.add("Select country");
        RequestQueue countryListFromServer= VolleyHelper.getInstance().getRequestQueue();
        String url= "http://bismarck.sdsu.edu/hometown/countries";
        JsonArrayRequest listOfCountries = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                //System.out.println(response);

                for(int i=0;i<response.length();i++)
                {
                    // JSONObject responseData= new JSONObject();
                    try {
                        countryList.add(response.get(i).toString());
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter<String> countrylistpopulator = new ArrayAdapter<String>(RegisterUser.this,android.R.layout.simple_spinner_item,countryList);
                spcountry.setAdapter(countrylistpopulator);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });countryListFromServer.add(listOfCountries);
        return;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
      switch (parent.getId())
      {
          case R.id.spinner:
              countrySelected=parent.getSelectedItem().toString();
              loadStateList(countrySelected);
              break;
          case R.id.spinner2:
              stateSelected=parent.getSelectedItem().toString();
              break;
      }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
    public void loadStateList(String country)
    {
        stateList = new ArrayList<>();
        stateList.add("Select state");
        RequestQueue stateListFromServer= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/states?country=" + country;
        JsonArrayRequest listOfStates= new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {


                for (int i = 0; i < response.length(); i++) {
                    try {
                        stateList.add(response.get(i).toString());
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(RegisterUser.this, android.R.layout.simple_spinner_item, stateList);
                spstate.setAdapter(stateAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });stateListFromServer.add(listOfStates);
        return;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        String nickname= etname.getText().toString();
        String password= etpassword.getText().toString();
        String email= etmailid.getText().toString();
        String city= etcity.getText().toString();
        String yearval= etyear.getText().toString();
        int year= Integer.parseInt(yearval);
        outState.putString("nickname",nickname);
        outState.putString("password",password);
        outState.putString("mail",email);
        outState.putString("city",city);
        outState.putInt("year",year);
    }
    public void formSubmit() throws IOException {
        String nickname = etname.getText().toString();
        String password = etpassword.getText().toString();
        String email = etmailid.getText().toString();
        String city = etcity.getText().toString();
        String yearval = etyear.getText().toString();
        //boolean res= checkForExistingUser(nickname);
        if (nickname.equals("")) {
            etname.setError("Please enter nickname to proceed");
            etname.requestFocus();
        }
        else if (password.equals(""))
        {
            etpassword.setError("Enter password to proceed");
            etpassword.requestFocus();
        }
        else if (password.length() < 3)
        {
            etpassword.setError("Password length should be atleast 3");
            etpassword.requestFocus();
        }
        else if (email.equals(""))
        {
            etmailid.setError("Enter email to proceed");
            etmailid.requestFocus();
        }
        else if(city.equals(""))
        {
            etcity.setError("Enter city to proceed");
            etcity.requestFocus();
        }
        else if(yearval.equals(""))
        {
            etyear.setError("Enter year to proceed");
            etyear.requestFocus();
        }
        else if(Integer.parseInt(yearval)<1970 ||Integer.parseInt(yearval)>2017)
        {
            etyear.setError("Year shoud be between 1970 and 2017");
        }
        else if(yearval.length()<4|| yearval.length()>4)
        {
            etyear.setError("Year should be of length 4");
            etyear.requestFocus();
        }
        else if(countrySelected==null)
        {
            Toast.makeText(this,"Choose country to proceed",Toast.LENGTH_LONG).show();

        }
        else if(countrySelected==null || stateSelected==null)
        {
            Toast.makeText(this,"Choose country to proceed",Toast.LENGTH_LONG).show();
        }
        else
        {
          checkLatLng();
          saveDataToServer();
          saveDataToFirebase();
        }

//checkForExistingUser(nickname);
    }
    public void checkForExistingUser(String temp)
    {
        RequestQueue requestQueueObj= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/nicknameexists?name="+temp;
        StringRequest stringRequest= new StringRequest(Request.Method.GET,url,new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                if(response.equals("true"))
                {
                    etname.setError("Username exists");
                    etname.requestFocus();
                    //Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                }
                else
                {
                    //Toast.makeText(getActivity(),"FALSE",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("msg","W");
            }
        });
        requestQueueObj.add(stringRequest);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(!hasFocus)
        {
            String nick= etname.getText().toString();
            checkForExistingUser(nick);
        }

    }
//    public void loadForTemp()
//    {
//        Intent i= new Intent(this,NavDrawer.class);
//        startActivity(i);
//    }

    public void saveDataToServer()
    {
        String nicknameValue = etname.getText().toString();
        String passwordValue = etpassword.getText().toString();
        String countryValue = countrySelected;
        String stateValue = stateSelected;
        String cityValue = etcity.getText().toString();
        String yearval= etyear.getText().toString();
        JSONObject serverData = new JSONObject();
        try {
            int year=Integer.parseInt(yearval);
            serverData.put("nickname", nicknameValue);
            serverData.put("password", passwordValue);
            serverData.put("country", countryValue);
            serverData.put("state", stateValue);
            serverData.put("city", cityValue);
            serverData.put("year", year);
            serverData.put("latitude", latitudeValue);
            serverData.put("longitude", longitudeValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestQueue serverPost = VolleyHelper.getInstance().getRequestQueue();
        String url = "http://bismarck.sdsu.edu/hometown/adduser";
        JsonObjectRequest dataForServer = new JsonObjectRequest(Request.Method.POST, url, serverData, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if (response.get("message").toString().equals("ok")) {
                        Toast.makeText(RegisterUser.this, "User added Successfully to server", Toast.LENGTH_LONG).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("msg", "Error");
            }

        });
        serverPost.add(dataForServer);
        return;
    }

    private void resetFormData()
    {
        etname.setText("");
        etpassword.setText("");
        etcity.setText("");
        etmailid.setText("");
        etyear.setText("");
        spcountry.setSelection(0);
        spstate.setSelection(0);
    }

    public void saveDataToFirebase()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refObj= database.getReference("users");
        String nicknameValue = etname.getText().toString();
        String passwordValue = etpassword.getText().toString();
        String emailValue= etmailid.getText().toString();
        String[] splitEmailKey= emailValue.split("\\.");
        String splitemail=splitEmailKey[0];
        String countryvalue=countrySelected;
        String statevalue= stateSelected;
        String city=etcity.getText().toString();
        String yearvalue=etyear.getText().toString();
        int year1= Integer.parseInt(yearvalue);
        personDetails= new PersonDetails();
        personDetails.setUsername(nicknameValue);
        personDetails.setPassword(passwordValue);
        personDetails.setCountry(countryvalue);
        personDetails.setState(statevalue);
        personDetails.setJoiningYear(year1);
        personDetails.setCity(city);
        //personDetails.setUniqueIdForFirebase(firebaseAuth.getCurrentUser().getUid());
        refObj.child(nicknameValue).setValue(personDetails);

        firebaseAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(RegisterUser.this, new OnCompleteListener<AuthResult>()
                {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if(task.isSuccessful())
                {
                  attachUserNickNameToFireBaseAuth(etname.getText().toString(),task.getResult().getUser());
                }
            }
        }
    );
        return;
    }

    public void attachUserNickNameToFireBaseAuth(String name,FirebaseUser userid)
    {
        final UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        userid.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(RegisterUser.this, "User registered to firebase", Toast.LENGTH_SHORT)
                                    .show();
                            resetFormData();
                            Intent intent = new Intent(RegisterUser.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                }
       );
    }

    public void checkLatLng() throws IOException {

        if(latitudeValue==0.0||longitudeValue==0.0)
        {
            Geocoder geocoder= new Geocoder(RegisterUser.this);
            addressList= (ArrayList<Address>) geocoder.getFromLocationName(stateSelected+", "+countrySelected,1);
            for(Address resdata: addressList)
            {
                latitudeValue=resdata.getLatitude();
                longitudeValue=resdata.getLongitude();
            }
        }
        return;
    }

}
