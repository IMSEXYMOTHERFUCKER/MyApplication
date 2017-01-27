package com.example.bilguun.busroutev1;

import com.mapbox.mapboxsdk.geometry.LatLng;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class D_stops {
    public int ID;
    public String Name;
    LatLng loc;
    public int[] nStopID;
    public D_stops(int _ID,String _Name, int n_neighbor, double _locX, double _locY){
        ID=_ID; Name=_Name;
        nStopID=new int[n_neighbor];
        loc=new LatLng(_locY,_locX);
    }
}
