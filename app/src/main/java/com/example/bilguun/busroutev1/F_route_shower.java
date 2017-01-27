package com.example.bilguun.busroutev1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_route_shower.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_route_shower#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_route_shower extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public F_route_shower() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment F_route_shower.
     */
    public static F_route_shower newInstance(String param1, String param2) {
        F_route_shower fragment = new F_route_shower();
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
        return inflater.inflate(R.layout.fragment_route_shower, container, false);
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

    LA_route_shower adapter;
    TextView routeID,duration,ctime,ntake;
    @Override
    public void onStart() {
        super.onStart();
        adapter=new LA_route_shower(getActivity(),((MainActivity)getActivity()).route_finder());
        ListView lv=(ListView)getView().findViewById(R.id.shower_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity)getActivity()).details=adapter.objects.elementAt(position);
                ((MainActivity)getActivity()).change_fragment("Details");
            }
        });

        ((MainActivity)getActivity()).cleaner();

        routeID=(TextView)getView().findViewById(R.id.shower_routeID);
        routeID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeID.setBackgroundColor(0xffc0c0c0);
                duration.setBackgroundColor(0xffffffff);
                ctime.setBackgroundColor(0xffffffff);
                ntake.setBackgroundColor(0xffffffff);
                adapter_sort(1);
                adapter.notifyDataSetChanged();
            }
        });

        duration=(TextView)getView().findViewById(R.id.shower_duration);
        duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeID.setBackgroundColor(0xffffffff);
                duration.setBackgroundColor(0xffc0c0c0);
                ctime.setBackgroundColor(0xffffffff);
                ntake.setBackgroundColor(0xffffffff);
                adapter_sort(2);
                adapter.notifyDataSetChanged();
            }
        });

        ctime=(TextView)getView().findViewById(R.id.shower_ctime);
        ctime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeID.setBackgroundColor(0xffffffff);
                duration.setBackgroundColor(0xffffffff);
                ctime.setBackgroundColor(0xffc0c0c0);
                ntake.setBackgroundColor(0xffffffff);
                adapter_sort(3);
                adapter.notifyDataSetChanged();
            }
        });

        ntake=(TextView)getView().findViewById(R.id.shower_ntake);
        ntake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                routeID.setBackgroundColor(0xffffffff);
                duration.setBackgroundColor(0xffffffff);
                ctime.setBackgroundColor(0xffffffff);
                ntake.setBackgroundColor(0xffc0c0c0);
                adapter_sort(4);
                adapter.notifyDataSetChanged();
            }
        });
    }

    void adapter_sort(int n){
        Vector<LO_route_shower> target=adapter.objects;
        switch (n){
            case 1:
                //Sort Names
                Collections.sort(target,LO_route_shower.bynames);
                break;
            case 2:
                //Sort Duration
                Collections.sort(target,LO_route_shower.byduration);
                break;
            case 3:
                //Sort Ctime
                Collections.sort(target,LO_route_shower.byctime);
                break;
            case 4:
                //Sort Ntake
                Collections.sort(target,LO_route_shower.byntake);
                break;
        }
        adapter.objects=target;
        adapter.notifyDataSetChanged();
    }
}
