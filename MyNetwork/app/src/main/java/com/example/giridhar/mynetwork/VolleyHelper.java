package com.example.giridhar.mynetwork;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by giridhar on 4/2/17.
 */

public class VolleyHelper
{
    private static VolleyHelper volleyObj=null;
    private RequestQueue requestQueue;
    private VolleyHelper()
    {
        requestQueue= Volley.newRequestQueue(ApplicationContextHelper.getAppContext());
    }
    public static VolleyHelper getInstance()
    {
        if(volleyObj==null)
        {
            volleyObj=new VolleyHelper();
        }
        return volleyObj;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
