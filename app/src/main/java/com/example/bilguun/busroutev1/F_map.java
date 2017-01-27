package com.example.bilguun.busroutev1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_map extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public F_map() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment F_map.
     */
    public static F_map newInstance(String param1, String param2) {
        F_map fragment = new F_map();
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
        return inflater.inflate(R.layout.fragment_map, container, false);
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        LatLng Ulaanbaatar= new LatLng(47.921230, 106.918556);
        LatLngBounds UB_box=new LatLngBounds(new LatLng(47.7540,106.5624),new LatLng(48.1487,107.3945));
        map.setLatLngBoundsForCameraTarget(UB_box);
        //TODO Offline Map here
        //GroundOverlayOptions UB=new GroundOverlayOptions().image().position();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Ulaanbaatar,15.0f));
        map.setOnMarkerClickListener(this);
        map.setOnCameraMoveListener(this);
        stop_drawer();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        boolean isA=((MainActivity)getActivity()).isA;
        if(isA){
            if(visibleMarkers.containsKey(((MainActivity)getActivity()).Astop)) { //Is on Display
                visibleMarkers.get(((MainActivity)getActivity()).Astop).remove();
                visibleMarkers.remove(((MainActivity)getActivity()).Astop); //Remove from Display and Hashmap
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_a));
            ((MainActivity)getActivity()).Astop=Integer.valueOf(marker.getSnippet());
            isA=false;
        } else {
            if(visibleMarkers.containsKey(((MainActivity)getActivity()).Bstop)) { //Is on Display
                visibleMarkers.get(((MainActivity)getActivity()).Bstop).remove();
                visibleMarkers.remove(((MainActivity)getActivity()).Bstop); //Remove from Display and Hashmap
            }
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.map_b));
            ((MainActivity)getActivity()).Bstop=Integer.valueOf(marker.getSnippet());
            isA=true;
        }
        ((MainActivity)getActivity()).isA=isA;
        return false;
    }

    @Override
    public void onCameraMove() {
        stop_drawer();
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

    GoogleMap map;

    @Override
    public void onStart() {
        super.onStart();
        Button button=(Button)getView().findViewById(R.id.map_go);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int A=((MainActivity)getActivity()).Astop;
                int B=((MainActivity)getActivity()).Bstop;
                if(A*B==0){
                    ((MainActivity)getActivity()).error_message();
                } else {
                    ((MainActivity)getActivity()).change_fragment("Route_Shower");
                }
            }
        });
        button=(Button)getView().findViewById(R.id.map_clear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).Astop=0;
                ((MainActivity)getActivity()).Bstop=0;
                map.clear();
                D_stops[] stops=((MainActivity)getActivity()).stops;
                for (int i = 1; i < stops.length; i++){
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(stops[i].loc);
                    markerOptions.title(stops[i].Name);
                    markerOptions.snippet(Integer.toString(stops[i].ID));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_stop));
                    map.addMarker(markerOptions);
                }
            }
        });

        SupportMapFragment mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_map);
        mapFragment.getMapAsync(this);
    }
    private HashMap<Integer, Marker> visibleMarkers = new HashMap<Integer, Marker>();
    public void stop_drawer(){
        D_stops[] stops=((MainActivity)getActivity()).stops;
        int A=((MainActivity)getActivity()).Astop;
        int B=((MainActivity)getActivity()).Bstop;
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        for (int s=1;s<stops.length;s++) {
            D_stops stop=stops[s];
            LatLng item=stop.loc;
            if(bounds.contains(new LatLng(item.latitude, item.longitude))) { //Is in boundary?
                if(!visibleMarkers.containsKey(s)) { //Is on Display?
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(stop.loc);
                    markerOptions.title(stop.Name);
                    markerOptions.snippet(Integer.toString(stop.ID));
                    if(stop.ID==A) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_a));
                    else if(stop.ID==B) markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_b));
                    else markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_stop));
                    visibleMarkers.put(s, map.addMarker(markerOptions)); //add marker
                }
            } else { //Not in boundary
                if(visibleMarkers.containsKey(s)) { //Is on Display
                    visibleMarkers.get(s).remove();
                    visibleMarkers.remove(s); //Remove from Display and Hashmap
                }
            }
        }
    }
}
