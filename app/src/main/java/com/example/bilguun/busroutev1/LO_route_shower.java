package com.example.bilguun.busroutev1;

import java.util.Comparator;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class LO_route_shower implements Comparable<LA_route_shower> {
    int Duration,Arrival,Ntake;
    int[] RouteIDs, Switch_Stops;
    String[] RouteNames,StopNames;

    public LO_route_shower(int _D,int _A,int _N){
        Duration=_D;
        Arrival=_A;
        Ntake=_N;
        RouteIDs=new int[Ntake];
        Switch_Stops=new int[Ntake+1];
        RouteNames=new String[Ntake];
        StopNames=new String[Ntake+1];
    }

    @Override
    public int compareTo(LA_route_shower another) {
        return 0;
    }

    public static Comparator<LO_route_shower> bynames = new Comparator<LO_route_shower>() {
        @Override
        public int compare(LO_route_shower lhs, LO_route_shower rhs) {
            return lhs.RouteNames[0].compareTo(rhs.RouteNames[0]);
        }
    };
    public static Comparator<LO_route_shower> byduration = new Comparator<LO_route_shower>() {
        @Override
        public int compare(LO_route_shower lhs, LO_route_shower rhs) {
            return lhs.Duration-rhs.Duration;
        }
    };
    public static Comparator<LO_route_shower> byctime = new Comparator<LO_route_shower>() {
        @Override
        public int compare(LO_route_shower lhs, LO_route_shower rhs) {
            return lhs.Arrival-rhs.Arrival;
        }
    };
    public static Comparator<LO_route_shower> byntake = new Comparator<LO_route_shower>() {
        @Override
        public int compare(LO_route_shower lhs, LO_route_shower rhs) {
            return lhs.Ntake-rhs.Ntake;
        }
    };
}
