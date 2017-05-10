package com.example.giridhar.mynetwork;


import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ListViewFragment extends Fragment implements View.OnClickListener,AdapterView.OnItemSelectedListener,AbsListView.OnScrollListener,AdapterView.OnItemClickListener{
    View  v;
    Spinner spCountry,spState;
    EditText etYear;
    Button btfilter,btclear;
    ArrayList<String> countryListArray;
    ArrayList<String> stateListArray;
    String countryChosen,stateChosen;
    ListView userList;
    PersonDetails personData;
    DatabaseHelper db;
    ArrayList<PersonDetails> dataList;
    ArrayList<String>userData= new ArrayList<>();
    ArrayAdapter<String> userAdapter;
    int lastIdref=0;
    int lastClickedIdForFilter=0;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<String> usersFromFirebase = new ArrayList<>();
    ArrayList<Integer> listData = new ArrayList<>();
    DatabaseReference refobj;
    boolean hasScrolled=false;
    boolean isClickedFilter=false;
    String localCountryFilterValue,localStateFilterValue;
    int localyearFilterValue;
    DatabaseHelper databaseHelper;
    PersonDetails personObj;
    ArrayList<PersonDetails>forServerData=new ArrayList<>();


    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        v=inflater.inflate(R.layout.fragment_list_view, container, false);
        db= new DatabaseHelper(getContext());
        spCountry=(Spinner)v.findViewById(R.id.spinner3);
        spState=(Spinner)v.findViewById(R.id.spinner4);
        etYear=(EditText)v.findViewById(R.id.editText8);
        btfilter=(Button)v.findViewById(R.id.button4);
        btclear=(Button)v.findViewById(R.id.button5);
        userList= (ListView) v.findViewById(R.id.list);
        userAdapter= new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,userData);
        userList.setAdapter(userAdapter);
        userList.setOnItemClickListener(this);
        userList.setOnScrollListener(this);
        btfilter.setOnClickListener(this);
        btclear.setOnClickListener(this);
        spCountry.setOnItemSelectedListener(this);
        spState.setOnItemSelectedListener(this);
        loadCountryList();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        refobj=firebaseDatabase.getReference("users");
        getFirebaseUsers();
        //System.out.println(firebaseAuth.getCurrentUser().getDisplayName());
        checkDataInDatabase();
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //retrieveValue();

    }
    @Override
    public void onClick(View v) {
        switch ((v.getId()))
        {
            case R.id.button4:
                      //  loadListView();
                if(!userData.isEmpty())
                {
                    lastClickedIdForFilter=0;
                    userData.clear();
                    //userAdapter.notifyDataSetChanged();
                    isClickedFilter=true;
                    generateUrl(etYear.getText().toString());
                }
                else
                {
                    lastClickedIdForFilter=0;
                    userData.clear();
                   // userAdapter.notifyDataSetChanged();
                    isClickedFilter=true;
                    generateUrl(etYear.getText().toString());
                }
                         break;


            case R.id.button5:
                         clearFilterValues();
                         isClickedFilter=false;
                break;
          }
    }

    private void clearFilterValues()
    {
        countryChosen="Select";
        stateChosen= null;
        spState.setSelection(0);
        spCountry.setSelection(0);
        stateListArray.clear();
        //String yearvalue= etYear.getText().toString();
        etYear.setText("");
        userData.clear();
        generateUrl(etYear.getText().toString());
       // userAdapter.notifyDataSetChanged();
//        generateUrl(etYear.getText().toString());

    }

    public void loadCountryList()
    {
        countryListArray= new ArrayList<>();
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
           case R.id.spinner3:
                       countryChosen=parent.getItemAtPosition(position).toString();
                       loadStateList(countryChosen);
                        break;

           case R.id.spinner4:
                      stateChosen=parent.getItemAtPosition(position).toString();
                      break;
       }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {

    }


    public void loadStateList(String countryName)
    {
        stateListArray = new ArrayList<>();
        stateListArray.add("Select state");
        RequestQueue stateListFromServer= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/states?country=" + countryName;
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

     public void getNextID(final nextIDValue obj)
     {
         RequestQueue nextIdRef= VolleyHelper.getInstance().getRequestQueue();
         String url="http://bismarck.sdsu.edu/hometown/nextid";
         final StringRequest getnextid= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String response) {
              int  temp=Integer.parseInt(response);
                 obj.nextIdValue(temp);

             }


         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {

             }

         });nextIdRef.add(getnextid);
         return;
     }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            hasScrolled = false;
        } else if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING ||
                scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
            hasScrolled = true;

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if(hasScrolled)
        {
           if (totalItemCount < (firstVisibleItem + visibleItemCount + 1))
           {
                if(isClickedFilter)
                {
                  lastClickedIdForFilter=totalItemCount;
                  generateUrl(etYear.getText().toString());
                    //System.out.println("First Visible:" +firstVisibleItem + "Visible Count" + visibleItemCount +"Total item count" + totalItemCount);
                }
                else {
                    lastIdref = totalItemCount;
                    Toast.makeText(getContext(), "End of list reached! Loading 100 more data", Toast.LENGTH_LONG).show();
                    checkDataInDatabase();
                }
                    // getTheValue(lastIdref);
           }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String s = parent.getItemAtPosition(position).toString();
//        String result= s.substring(15);
//        System.out.println(result);
        String[] finalres = s.split("\n");
        String [] clickedArray = finalres[0].split("\\|");
        System.out.println(clickedArray[0]);
        String clickedItem =clickedArray[1].substring(15).trim();
           //usersFromFirebase.clear();
        checkForUserNames(clickedItem);

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
//        for(String s:usersFromFirebase)
//        {
//            System.out.println("In getfirebase" + s);
//        }


    }
    public void checkForUserNames(String clickedItem)
    {
        String currentUser=firebaseAuth.getCurrentUser().getDisplayName();
        String clickedUser=clickedItem;
        String uidForClickedUser="";

        if(currentUser.equals(clickedUser))
        {
            Toast.makeText(getContext(),"Cannot chat with yourself",Toast.LENGTH_LONG).show();
            ChatFragment chatFragment = new ChatFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft =fm.beginTransaction();
            ft.replace(R.id.fragHolderForChat,chatFragment);
            ft.commit();
        }


        else if(usersFromFirebase.contains(clickedUser))
        {
           // getChat(currentUser,clickedUser);
            System.out.println(refobj.child(clickedUser).getKey());
            Toast.makeText(getContext(),"User online",Toast.LENGTH_LONG).show();
            //String resultKey=  createUniqueKeyForChat(currentUser,clickedUser);
           // System.out.println("Chat key" + resultKey);
            Bundle bdl = new Bundle();
            bdl.putString("clickeduser",clickedItem);
            bdl.putString("currentuser",currentUser);
           // bdl.putString("chatkey",resultKey);
//            refobj.child(currentUser).child(resultKey).setValue(resultKey);
//            refobj.child(clickedUser).child(resultKey).setValue(resultKey);
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
            ChatFragment chatFragment = new ChatFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft =fm.beginTransaction();
            ft.replace(R.id.fragHolderForChat,chatFragment);
            ft.commit();
        }

    }


    public interface nextIDValue
    {
        void nextIdValue(int value);
    }

    public void genericLoaderForListView(final int nextidvalue)
    {

        //System.out.println(nextidvalue);
        final int afteridvalue=nextidvalue-100;
     //   System.out.println(afteridvalue + " "+ nextidvalue);
        db= new DatabaseHelper(getContext());
        RequestQueue personInfoQueue= VolleyHelper.getInstance().getRequestQueue();
        String url="http://bismarck.sdsu.edu/hometown/users?afterid="+afteridvalue+"&beforeid="+nextidvalue;
        System.out.println("In genric loader" + url);
        JsonArrayRequest dataFromServer= new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response)
            {
                dataList= new ArrayList<>();
                personData= new PersonDetails();
                for(int i=0;i<response.length();i++)
                {
                    try
                    {
                        JSONObject responseData= response.getJSONObject(i);
                            double latitudeValue=responseData.getDouble("latitude");
                            double longitudeValue=responseData.getDouble("longitude");
                                personData.setUsername(responseData.getString("nickname"));
                                personData.setJoiningYear(responseData.getInt("year"));
                                personData.setCountry(responseData.getString("country"));
                                personData.setState(responseData.getString("state"));
                                personData.setCity(responseData.getString("city"));
                                personData.setIdForPerson(responseData.getInt("id"));
                                personData.setLatitude(latitudeValue);
                                personData.setLongitude(longitudeValue);
                                db.addPersonDetails(personData);
                                //dataList.add(personData);
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                dataList.clear();
                //userData.clear();
                dataList=db.getLimitData(afteridvalue,nextidvalue);

                for(PersonDetails persObj:dataList)
                {

                  userData.add(persObj.getIdForPerson()+"| Nickname" + "   :   "+persObj.getUsername() + "\nYear Of Joining"+ "  :  " +persObj.getJoiningYear() + "\nCountry" + "  :  "+persObj.getCountry() +"\nState" + "  :  "+persObj.getState()
                            + "\nCity" +" :  "+ persObj.getCity());
  //                  userData.add(" ID " + persObj.getIdForPerson());
                }

               userAdapter.notifyDataSetChanged();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });personInfoQueue.add(dataFromServer);
        db.close();
    }

    public void checkDataInDatabase()
    {
        db = new DatabaseHelper(getContext());
        ArrayList<PersonDetails>getData= new ArrayList<>();
        getData=db.getPersonDetails();
        if(!getData.isEmpty())
        {
          ArrayList<Integer> idOfUsers= new ArrayList<>();
          for(PersonDetails each: getData)
          {
              idOfUsers.add(each.getIdForPerson());
          }
              if(lastIdref==0)           //oncreate
              {

                int id= getData.get(0).getIdForPerson();
                checkForNewNextid(id);

              }
              else          //onscroll
              {
                  System.out.println("Inside scroll  new part");
                  DatabaseHelper dbObj= new DatabaseHelper(getContext());
                  ArrayList<PersonDetails> limitedDataFromDB= new ArrayList<>();
                  String item="";
                  String [] tempafterid;
                    item = userData.get(userAdapter.getCount()-1);
                    tempafterid= item.split("\\|");
                       //item  = lastIdref - 1;
                       //nextid= idOfUsers.get(item);
                       //tempafterid = nextid -100;
                       //System.out.println("Error part" + nextid + " " + tempafterid);
                   int  nextidval = Integer.parseInt(tempafterid[0]);
                   int beforeidval =nextidval-100;
                       limitedDataFromDB=dbObj.getLimitData(beforeidval,nextidval);
                  if(!limitedDataFromDB.isEmpty())
                  {
                     for(PersonDetails personDetails:limitedDataFromDB)
                     {
                        dbObj.addPersonDetails(personDetails);
                         userData.add(personDetails.getIdForPerson()+"| Nickname" + "   :   "+personDetails.getUsername() +  "\nYear Of Joining"+ "  :  " +personDetails.getJoiningYear() + "\nCountry" + "  :  "+personDetails.getCountry() +"\nState" + "  :  "+personDetails.getState()
                                 + "\nCity" +" :  "+ personDetails.getCity());
                        // userData.add("ID " + personDetails.getIdForPerson());
                         userAdapter.notifyDataSetChanged();
                     }
                  }
                  else
                  {
                     genericLoaderForListView(nextidval);
                  }
                  dbObj.close();
              }
        }
        else
        {
            //Toast.makeText(getContext(),"No data in database",Toast.LENGTH_LONG).show();
            getNextID(new nextIDValue()
            {
                @Override
                public void nextIdValue(int value)
                {
                    int nextIDref=value;
                    genericLoaderForListView(nextIDref);
                }
            });
        }
  //  db.close();
    }

    public void checkForNewNextid(final int currentValueForNext)
 {
       // System.out.println("Inside checkfornew");
       getNextID(new nextIDValue()
       {
           @Override
           public void nextIdValue(int value)
           {
               int afterid=currentValueForNext;
               int next = value;
               int res = next-afterid;
               if(res > 1)
               {
                   genericLoaderForListView(next);
               }
               else
               {
                   Toast.makeText(getContext(),"No updated data found",Toast.LENGTH_LONG).show();
                   int afterTemp=currentValueForNext-100;
                   //int afterTemp=next-100;
                   db= new DatabaseHelper(getContext());
                   ArrayList<PersonDetails> userDataExisting= new ArrayList<PersonDetails>();
                   userDataExisting=db.getLimitData(afterTemp,currentValueForNext);
                   for(PersonDetails personDetails:userDataExisting)
                   {
                       db.addPersonDetails(personDetails);
                       userData.add(personDetails.getIdForPerson()+"| Nickname" + "   :   "+ personDetails.getUsername() + "\nYear Of Joining"+ "  :  " +personDetails.getJoiningYear() + "\nCountry" + "  :  "+ personDetails.getCountry() +"\nState" + "  :  "+ personDetails.getState()
                               + "\nCity" +" :  "+ personDetails.getCity());
                       userAdapter.notifyDataSetChanged();
                   }
                   db.close();
               }

           }
       });
 }

 public String createUniqueKeyForChat(String current, String clicked)
 {
      String chatKey="";
      int resvalue= current.compareToIgnoreCase(clicked);
     if(resvalue < 0)
     {
       chatKey = current + clicked;
     }
     else
     {
         chatKey = current + clicked;
     }
    return chatKey;
 }

 public void getChat(String s1, String s2)
 {
     FirebaseDatabase fb = FirebaseDatabase.getInstance();
     DatabaseReference dbref= fb.getReference("listOfChats");
     String keyToCheck = createUniqueKeyForChat(s1,s2);
     System.out.println(keyToCheck);
     dbref.child(keyToCheck).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(DataSnapshot dataSnapshot) {
             getMessageList(dataSnapshot.getChildren());
         }

         @Override
         public void onCancelled(DatabaseError databaseError) {

         }
     });
 }

    private void getMessageList(Iterable<DataSnapshot> children)
    {
        ArrayList<String > messagesListFromFire = new ArrayList<>();
        for(DataSnapshot data:children)
        {
            System.out.println(data.getValue());
            //MessageHelper messageHelper = data.getValue(MessageHelper.class);
            // messagesListFromFire.add(messageHelper.getSenderName() + messageHelper.getMessage());
            //System.out.println(messageHelper.getSenderName() + " " + messageHelper.getSenderName());
        }
    }

    public void checkForDataInDataBase(String queryvalue, String urlvalue)
    {
      //  System.out.println("Inside checkforDatabase");
        String userItem="";
        String [] arr;
        String queriedargument=queryvalue;
        final String urlGenerated =urlvalue;
        ArrayList<PersonDetails>listOfUserNames = new ArrayList<>();
        DatabaseHelper db =new DatabaseHelper(getContext());
        listOfUserNames=db.getPersonDetails();
        if(!listOfUserNames.isEmpty())
        {
        //    ArrayList<Integer> idOfUsers= new ArrayList<>();
          //  for(PersonDetails each: listOfUserNames)
           // {
          //      idOfUsers.add(each.getIdForPerson());
         //   }
            if(lastClickedIdForFilter==0)
            {
               // int maxid= listOfUserNames.get(0).getIdForPerson();
              //  checkForDataConsistency(maxid,queriedargument,urlGenerated);
                getQueryResult(queryvalue,urlvalue);
            }
            else
            {
                //int item = lastClickedIdForFilter-1;
                try {
                    userItem = userData.get(userAdapter.getCount()-1);
                    arr= userItem.split("\\|");
                    loadMoreData(urlGenerated,queriedargument, Integer.parseInt(arr[0]));
                }
                catch (Exception e) {

                }
            }
        }
        else
        {
            Toast.makeText(getContext(),"Requested Data not found..Contacting server",Toast.LENGTH_LONG).show();
            getNextID(new nextIDValue()
            {
                @Override
                public void nextIdValue(int value) {
                    genericLoaderForListView(value);

                }
            });

        }
      //  db.close();

    }
    private void checkForDataConsistency(final int maxid, final String queryvalue, final String urlvalue)
    {
      getNextID(new nextIDValue() {
          @Override
          public void nextIdValue(int value)
          {
              int nextid= value;
              int beforeid=maxid;
              int result = nextid-beforeid;
              if(result > 1 )
              {
                  Toast.makeText(getContext(),"Incosistent data..fetching to normalise",Toast.LENGTH_LONG).show();
                  loadInConsistentData(urlvalue,queryvalue,nextid);
                  checkForDataInDataBase(queryvalue,urlvalue);
              }
              else
              {
                  //write query
                  Toast.makeText(getContext(),"Data consistent in both db and server..now querying",Toast.LENGTH_LONG).show();
                  getQueryResult(queryvalue,urlvalue);
              }
          }
      });

    }

    private void getQueryResult(String queryvalue, String urlvalue)
    {
        System.out.println("Inside get query");
        ArrayList<PersonDetails>queryResult = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(getContext());
        String newQuery = queryvalue + " order by " + " id " + " desc " + " limit '" + 100 + "'";
        System.out.println(newQuery);
        queryResult= db.getFilterData(newQuery);
        if(!queryResult.isEmpty())
        {
            Toast.makeText(getContext(),"Data required present in db",Toast.LENGTH_LONG);
            for(PersonDetails persObj:queryResult)
            {
                System.out.println("In get query " + persObj.getIdForPerson());
                userData.add(persObj.getIdForPerson()+"| Nickname" + "   :   "+ persObj.getUsername() + "\nYear Of Joining"+ "  :  " +persObj.getJoiningYear() + "\nCountry" + "  :  "+ persObj.getCountry() +"\nState" + "  :  "+ persObj.getState()
                        + "\nCity" +" :  "+ persObj.getCity());
            }
            userAdapter.notifyDataSetChanged();
        }
        else
        {
            loadDataFromServer(urlvalue);
        }
    }

    public void loadMoreData(String urlval,String queryval, int nextid)
    {
        ArrayList<PersonDetails> dataInDb = new ArrayList<>();
        DatabaseHelper db =new DatabaseHelper(getContext());
        queryval = queryval + " and " + " id " + " < '" + nextid + "' order by " + " id " + " desc " + " limit '" + 100 + "'";
        System.out.println(queryval);
        dataInDb = db.getFilterData(queryval);
        if(!dataInDb.isEmpty())
        {
            Toast.makeText(getContext(),"Next 100 data in db!!",Toast.LENGTH_LONG).show();
            for(PersonDetails personObj: dataInDb)
            {
                System.out.println("in load more" + personObj.getIdForPerson());
                userData.add(personObj.getIdForPerson()+"| Nickname" + "   :   "+ personObj.getUsername() + "\nYear Of Joining"+ "  :  " +personObj.getJoiningYear() + "\nCountry" + "  :  "+ personObj.getCountry() +"\nState" + "  :  "+ personObj.getState()
                        + "\nCity" +" :  "+ personObj.getCity());
            }
            userAdapter.notifyDataSetChanged();

        }
        else
        {
           // Toast.makeText(getContext(),"Next 100 not in db",Toast.LENGTH_LONG).show();
            String url = urlval +"&beforeid=" + nextid;
            loadDataFromServer(url);
        }
        db.close();
    }

    private void loadDataFromServer(String urlvalue)
    {
       if(!forServerData.isEmpty())
       {
           forServerData.clear();
       }
       else
       {
           RequestQueue personInfo = VolleyHelper.getInstance().getRequestQueue();
           JsonArrayRequest dataServer = new JsonArrayRequest(urlvalue, new Response.Listener<JSONArray>()
           {
               @Override
               public void onResponse(JSONArray response) {
                   for (int i = 0; i < response.length(); i++)
                   {
                       databaseHelper= new DatabaseHelper(getContext());
                       personObj = new PersonDetails();
                       try {
                           JSONObject responseData = response.getJSONObject(i);
                           personObj.setUsername(responseData.getString("nickname"));
                           personObj.setJoiningYear(responseData.getInt("year"));
                           personObj.setIdForPerson(responseData.getInt("id"));
                           personObj.setCountry(responseData.getString("country"));
                           personObj.setState(responseData.getString("state"));
                           personObj.setCity(responseData.getString("city"));
                           personObj.setLatitude(responseData.getDouble("latitude"));
                           personObj.setLongitude(responseData.getDouble("longitude"));
                           databaseHelper.addPersonDetails(personObj);
                           forServerData.add(personObj);

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }

                   }
                   if(forServerData.isEmpty())
                   {
                       //Toast.makeText(getContext(),"No more data to display",Toast.LENGTH_LONG).show();
                   }
                   else
                   {

                       for (PersonDetails person : forServerData)
                       {
                        //   System.out.println("In load Data of server" + person.getIdForPerson());
                           userData.add(person.getIdForPerson()+"| Nickname" + "   :   "+ person.getUsername() + "\nYear Of Joining"+ "  :  " +person.getJoiningYear() + "\nCountry" + "  :  "+ person.getCountry() +"\nState" + "  :  "+ person.getState()
                                   + "\nCity" +" :  "+ person.getCity());

                       }
                       userAdapter.notifyDataSetChanged();
                   }
               }
           }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {

               }
           });personInfo.add(dataServer);
       }
