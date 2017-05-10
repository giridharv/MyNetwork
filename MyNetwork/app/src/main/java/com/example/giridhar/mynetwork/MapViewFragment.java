package com.example.giridhar.mynetwork;


import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener,AdapterView.OnItemSelectedListener, GoogleMap.OnInfoWindowClickListener {
    MapView viewOfMap;
    View v;
    GoogleMap gmap;
    Marker marker;
    Button btclearFilter, btsetFilter, btLoadData;
    Spinner spCountry, spState;
    EditText etyear;
    ArrayList<Address> addressForMap = new ArrayList<>();
    String countryChosen, stateChosen;
    ArrayList<String> countryListArray = new ArrayList<>();
    ArrayList<String> stateListArray = new ArrayList<>();
    // String urlValue;
    DatabaseHelper databaseHelper;
    int pageNumber = 0;
    PersonDetails mapPlotterObj;
    ArrayList<PersonDetails> serverdata = new ArrayList<>();
    boolean isClicked=false;
    int beforeID=0;
    String localCountryFilterValue,localStateFilterValue;
    int localyearFilterValue;
    ArrayList<String> usersFromFirebase = new ArrayList<>();
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    ArrayList<PersonDetails> asyncData;
    double latitudeValue,longitudeValue;
    float zoomValue;
    ArrayList<Address>addressListForPlot= new ArrayList<>();
    double latfromFilter,lonFromFilter;
    public MapViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_map_view, container, false);
        etyear = (EditText) v.findViewById(R.id.editText10);
        btsetFilter = (Button) v.findViewById(R.id.button2);
        btclearFilter = (Button) v.findViewById(R.id.button7);
        btLoadData = (Button) v.findViewById(R.id.button8);
        spCountry = (Spinner) v.findViewById(R.id.countrySpinner);
        spState = (Spinner) v.findViewById(R.id.stateSpinner);
        viewOfMap = (MapView) v.findViewById(R.id.mapView2);
        firebaseDatabase =FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        getFirebaseUsers();
        loadCountryList();
        viewOfMap.onCreate(savedInstanceState);
        viewOfMap.onResume();
        viewOfMap.getMapAsync(this);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btLoadData.setOnClickListener(this);
        btsetFilter.setOnClickListener(this);
        btclearFilter.setOnClickListener(this);
        spCountry.setOnItemSelectedListener(this);
        spState.setOnItemSelectedListener(this);
        if(serverdata.isEmpty())
        {
            btLoadData.setEnabled(false);
        }
        else
        {
            btLoadData.setEnabled(true);
        }
        viewOfMap.postInvalidate();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setOnMapClickListener(this);
        gmap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onMapClick(LatLng latLng)
    {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button8:

                        isClicked=true;
                        generateUrl(etyear.getText().toString(), getPageNumber());
                try
                {
                    String countryval = localCountryFilterValue;
                    String stateval =localStateFilterValue;
                    getLocation(countryval,stateval);
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                break;
            case R.id.button2:
                if(!serverdata.isEmpty())
                {
                    gmap.clear();
                    serverdata.clear();
                    setPageNumber(0);
                    btLoadData.setEnabled(false);
                    isClicked=false;
                    generateUrl(etyear.getText().toString(), getPageNumber());
                }
                else
                {
                    gmap.clear();
                    setPageNumber(0);
                    isClicked=false;
                   generateUrl(etyear.getText().toString(), pageNumber);
                }
                break;
            case R.id.button7:
                clearFilterValues();
        }
    }

    private void clearFilterValues()
    {
        gmap.clear();
        spCountry.setSelection(0);
        spState.setSelection(0);
        countryChosen=null;
        stateChosen=null;
        int yearFilter;
        String year=etyear.getText().toString();
        if(year.length()==4)
        {
            yearFilter= Integer.parseInt(year);
            etyear.setText("0");
            yearFilter=0;
        }
      isClicked=false;

    }

    public void loadCountryList()
     {
           // stateListArray.add("Select state");
            countryListArray.clear();
            countryListArray.add("Select country");
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
                            countryListArray.add(response.get(i).toString());
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> countrylistpopulator = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,countryListArray);
                    spCountry.setAdapter(countrylistpopulator);


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
         case R.id.countrySpinner:
                   countryChosen=parent.getItemAtPosition(position).toString();
                   loadStateList(countryChosen);


               break;
         case R.id.stateSpinner:
               stateChosen=parent.getItemAtPosition(position).toString();
               break;
     }
    }

    public void loadStateList(String countryvalue)
    {
        stateListArray.clear();
        stateListArray.add("Select state");
        RequestQueue stateListFromServer= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/states?country=" + countryvalue;
        JsonArrayRequest listOfStates= new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        stateListArray.add(response.get(i).toString());
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, stateListArray);
                spState.setAdapter(stateAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });stateListFromServer.add(listOfStates);
        return;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }
    public void generateUrl(String yearvalue,int pageValue)
    {
        //pageNumber=getPageNumber();
        //setPageNumber(0);
        String countryFilter = countryChosen;
        String stateFilter = stateChosen;
        int yearFilter;
        if(yearvalue.isEmpty())
        {
          yearFilter=0;
        }
        else
        {
            yearFilter = Integer.parseInt(yearvalue);
        }
        if(countryFilter.contains("Select") && (stateFilter==null) && yearFilter==0)
        {
            String urlValue="http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true ";
            String query= " select * from USERDETAILS " + " order by " + " id " + " desc ";
            getFilters(null,null,0);
            checkDatabase(query,urlValue);
        }
        else if(countryFilter.contains("Select")&&(stateFilter==null) && yearFilter!=0 )
        {
            String urlValue="http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true"+"?year="+yearFilter;
            String query= " select * from USERDETAILS where " + " dateOfEntry " + " = '" + yearFilter + "' order by " + " id " + "desc";
            getFilters(null,null,yearFilter);
            checkDatabase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && stateFilter.contains("Select") && yearFilter==0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter;
            String query="select * from USERDETAILS where" + " country " + " = '" + countryFilter + "' order by " + " id " + " desc ";
            getFilters(countryFilter,null,0);
            checkDatabase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && stateFilter.contains("Select") && yearFilter!=0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true"+"&country ="+countryFilter+"&year="+yearFilter;
            String query=" select * from USERDETAILS where " + " country " + " = '" + countryFilter + "' and " + " dateOfEntry " + " = '" + yearFilter + "' order by " + " id " + " desc ";
            getFilters(countryFilter,null,yearFilter);
            checkDatabase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && !stateFilter.contains("Select") && yearFilter ==0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter+"&state="+stateFilter;
            String query=" select * from USERDETAILS where " +  " country " + " = '" + countryFilter + "' and " + " state " + "= '" + stateFilter + "' order by " + " id " + " desc ";
            getFilters(countryFilter,stateFilter,0);
            checkDatabase(query,urlValue);
        }
        else
        {
            String  urlValue= "http://bismarck.sdsu.edu/hometown/users?page="+pageValue+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter+"&state="+stateFilter+"&year="+yearFilter;
//            checkDatabase(countryFilter,stateFilter,yearFilter,urlValue);
            String query = " select * from USERDETAILS where " + " country " + " = '" + countryFilter + "' and " + " state " + " = '" + stateFilter + "' and " + " dateOfEntry " + " = '" + yearFilter + "' order by " + " id " + " desc ";
            getFilters(countryFilter,stateFilter,yearFilter);
            checkDatabase(query,urlValue);
        }
    }

    private void checkDatabase(String query, String urlValue)
    {
        String queriedargument=query;
        final String urlGenerated =urlValue;
        ArrayList<PersonDetails>listOfUserNames = new ArrayList<>();
        DatabaseHelper db =new DatabaseHelper(getContext());
        listOfUserNames=db.getPersonDetails();
        if(!listOfUserNames.isEmpty())
        {
            ArrayList<Integer>listOfUserIds = new ArrayList<>();
            for(PersonDetails personObj:listOfUserNames)
            {
                listOfUserIds.add(personObj.getIdForPerson());
            }
            if(!isClicked)
            {
                int maxid= listOfUserNames.get(0).getIdForPerson();
                checkForDataConsistency(maxid,queriedargument,urlGenerated);
            }
            else
            {
              loadMoreData(urlGenerated);
            }
        }
        else
        {
            Toast.makeText(getContext(),"Requested Data not found..Contacting server",Toast.LENGTH_LONG).show();
            getNextID(new nextID() {
                @Override
                public void getnextIdValue(int value) {
                    int nextidval=value;
                   // urlGenerated=urlGenerated +"beforeid="+nextidval;
                    loadData(urlGenerated);
                    btLoadData.setEnabled(true);
//                    int page= getPageNumber();
//                    setPageNumber(page+1);

                }
            });

        }
    }

    private void loadMoreData(String urlGenerated)
    {
        ArrayList<PersonDetails> dataInDb = new ArrayList<>();
        DatabaseHelper db =new DatabaseHelper(getContext());
        int arayLength=0;
        int minid= 0;
        try {
            arayLength=(serverdata.size())-1;
            minid  =  serverdata.get(arayLength).getIdForPerson();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

        String countryfilterval = localCountryFilterValue;
        String statefilterval = localStateFilterValue;
        int yearfilterval = localyearFilterValue;
        String resultUrl = generateUrlForLoadMore(countryfilterval,statefilterval,yearfilterval);
        resultUrl = resultUrl + " and " + " id " + " < '" + minid + "' order by " + " id " + " desc " + " limit '" + 100 + "'";
        System.out.println(resultUrl);
        dataInDb = db.getFilterData(resultUrl);
        if(!dataInDb.isEmpty())
        {
            Toast.makeText(getContext(),"Next 100 data in db!!",Toast.LENGTH_LONG).show();
            for(PersonDetails personObj: dataInDb)
            {
                System.out.println("in load more" + personObj.getIdForPerson());
              serverdata.add(personObj);
            }

            for(PersonDetails pers : serverdata)
            {

                plotMarker(pers.getLatitude(),pers.getLongitude(),pers.getUsername());
            }

        }
        else
        {
            Toast.makeText(getContext(),"Next 100 not in db",Toast.LENGTH_LONG).show();
            String url = urlGenerated +"&beforeid=" + minid;
            loadData(url);
        }


    }

    private void checkForDataConsistency(final int maxid, final String queryvalue, final String urlvalue)
    {
       getNextID(new nextID() {
           @Override
           public void getnextIdValue(int value) {
               int nextidval=value;
               int beforeid=maxid;
               int result= nextidval-beforeid;
               if(result > 1)
               {
                   //System.out.println("Data extra ide");
                   Toast.makeText(getContext(),"Inconsistent data",Toast.LENGTH_LONG).show();
                   loadInConsistentData(urlvalue,queryvalue,nextidval);
                   checkDatabase(queryvalue,urlvalue);
               }
               else
               {
                   Toast.makeText(getContext(),"Data consistent in both db and server..now querying",Toast.LENGTH_LONG).show();
                   getQueryResult(queryvalue,urlvalue);
               }

           }
       });

    }

    private void loadInConsistentData(String urlvalue, String queryvalue, int nextidval)
    {
        asyncData = new ArrayList<>();
        String url = "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&pagesize=100&beforeid="+nextidval;
        RequestQueue personInfo = VolleyHelper.getInstance().getRequestQueue();
        JsonArrayRequest dataServer = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++)
                {
                    databaseHelper= new DatabaseHelper(getContext());
                    mapPlotterObj = new PersonDetails();
                    try {
                        JSONObject responseData = response.getJSONObject(i);
                        longitudeValue = responseData.getDouble("longitude");
                        latitudeValue = responseData.getDouble("latitude");
                        mapPlotterObj.setUsername(responseData.getString("nickname"));
                        mapPlotterObj.setJoiningYear(responseData.getInt("year"));
                        mapPlotterObj.setIdForPerson(responseData.getInt("id"));
                        mapPlotterObj.setCountry(responseData.getString("country"));
                        mapPlotterObj.setState(responseData.getString("state"));
                        mapPlotterObj.setCity(responseData.getString("city"));
                        mapPlotterObj.setLatitude(latitudeValue);
                        mapPlotterObj.setLongitude(longitudeValue);
                        if (latitudeValue == 0.0 || longitudeValue == 0.0) {
                            //insert to thread list
                            //    asyncData.add(mapPlotterPersonObj);
                            asyncData.add(mapPlotterObj);
                        } else
                        {
                            databaseHelper.addPersonDetails(mapPlotterObj);


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new runAsync().execute(asyncData);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });personInfo.add(dataServer);
     return;
    }

   public void getQueryResult(String query,String urlvalue)
{
    ArrayList<PersonDetails>queryResult = new ArrayList<>();
    DatabaseHelper db = new DatabaseHelper(getContext());
    String newQuery = query + " limit '" + 100 + "'";
    System.out.println(newQuery);
    queryResult= db.getFilterData(newQuery);
    if(!queryResult.isEmpty())
    {
        Toast.makeText(getContext(),"In db data is present",Toast.LENGTH_LONG).show();
        for(PersonDetails personObj:queryResult)
        {
            System.out.println("db data present:" +personObj.getIdForPerson());
            serverdata.add(personObj);
        }

        for(PersonDetails personObj:serverdata)
        {
            System.out.println(personObj.getIdForPerson());
            plotMarker(personObj.getLatitude(),personObj.getLongitude(),personObj.getUsername());
        }
        btLoadData.setEnabled(true);
    }

    else
    {
//         urlvalue =urlvalue +"&beforeid=" + minid;
         loadData(urlvalue);
      //  int page=getPageNumber();
       // setPageNumber(page+1);
    }
}
    public void getNextID(final nextID obj)
    {
        RequestQueue nextIdRef= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/nextid";
        StringRequest getnextid= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int  temp=Integer.parseInt(response);
                obj.getnextIdValue(temp);

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }

        });nextIdRef.add(getnextid);
        return;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
     String username= marker.getTitle();
        checkForUserNames(username);

    }

    private void getFirebaseUsers()
    {

        DatabaseReference refObj= firebaseDatabase.getReference("users");
        refObj.addListenerForSingleValueEvent(new ValueEventListener()
        {
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                getFireBaseList(dataSnapshot.getChildren());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return;
    }

    private void getFireBaseList(Iterable<DataSnapshot> children)
    {
        for(DataSnapshot data:children)
        {
            PersonDetails person = data.getValue(PersonDetails.class);
            usersFromFirebase.add(person.getUsername());
        }

    }
    public void checkForUserNames(String clickedItem)
    {
        String currentUser=firebaseAuth.getCurrentUser().getDisplayName();
        String clickedUser=clickedItem;
        String uidForClickedUser="";

        if(currentUser.equals(clickedUser))
        {
            Toast.makeText(getContext(),"Cannot chat with yourself",Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(),"Cannot chat with yourself",Toast.LENGTH_LONG).show();
            ChatFragment chatFragment = new ChatFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft =fm.beginTransaction();
            ft.replace(R.id.fragHolderForChat,chatFragment);
            ft.commit();
        }


        else if(usersFromFirebase.contains(clickedUser))
        {
            Toast.makeText(getContext(),"User online",Toast.LENGTH_LONG).show();
            Bundle bdl = new Bundle();
            bdl.putString("clickeduser",clickedItem);
            bdl.putString("currentuser",currentUser);
            MessageFragment messageFrag= new MessageFragment();
            messageFrag.setArguments(bdl);
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragHolderForChat,messageFrag);
            ft.commit();

        }
        else
        {
            Toast.makeText(getContext(),"User offline",Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(),"Cannot chat with yourself",Toast.LENGTH_LONG).show();
            ChatFragment chatFragment = new ChatFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft =fm.beginTransaction();
            ft.replace(R.id.fragHolderForChat,chatFragment);
            ft.commit();
        }

    }


    interface nextID
    {
        void getnextIdValue(int value);
    }
    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber)
    {
        this.pageNumber = pageNumber;
    }




    private void loadData(String url)
    {
          serverdata.clear();
          System.out.println(url);
          asyncData= new ArrayList<>();
          RequestQueue personInfo = VolleyHelper.getInstance().getRequestQueue();
           JsonArrayRequest dataServer = new JsonArrayRequest(url, new Response.Listener<JSONArray>()
            {
                @Override
                public void onResponse(JSONArray response) {
                    for (int i = 0; i < response.length(); i++)
                    {
                        databaseHelper= new DatabaseHelper(getContext());
                        mapPlotterObj = new PersonDetails();
                        try {
                            JSONObject responseData = response.getJSONObject(i);
                            longitudeValue = responseData.getDouble("longitude");
                            latitudeValue = responseData.getDouble("latitude");
                            mapPlotterObj.setUsername(responseData.getString("nickname"));
                            mapPlotterObj.setJoiningYear(responseData.getInt("year"));
                            mapPlotterObj.setIdForPerson(responseData.getInt("id"));
                            mapPlotterObj.setCountry(responseData.getString("country"));
                            mapPlotterObj.setState(responseData.getString("state"));
                            mapPlotterObj.setCity(responseData.getString("city"));
                            mapPlotterObj.setLatitude(latitudeValue);
                            mapPlotterObj.setLongitude(longitudeValue);
                            if (latitudeValue == 0.0 || longitudeValue == 0.0)
                            {
                                //insert to thread list
                                  asyncData.add(mapPlotterObj);
                            }
                            else
                             {
                                databaseHelper.addPersonDetails(mapPlotterObj);
                                serverdata.add(mapPlotterObj);

                             }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new runAsync().execute(asyncData);

                    }

                    //new runAsync().execute(asyncData)
                   // btLoadData.setEnabled(true);
//                        pageNumber=pageNumber+1;
//                        generateUrl(etyear.getText().toString(), pageNumber);
//
                    if(serverdata.isEmpty())
                    {
                        Toast.makeText(getContext(),"No more data to display",Toast.LENGTH_LONG).show();
                        isClicked=false;
                        btLoadData.setEnabled(false);
                    }
                    else
                        {

                        for (PersonDetails person : serverdata) {
                            System.out.println("In load Data of server" + person.getIdForPerson());
                            plotMarker(person.getLatitude(), person.getLongitude(), person.getUsername());

                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        isClicked=false;
            personInfo.add(dataServer);
        }


    public void plotMarker(double lat,double lon,String title)
    {
       // System.out.println("In plotter");
        gmap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }
    public double[] calculatelatLng(String st,String count) throws IOException {
        Geocoder geocoder= new Geocoder(getContext());
        addressForMap= (ArrayList<Address>) geocoder.getFromLocationName(st +", "+count,1);
        for(Address resdata: addressForMap)
        {
            latitudeValue=resdata.getLatitude();
            longitudeValue=resdata.getLongitude();
        }
        return  new double[] {latitudeValue,longitudeValue};
    }

    public void getFilters(String countryValue, String stateValue, int yearValue)
    {
        localCountryFilterValue=countryValue;
        localStateFilterValue=stateValue;
        localyearFilterValue=yearValue;
    }
    public String generateUrlForLoadMore(String s1,String s2, int y)
    {
        String loadMoreUrl;
        if(s1==null && s2==null && y==0)
        {
         loadMoreUrl = " select * from USERDETAILS ";
        }
        else if(s1==null && s2==null && y!=0)
        {
            loadMoreUrl = " select * from USERDETAILS where" + " dateOfEntry " + " = '" + y + "'";
        }
        else if(s1!=null && s2==null &&y==0)
        {
            loadMoreUrl = "select * from USERDETAILS where" + " country " + " = '" + s1 + "'";
        }
        else if(s1!=null && s2!=null && y==0)
        {
            loadMoreUrl= " select * from USERDETAILS where" +  " country " + " = '" + s1 + "' and " + " state " + "= '" + s2 + "'";
        }
        else if(s1!=null && s2==null&& y!=0)
        {
            loadMoreUrl=" select * from USERDETAILS where " + " country " + " = '" + s1 + "' and " + " dateOfEntry " + " = '" + s2 +"'";
        }
        else
        {
            loadMoreUrl=" select * from USERDETAILS where" + " country " + " = '" + s1 + "' and " + " state " + " = '" + s2 + "' and " + " dateOfEntry " + " = '" + y + "'";
        }
        return loadMoreUrl;
    }
    public void getLocation(String s1,String s2) throws IOException
    {
        if(s1!=null &&s2==null)
        {
            Geocoder geocoder= new Geocoder(getContext());
            addressListForPlot= (ArrayList<Address>) geocoder.getFromLocationName(s1,1);
            for(Address resdata1: addressListForPlot)
            {
                latfromFilter=resdata1.getLatitude();
                lonFromFilter=resdata1.getLongitude();
                zoomValue=4.0f;
            }
        }
        else if(s1!=null && s2!=null)
        {
            Geocoder geocoder= new Geocoder(getContext());
            addressListForPlot= (ArrayList<Address>) geocoder.getFromLocationName(s2+","+s1,1);
            for(Address resdata1: addressListForPlot)
            {
                latfromFilter=resdata1.getLatitude();
                lonFromFilter=resdata1.getLongitude();
                zoomValue=6.0f;
            }
        }
        else
        {
            latfromFilter=0.0;
            lonFromFilter=0.0;
            zoomValue=1.0f;
        }

    }

    private class runAsync extends AsyncTask<ArrayList<PersonDetails>, PersonDetails, ArrayList<PersonDetails>> {



        @Override
        protected ArrayList<PersonDetails> doInBackground(ArrayList<PersonDetails>... params) {



            try{

                for(int i = 0; i < params[0].size(); i++){
                    PersonDetails obj = new PersonDetails();
                    obj = params[0].get(i);

                    String country = obj.getCountry();
                    String state = obj.getState();
                    double [] latLng = calculatelatLng(state, country);
                    obj.setLatitude(latLng[0]);
                    obj.setLongitude(latLng[1]);
                    publishProgress(obj);
                }


            }catch (Exception e){
                e.printStackTrace();
            }


            return  null;
        }

        @Override
        protected void onProgressUpdate(PersonDetails... values) {

            plotMarker(values[0].getLatitude(), values[0].getLongitude(), values[0].getUsername());

        }

        @Override
        protected void onPostExecute(ArrayList<PersonDetails> aVoid)
        {


        }
    }


}
