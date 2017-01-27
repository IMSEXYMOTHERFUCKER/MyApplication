package com.example.bilguun.busroutev1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
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

import org.json.JSONObject;

import java.util.HashMap;

import static com.mapbox.mapboxsdk.constants.MapboxConstants.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_map extends Fragment implements OnMapReadyCallback, MapboxMap.OnMarkerClickListener, MapboxMap.OnCameraChangeListener{
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
    public void onMapReady(MapboxMap googleMap) {
        offline_downloader();
        map=googleMap;
        //TODO Offline Map here
        //GroundOverlayOptions UB=new GroundOverlayOptions().image().position();
        map.setOnMarkerClickListener(this);
        map.setOnCameraChangeListener(this);
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
            marker.setIcon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_a));
            ((MainActivity)getActivity()).Astop=Integer.valueOf(marker.getSnippet());
            isA=false;
        } else {
            if(visibleMarkers.containsKey(((MainActivity)getActivity()).Bstop)) { //Is on Display
                visibleMarkers.get(((MainActivity)getActivity()).Bstop).remove();
                visibleMarkers.remove(((MainActivity)getActivity()).Bstop); //Remove from Display and Hashmap
            }
            marker.setIcon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_b));
            ((MainActivity)getActivity()).Bstop=Integer.valueOf(marker.getSnippet());
            isA=true;
        }
        ((MainActivity)getActivity()).isA=isA;
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition position) {
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

    MapboxMap map;

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
                    markerOptions.icon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_stop));
                    map.addMarker(markerOptions);
                }
            }
        });

        final FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Build mapboxMap
        MapboxMapOptions options = new MapboxMapOptions();
        options.styleUrl(Style.MAPBOX_STREETS);
        LatLng Ulaanbaatar= new LatLng(47.921230, 106.918556);
        options.camera(new CameraPosition.Builder()
                .target(Ulaanbaatar)
                .zoom(14)
                .build());

        // Create map fragment
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

        // Add map fragment to parent container
        transaction.add(R.id.map_map, mapFragment, "com.mapbox.map");
        transaction.commit();
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
            if(bounds.contains(new LatLng(item.getLatitude(), item.getLongitude()))) { //Is in boundary?
                if(!visibleMarkers.containsKey(s)) { //Is on Display?
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(stop.loc);
                    markerOptions.title(stop.Name);
                    markerOptions.snippet(Integer.toString(stop.ID));
                    if(stop.ID==A) markerOptions.icon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_a));
                    else if(stop.ID==B) markerOptions.icon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_b));
                    else markerOptions.icon(IconFactory.getInstance(getActivity()).fromResource(R.mipmap.map_stop));
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
                        p_view = (TextView)getView().findViewById(R.id.map_percentage);
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
        p_view.setVisibility(View.VISIBLE);
    }

    private void setPercentage(final int percentage) {
        p_view.setText("Газрын зураг татаж байна: "+Integer.toString(percentage)+"%");
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