//databaseHelper.close();
    }
    private void loadInConsistentData(String urlvalue, String queryvalue, int nextidval)
    {
        String url = "http://bismarck.sdsu.edu/hometown/users?page=0&reverse=true&pagesize=100&beforeid="+nextidval;
        //System.out.println(url);
        RequestQueue personInfo = VolleyHelper.getInstance().getRequestQueue();
        JsonArrayRequest dataServer = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++)
                {
                    databaseHelper= new DatabaseHelper(getContext());
                    personObj = new PersonDetails();
                    try {
                        JSONObject responseData = response.getJSONObject(i);

                        personObj.setUsername(responseData.getString("nickname"));
                        personObj.setJoiningYear(responseData.getInt("year"));
                        personObj.setIdForPerson(responseData.getInt("id"));
                        personObj.setCountry(responseData.getString("country"));
                        personObj.setState(responseData.getString("state"));
                        personObj.setCity(responseData.getString("city"));
                        personObj.setLatitude(responseData.getDouble("latitude"));
                        personObj.setLongitude(responseData.getDouble("longitude"));
                        databaseHelper.addPersonDetails(personObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                databaseHelper.close();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });personInfo.add(dataServer);

        return;
    }


    public void generateUrl(String yearvalue)
    {
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
            String urlValue="http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true ";
            String query= " select * from USERDETAILS ";
            //System.out.println(query + "  " + countryFilter + "  "+ stateFilter + "  " + yearFilter);
            // getFilters(null,null,0);
            checkForDataInDataBase(query,urlValue);
        }
        else if(countryFilter.contains("Select")&&(stateFilter==null) && yearFilter!=0 )
        {
            String urlValue="http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true"+"?year="+yearFilter;
            String query= " select * from USERDETAILS where" + " dateOfEntry " + " = '" + yearFilter + "'";
            //getFilters(null,null,yearFilter);
            checkForDataInDataBase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && stateFilter.contains("Select") && yearFilter==0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter;
            String query="select * from USERDETAILS where" + " country " + " = '" + countryFilter + "'";
           // getFilters(countryFilter,null,0);
            checkForDataInDataBase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && stateFilter.contains("Select") && yearFilter!=0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true"+"&country ="+countryFilter+"&year="+yearFilter;
            String query=" select * from USERDETAILS where "+ " country " + " = '" + countryFilter + "' and " + " dateOfEntry " + " = '" + yearFilter + "'";
         //   getFilters(countryFilter,null,yearFilter);
            checkForDataInDataBase(query,urlValue);
        }
        else if(!countryFilter.contains("Select") && !stateFilter.contains("Select") && yearFilter ==0)
        {
            String urlValue= "http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter+"&state="+stateFilter;
            String query=" select * from USERDETAILS where" +  " country " + " = '" + countryFilter + "' and " + " state " + "= '" + stateFilter + "'";
            //getFilters(countryFilter,stateFilter,0);
            checkForDataInDataBase(query,urlValue);
        }
        else
        {
            String  urlValue= "http://bismarck.sdsu.edu/hometown/users?page=0"+"&pagesize=100"+"&reverse=true"+"&country="+countryFilter+"&state="+stateFilter+"&year="+yearFilter;
//            checkDatabase(countryFilter,stateFilter,yearFilter,urlValue);
            String query = " select * from USERDETAILS where" + " country " + " = '" + countryFilter + "' and " + " state " + " = '" + stateFilter + "' and " + " dateOfEntry " + " = '" + yearFilter + "'";
          //  getFilters(countryFilter,stateFilter,yearFilter);
            checkForDataInDataBase(query,urlValue);
        }
    }
//    public void getFilters(String countryValue, String stateValue, int yearValue)
//    {
//        localCountryFilterValue=countryValue;
//        localStateFilterValue=stateValue;
//        localyearFilterValue=yearValue;
//    }


}

