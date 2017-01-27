package com.example.bilguun.busroutev1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Vector;

public class LA_stop_selector extends ArrayAdapter {
    Vector<String> names=new Vector<>();
    Vector<Integer> ids=new Vector<>();
    Activity context;
    public LA_stop_selector(Activity context, Vector<String> nameste, Vector<Integer> idste) {
        super(context, R.layout.lo_stop_selector,nameste);
        this.context=context;
        names=nameste;
        ids=idste;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View rowview=layoutInflater.inflate(R.layout.lo_stop_selector,null,true);

        TextView textView=(TextView)rowview.findViewById(R.id.lo_ss_name);
        textView.setText(names.get(position));
        return rowview;
    }
}
