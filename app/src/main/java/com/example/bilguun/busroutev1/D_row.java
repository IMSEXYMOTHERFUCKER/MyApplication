package com.example.bilguun.busroutev1;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class D_row {
    public int[] data1;
    public int[] data2;

    public D_row(){}

    public D_row(int n,int[] d1,int[] d2){
        data1=new int[n];
        data2=new int[n];
        for(int i=0;i<n;i++){
            data1[i]=d1[i];
            data2[i]=d2[i];
        }
    }
}
