package com.example.bilguun.busroutev1;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class D_route {
    public int ID;
    public String Name,kml;
    public int[] stopIDs;
    //public D_row[] time_interval; //Time, Interval

    public D_route(int _ID,String _Name,int stop_num,String _kml){
        ID=_ID;
        Name=_Name;
        stopIDs=new int[stop_num];
        //time_interval=new D_row[row_num];
        kml=_kml;
    }
}
