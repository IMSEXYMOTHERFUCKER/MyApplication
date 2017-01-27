package com.example.bilguun.busroutev1;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.wearable.Asset;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class MainActivity extends FragmentActivity implements BlankFragment.OnFragmentInteractionListener,F_details.OnFragmentInteractionListener,F_history.OnFragmentInteractionListener,F_home.OnFragmentInteractionListener,F_map.OnFragmentInteractionListener,F_route_shower.OnFragmentInteractionListener,F_stop_selector.OnFragmentInteractionListener{

    //User Datas
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    int Astop=0, Bstop=0;
    boolean isA=true;
    D_stops[] stops;
    D_route[] routes;
    LO_route_shower details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView home=(ImageView)findViewById(R.id.Home_Button);
        ImageView map=(ImageView)findViewById(R.id.Map_Button);
        ImageView log=(ImageView)findViewById(R.id.Log_Button);
        home.setBackgroundColor(0xFFC0c0c0);
        map.setBackgroundColor(0xFFFFFFFF);
        log.setBackgroundColor(0xFFFFFFFF);

        Fragment fr=new BlankFragment();
        fm=getSupportFragmentManager();
        fragmentTransaction=fm.beginTransaction();
        fragmentTransaction.replace(R.id.Fragment_Window,fr);
        fragmentTransaction.commit();

        change_fragment("Home");
        String jsonStr="";
        AssetManager am=getAssets();
        try {
            InputStream is=am.open("stops.json");
            jsonStr= IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONArray jsonArray=jsonObject.getJSONArray("stops");
            stops=new D_stops[jsonArray.length()+1];
            for(int s=0;s<jsonArray.length();s++){
                JSONObject c=jsonArray.getJSONObject(s);
                int neighbors=c.getInt("neighbors");
                D_stops tmp=new D_stops(s+1,c.getString("Name"),neighbors,c.getDouble("locX"),c.getDouble("locY"));
                int[] neighbor_id=new int[neighbors]; //NeighborIDS
                if(neighbors==1){
                    neighbor_id[0]=c.getInt("n_IDs");
                } else {
                    JSONArray n_IDs=c.getJSONArray("n_IDs");
                    for(int n=0;n<neighbors;n++){
                        neighbor_id[n]=n_IDs.getInt(n);
                    }
                }
                tmp.nStopID=neighbor_id;
                stops[s+1]=tmp;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            InputStream is=am.open("routes.json");
            jsonStr= IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONArray jsonArray=jsonObject.getJSONArray("routes");
            routes=new D_route[jsonArray.length()];
            for(int r=0;r<jsonArray.length();r++){
                JSONObject c=jsonArray.getJSONObject(r);
                int stop_num=c.getInt("stop_num");
                String kml=c.getString("kml");
                D_route tmp=new D_route(r,c.getString("Name"),stop_num,kml);
                JSONArray stopIDs=c.getJSONArray("stopIDs");
                for(int n=0;n<stop_num;n++){
                    tmp.stopIDs[n]=stopIDs.getInt(n);
                }
                routes[r]=tmp;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void main_buttons(View view) {
        ImageView home=(ImageView)findViewById(R.id.Home_Button);
        ImageView map=(ImageView)findViewById(R.id.Map_Button);
        ImageView log=(ImageView)findViewById(R.id.Log_Button);
        switch (view.getId()){
            case R.id.Home_Button:
                change_fragment("Home");
                home.setBackgroundColor(0xFFC0C0C0);
                map.setBackgroundColor(0xFFFFFFFF);
                log.setBackgroundColor(0xFFFFFFFF);
                break;
            case R.id.Log_Button:
                change_fragment("History");
                home.setBackgroundColor(0xFFFFFFFF);
                map.setBackgroundColor(0xFFFFFFFF);
                log.setBackgroundColor(0xFFC0C0C0);
                break;
            case R.id.Map_Button:
                change_fragment("Map");
                home.setBackgroundColor(0xFFFFFFFF);
                map.setBackgroundColor(0xFFC0C0C0);
                log.setBackgroundColor(0xFFFFFFFF);
                break;
        }
    }
    int cur_fID=0; //Home=0 Map=1 History=2 Other=3
    public void change_fragment(String fragName){
        Fragment fr=new F_home();
        fragmentTransaction=fm.beginTransaction();
        switch (fragName){
            case "Home":
                fr=new F_home();
                if(cur_fID==2){
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                } else if(cur_fID==1){
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                cur_fID=0;
                break;
            case "Map":
                fr=new F_map();
                if((cur_fID==0)|(cur_fID==2)){
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
                }
                cur_fID=1;
                break;
            case "History":
                fr=new F_history();
                if((cur_fID==0)|(cur_fID==1)){
                    fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                }
                cur_fID=2;
                break;
            case "Stop_Selector":
                fr=new F_stop_selector();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;
            case "Route_Shower":
                fr=new F_route_shower();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;
            case "Details":
                fr=new F_details();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                break;
        }
        fragmentTransaction.replace(R.id.Fragment_Window,fr);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public Vector<D_stops> stops_sort(String target){
        Vector<D_stops> filtered=new Vector<>();
        for (int i = 1; i < stops.length; i++) {
            if(stops[i].Name.toLowerCase().contains(target.toLowerCase())){
                filtered.add(stops[i]);
            }
            if(filtered.capacity()==20) break;
        }
        return filtered;
    };

    public Vector<LO_route_shower> route_finder(){
        Vector<LO_route_shower> found=new Vector<>();
        Vector<Integer> Aroutes=new Vector<>();
        Vector<Integer> Broutes=new Vector<>();
        for(int rID=0;rID<routes.length;rID++){
            int[] r_stops=routes[rID].stopIDs;
            int x=0,y=0;
            for (int s = 0; s < r_stops.length; s++) {
                if(r_stops[s]==Astop){ Aroutes.addElement(rID); x=s+1;}
                if(r_stops[s]==Bstop){ Broutes.addElement(rID); y=s+1;}
                if(x*y!=0) {
                    //TODO calculate time here
                    LO_route_shower tmp=new LO_route_shower(Math.abs(x-y),5,1);
                    int[] a={rID};
                    int[] b={Astop,Bstop};
                    String[] c={routes[rID].Name};
                    String[] d={stops[Astop].Name,stops[Bstop].Name};
                    tmp.RouteIDs=a;
                    tmp.Switch_Stops=b;
                    tmp.RouteNames=c;
                    tmp.StopNames=d;
                    found.addElement(tmp);
                    Aroutes.remove(Aroutes.lastElement());
                    Broutes.remove(Broutes.lastElement());
                    break;
                }
            }
        }
        //2 take
        for (Integer aroute : Aroutes) {
            int[] a_ss=routes[aroute].stopIDs;
            for (int a_s = 0; a_s < a_ss.length; a_s++) {
                for (Integer broute : Broutes) {
                    int[] b_ss=routes[broute].stopIDs;
                    for (int b_s = 0; b_s < b_ss.length; b_s++) {
                        if(a_ss[a_s]==b_ss[b_s]){
                            LO_route_shower tmp=new LO_route_shower(35,5,2);
                            int[] a={aroute,broute};
                            int[] b={Astop,a_ss[a_s],Bstop};
                            String[] c={routes[aroute].Name,routes[broute].Name};
                            String[] d={stops[Astop].Name,stops[a_ss[a_s]].Name,stops[Bstop].Name};
                            tmp.RouteIDs=a;
                            tmp.Switch_Stops=b;
                            tmp.RouteNames=c;
                            tmp.StopNames=d;
                            found.addElement(tmp);
                            break;
                        }
                    }
                }
            }
        }
        //TODO if can't find in 2take
        return found;
    }

    public void error_message(){
        LayoutInflater inflater = (LayoutInflater)MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popup_view=inflater.inflate(R.layout.err_popup, null, false);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (size.x*8)/10;
        int height = size.y/10;
        final PopupWindow pw = new PopupWindow(popup_view, width, height, true);
        // The code below assumes that the root container has an id called 'main'
        pw.setBackgroundDrawable(new BitmapDrawable());
        pw.setOutsideTouchable(true);
        pw.showAtLocation(this.findViewById(R.id.Fragment_Window), Gravity.CENTER, 0, 0);
        TextView textView=(TextView)popup_view.findViewById(R.id.err_text);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
        if(Astop==0){
            textView.setText("Суух буудлаа оруулна уу");
        } else {
            textView.setText("Буух буудлаа оруулна уу");
        }
    }

    @Override
    public void onBackPressed() {
        int count = fm.getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            fm.popBackStack();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    public void cleaner(){
        ImageView home=(ImageView)findViewById(R.id.Home_Button);
        ImageView map=(ImageView)findViewById(R.id.Map_Button);
        ImageView log=(ImageView)findViewById(R.id.Log_Button);
        home.setBackgroundColor(0xFFFFFFFF);
        map.setBackgroundColor(0xFFFFFFFF);
        log.setBackgroundColor(0xFFFFFFFF);
    }
}
