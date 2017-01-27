package com.example.bilguun.busroutev1;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_details.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_details extends Fragment implements OnMapReadyCallback{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public F_details() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment F_details.
     */
    public static F_details newInstance(String param1, String param2) {
        F_details fragment = new F_details();
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
        return inflater.inflate(R.layout.fragment_details, container, false);
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
        LO_route_shower target=((MainActivity)getActivity()).details;
        LatLng Ulaanbaatar= new LatLng(47.921230, 106.918556);
        LatLngBounds UB_box=new LatLngBounds(new LatLng(47.7540,106.5624),new LatLng(48.1487,107.3945));
        map.setLatLngBoundsForCameraTarget(UB_box);
        //TODO Offline Map here
        //GroundOverlayOptions UB=new GroundOverlayOptions().image().position();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(Ulaanbaatar,12.0f));
        int[] colors={0xFF000000,0xFFc0c0c0};
        int i=0;
        for (int routeID:target.RouteIDs) {
            D_route route=((MainActivity)getActivity()).routes[routeID];
            PolylineOptions polyLineOptions=new PolylineOptions();
            try {
                InputStream in=getActivity().getAssets().open(route.kml);
                String[] strings=IOUtils.toString(in).split(",");
                for(int s=0;s<strings.length;s=s+2){
                    polyLineOptions.add(new LatLng(Double.parseDouble(strings[s+1]),Double.parseDouble(strings[s])));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            polyLineOptions.color(colors[i]);
            i++;
            map.addPolyline(polyLineOptions);
        }
        for(int stopID:target.Switch_Stops){
            D_stops stop=((MainActivity)getActivity()).stops[stopID];
            MarkerOptions markerOptions=new MarkerOptions();
            markerOptions.position(stop.loc);
            markerOptions.title(stop.Name);
            map.addMarker(markerOptions);
        }
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
    public void onStop() {
        super.onStop();
        //tabHost.clearAllTabs();
    }
    boolean first_time=true;
    @Override
    public void onStart() {
        super.onStart();
        TabHost tabHost=(TabHost)getView().findViewById(R.id.details_tab_host);
        tabHost.setup();
        if(first_time){
            final LO_route_shower object=((MainActivity)getActivity()).details;
            int[] cids={R.id.details_tab1,R.id.details_tab2,R.id.details_tab3};
            int[] nids={R.id.pop_detail_name1,R.id.pop_detail_name2,R.id.pop_detail_name3};
            int[] aids={R.id.pop_detail_A1,R.id.pop_detail_A2,R.id.pop_detail_A3};
            int[] bids={R.id.pop_detail_B1,R.id.pop_detail_B2,R.id.pop_detail_B3};
            TextView textView=(TextView)getView().findViewById(R.id.detail_duration);
            textView.setText(Integer.toString(object.Duration)+" min");
            textView=(TextView)getView().findViewById(R.id.detail_ctime);
            textView.setText(Integer.toString(object.Arrival)+" min");
            Button button=(Button)getView().findViewById(R.id.detail_save);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    D_stops stopA=((MainActivity)getActivity()).stops[object.Switch_Stops[0]];
                    D_stops stopB=((MainActivity)getActivity()).stops[object.Switch_Stops[object.Ntake]];
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    D_history save_it=new D_history(stopA.ID,stopA.Name,stopB.ID,stopB.Name, df.format(Calendar.getInstance().getTime()));
                    String jsonStr="";
                    try {
                        InputStream is=getActivity().openFileInput("History");
                        jsonStr= IOUtils.toString(is);
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(jsonStr==""){
                        //first time
                        jsonStr="{history:[]}";
                    }
                    JSONObject tofile=new JSONObject();;
                    try {
                        JSONObject jsonObject=new JSONObject(jsonStr);
                        JSONArray jsonArray=jsonObject.getJSONArray("history");
                        JSONObject target=new JSONObject();
                        target.put("A",save_it.A);
                        target.put("Aname",save_it.Aname);
                        target.put("B",save_it.B);
                        target.put("Bname",save_it.Bname);
                        target.put("Time",save_it.Time);
                        jsonArray.put(target);
                        tofile.put("history",jsonArray);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        OutputStream os=getActivity().openFileOutput("History",Context.MODE_PRIVATE);
                        os.write(tofile.toString().getBytes());
                        os.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            //Tab1
            for(int t=0;t<object.Ntake;t++){
                TabHost.TabSpec spec= tabHost.newTabSpec("Bus "+Integer.toString(t));
                spec.setIndicator("Автобус "+Integer.toString(t+1));
                spec.setContent(cids[t]);
                textView=(TextView)getView().findViewById(nids[t]);
                textView.setText(object.RouteNames[t]);
                textView=(TextView)getView().findViewById(aids[t]);
                textView.setText(object.StopNames[t]);
                textView=(TextView)getView().findViewById(bids[t]);
                textView.setText(object.StopNames[t+1]);
                tabHost.addTab(spec);
            }
            first_time=false;
        }

        SupportMapFragment mapFragment=(SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.details_map);
        mapFragment.getMapAsync(this);
    }
}
