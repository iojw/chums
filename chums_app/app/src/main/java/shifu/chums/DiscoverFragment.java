package shifu.chums;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DiscoverFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_UID = "uid";
    private static final String ARG_PARAM2 = "param2";

    private String uid;
    private String mParam2;
    private Activity context;
    private ArrayList<ActivityTemplate> a;

    private OnFragmentInteractionListener mListener;

    public DiscoverFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DiscoverFragment newInstance(String uid) {
        DiscoverFragment fragment = new DiscoverFragment();
        Bundle args = new Bundle();
        args.putString(ARG_UID, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh(){
        HashMap<String, String> hm = new HashMap<>(1);
        hm.put("uid", uid);
        CallAPI getList = new CallAPI(list_c, "http://chums-iojw.rhcloud.com/list", hm);
        getList.execute();
    }

    final Callback going_c = new Callback() {
        @Override
        public void onResult(String result) {
            refresh();
        }
    };

    final Callback list_c = new Callback() {
        @Override
        public void onResult(String result) {
            if (result==null){
                Toast.makeText(context,"Error retrieving data",Toast.LENGTH_LONG);
            }
            else {
                a = new ArrayList<ActivityTemplate>();
                try {
                    JSONArray ja = new JSONArray(result);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject o = ja.getJSONObject(i);
                        int going = o.getJSONArray("going").length();
                        //todo:add desc
                        ActivityTemplate activity = new ActivityTemplate(o.getString("title"),
                                o.getString("time"), o.getString("venue"), "",o.getString("creator_name"),o.getString("creator"),o.getString("_id"),going);
                        if (activity.creator.equals(uid)==false && o.getBoolean("is_going")==false) {
                            a.add(activity);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON", e.toString());
                }
                AAdapter adapter = new AAdapter(context, a);
                ListView activitiesList = (ListView) getActivity().findViewById(R.id.activitiesList);
                activitiesList.setAdapter(adapter);
                if (a.isEmpty()){
                    a.add(new ActivityTemplate("No activities currently", "", "", "", "", "", "", -1));
                }
                else{
                    activitiesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final int index=position;
                            new AlertDialog.Builder(context)
                                    .setTitle("Attending?")
                                    .setMessage("Attend activity with "+a.get(index).creator_name+"?")
                                    .setIcon(R.drawable.ic_group_black)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            HashMap<String, String> hm = new HashMap<>(2);
                                            Log.d("chums", a.get(index).obj_id);
                                            hm.put("uid", uid);
                                            hm.put("obj_id", a.get(index).obj_id);
                                            CallAPI setGoing = new CallAPI(going_c, "http://chums-iojw.rhcloud.com/going", hm);
                                            setGoing.execute();
                                        }})
                                    .setNegativeButton(android.R.string.no, null).show();

                        }
                    });
                }
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_UID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisible){
        super.setUserVisibleHint(isVisible);

        if (isVisible) {
            refresh();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
