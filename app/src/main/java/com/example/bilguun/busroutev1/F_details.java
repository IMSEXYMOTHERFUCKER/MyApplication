package com.example.bilguun.busroutev1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.mapbox.mapboxsdk.constants.MapboxConstants.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_details.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_details#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_details extends Fragment implements OnMapReadyCallback {
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
    public void onMapReady(MapboxMap mapboxMap) {
        //offline_downloader();
        map=mapboxMap;
        LO_route_shower target=((MainActivity)getActivity()).details;
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

    MapboxMap map;
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

        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Build mapboxMap
        MapboxMapOptions options = new MapboxMapOptions();
        options.styleUrl(Style.MAPBOX_STREETS);
        LatLng Ulaanbaatar= new LatLng(47.921230, 106.918556);
        options.camera(new CameraPosition.Builder()
                .target(Ulaanbaatar)
                .zoom(12)
                .build());

        // Create map fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

        // Add map fragment to parent container
        transaction.add(R.id.details_map, mapFragment, "com.mapbox.map");
        transaction.commit();
        mapFragment.getMapAsync(this);
    }
    OfflineManager offlineManager;
    MapView mapView;
    TextView p_view;
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private boolean isEndNotified;
    public void offline_downloader() {
        offlineManager = OfflineManager.getInstance(getActivity());

        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(47.75157, 106.55746)) // Northeast
                .include(new LatLng(48.07164, 107.23617)) // Southwest
                .build();

        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                Style.MAPBOX_STREETS,
                latLngBounds,
                9,
                14,
                1);

        // Set the metadata
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, "Yosemite National Park");
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }

        // Create the region asynchronously
        offlineManager.createOfflineRegion(
                definition,
                metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

                        // Display the download progress bar
                        p_view = (TextView) getView().findViewById(R.id.detail_percentage);
                        startProgress();

                        // Monitor the download progress using setObserver
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {

                                // Calculate the download percentage and update the progress bar
                                double percentage = status.getRequiredResourceCount() >= 0
                                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                        0.0;

                                if (status.isComplete()) {
                                    // Download complete
                                    endProgress("Region downloaded successfully.");
                                } else if (status.isRequiredResourceCountPrecise()) {
                                    // Switch to determinate state
                                    setPercentage((int) Math.round(percentage));
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
                                // If an error occurs, print to logcat
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
                                // Notify if offline region exceeds maximum tile count
                                Log.e(TAG, "Mapbox tile count limit exceeded: " + limit);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });
    }

    private void startProgress() {
        // Start and show the progress bar
        isEndNotified = false;
        p_view.setText("Газрын зургыг татаж байна:");
        p_view.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        p_view.setText("Газрын зургыг татаж байна: "+Integer.toString(percentage));
    }

    private void endProgress(final String message) {
        // Don't notify more than once
        if (isEndNotified) {
            return;
        }

        // Stop and hide the progress bar
        isEndNotified = true;
        p_view.setVisibility(View.GONE);

        // Show a toast
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }
}

