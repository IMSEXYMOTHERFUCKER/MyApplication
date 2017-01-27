package com.example.bilguun.busroutev1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Comparator;
import java.util.Vector;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class LA_route_shower extends ArrayAdapter{
    Vector<LO_route_shower> objects=new Vector<>();
    Activity context;
    public LA_route_shower(Activity context,Vector<LO_route_shower> resource) {
        super(context,R.layout.lo_route_shower,resource);
        this.context=context;
        objects=resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View rowview=layoutInflater.inflate(R.layout.lo_route_shower,null,true);

        LO_route_shower tmp=objects.elementAt(position);
        TextView textView=(TextView)rowview.findViewById(R.id.lo_r_name);
        textView.setText(tmp.RouteNames[0]);
        textView=(TextView)rowview.findViewById(R.id.lo_r_duration);
        textView.setText(Integer.toString(tmp.Duration)+" min");
        textView=(TextView)rowview.findViewById(R.id.lo_r_ctime);
        textView.setText(Integer.toString(tmp.Arrival)+" min");
        textView=(TextView)rowview.findViewById(R.id.lo_r_ntake);
        textView.setText(Integer.toString(tmp.Ntake));

        return rowview;
    }
}
