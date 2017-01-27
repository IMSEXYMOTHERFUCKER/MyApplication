package com.example.bilguun.busroutev1;

import java.io.Serializable;
import java.text.SimpleDateFormat;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class D_history implements Serializable{
    int A,B;
    String Aname,Bname,Time;
    public D_history(int _A,String nameA,int _B,String nameB,String _Time){
        A=_A; B=_B;Time=_Time;
        Aname=nameA; Bname=nameB;
    }
}
