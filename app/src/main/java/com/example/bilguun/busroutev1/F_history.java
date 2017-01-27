package com.example.bilguun.busroutev1;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Vector;

import static android.R.attr.path;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_history.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_history#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_history extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public F_history() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment F_history.
     */
    public static F_history newInstance(String param1, String param2) {
        F_history fragment = new F_history();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    LA_history adapter;

    @Override
    public void onStart() {
        adapter_init();
        ListView lv=(ListView)getView().findViewById(R.id.history_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                D_history tmp=adapter.objects.elementAt(position);
                ((MainActivity)getActivity()).Astop=tmp.A;
                ((MainActivity)getActivity()).Bstop=tmp.B;
                ((MainActivity)getActivity()).change_fragment("Route_Shower");
            }
        });
        super.onStart();
    }

    void adapter_init(){
        Vector<D_history> tmpt=new Vector<>();
        String jsonStr="";
        try {
            InputStream is=getActivity().openFileInput("History");
            jsonStr= IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONArray jsonArray=jsonObject.getJSONArray("history");
            for(int h=0;h<jsonArray.length();h++){
                JSONObject c=jsonArray.getJSONObject(h);
                D_history tmp=new D_history(c.getInt("A"),c.getString("Aname"),c.getInt("B"),c.getString("Bname"),c.getString("Time"));
                tmpt.addElement(tmp);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter=new LA_history(getActivity(),tmpt);
    }
}
