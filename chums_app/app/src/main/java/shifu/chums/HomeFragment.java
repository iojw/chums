package shifu.chums;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String ARG_PARAM1 = "uid";

//    private String mParam1;

    private OnFragmentInteractionListener mListener;
    private Activity activity;
    private String uid;
    private ArrayList<ActivityTemplate> myList;
    private ArrayList<ActivityTemplate> goingList;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String uid) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, uid);
        fragment.setArguments(args);
        return fragment;
    }

    public void refresh(){
        HashMap<String, String> hm = new HashMap<>(1);
        hm.put("uid", uid);
        CallAPI getList = new CallAPI(c,"http://chums-iojw.rhcloud.com/list",hm);
        getList.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
        if (getArguments() != null) {
            uid = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisible){
        super.setUserVisibleHint(isVisible);

        if (isVisible) {
            refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
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

    final Callback c = new Callback() {
        @Override
        public void onResult(String result) {
            if (result==null){
                Toast.makeText(activity,"Error retrieving data",Toast.LENGTH_LONG);
            }
            else {
                myList = new ArrayList<ActivityTemplate>();
                goingList = new ArrayList<ActivityTemplate>();
                try {
                    JSONArray ja = new JSONArray(result);
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject o = ja.getJSONObject(i);
                        int going = o.getJSONArray("going").length();
                        //todo:add desc
                        ActivityTemplate activity = new ActivityTemplate(o.getString("title"),
                                o.getString("time"), o.getString("venue"), "", o.getString("creator_name"), o.getString("creator"), o.getString("_id"), going);
                        if (activity.creator.equals(uid)){
                            activity.isCreator();
                            myList.add(activity);
                        }
                        else if (o.getBoolean("is_going") == true)
                            goingList.add(activity);
                    }
                } catch (JSONException e) {
                    Log.e("JSON", e.toString());
                }
                TextView attendingText = (TextView) activity.findViewById(R.id.attendingText);

                final ListView goingListView = (ListView) getActivity().findViewById(R.id.goingList);
                if (goingList.isEmpty()) {
                    goingList.add(new ActivityTemplate("You are not attending any activities", "", "", "", "", "", "", -1));

                }
                else{
                    goingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final int index = position;
                            new AlertDialog.Builder(activity)
                                    .setTitle("Not attending?")
                                    .setMessage("Can't attend activity with " + goingList.get(index).creator_name + "?")
                                    .setIcon(R.drawable.ic_clear_black)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            HashMap<String, String> hm = new HashMap<>(1);
                                            hm.put("obj_id", goingList.get(index).obj_id);
                                            hm.put("uid", uid);
                                            CallAPI notGoing = new CallAPI(notgoing_c, "http://chums-iojw.rhcloud.com/notgoing", hm);
                                            notGoing.execute();
                                            goingListView.setAdapter(null);
                                        }
                                    })
                                    .setNegativeButton("Nope, still attending!", null).show();
                        }
                    });
                }
                AAdapter goingAdapter = new AAdapter(getActivity(), goingList);
                goingListView.setAdapter(goingAdapter);


                AAdapter myActAdapter = new AAdapter(getActivity(),myList);
                final ListView myActsList = (ListView) getActivity().findViewById(R.id.myActsList);
                if (myList.isEmpty()){
                    myList.add(new ActivityTemplate("You don't have any activities","","","","","","",-1));
                }
                else{
                    myActsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            final int index = position;
                            new AlertDialog.Builder(activity)
                                    .setTitle("Remove activity?")
                                    .setMessage("Remove this activity?")
                                    .setIcon(R.drawable.ic_clear_black)
                                    .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            HashMap<String, String> hm = new HashMap<>(1);
                                            hm.put("obj_id", myList.get(index).obj_id);
                                            CallAPI remove = new CallAPI(remove_c, "http://chums-iojw.rhcloud.com/remove", hm);
                                            myActsList.setAdapter(null);
                                            remove.execute();
                                        }
                                    })
                                    .setNegativeButton("Nope, still on!", null).show();
                        }
                    });
                }
                myActsList.setAdapter(myActAdapter);
            }

        }
    };

    final Callback notgoing_c = new Callback() {
        @Override
        public void onResult(String result) {
            refresh();
            Toast.makeText(activity,"Not attending.",Toast.LENGTH_LONG).show();
        }
    };

    final Callback remove_c = new Callback() {
        @Override
        public void onResult(String result) {
            refresh();
            Toast.makeText(activity,"Removed.",Toast.LENGTH_LONG).show();
        }
    };
}
