package com.example.bilguun.busroutev1;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Created by Bilguun on 1/19/2017.
 */

public class LA_history extends ArrayAdapter {
    public Vector<D_history> objects;
    Activity context;

    public LA_history(Activity context, Vector<D_history> resource) {
        //get datas from file
        super(context, R.layout.lo_history,resource);
        this.context=context;
        objects=resource;
    }

    int pos=0;
    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater=context.getLayoutInflater();
        View rowview=layoutInflater.inflate(R.layout.lo_history,null,true);
        pos=position;
        D_history tmp=objects.elementAt(position);
        TextView textView=(TextView)rowview.findViewById(R.id.lo_h_astop);
        textView.setText(tmp.Aname);
        textView=(TextView)rowview.findViewById(R.id.lo_h_bstop);
        textView.setText(tmp.Bname);
        textView=(TextView)rowview.findViewById(R.id.lo_h_time);
        textView.setText(tmp.Time);
        Button button=(Button)rowview.findViewById(R.id.lo_h_remove);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objects.removeElementAt(pos);
                notifyDataSetChanged();

                String jsonStr="";
                JSONObject tofile=new JSONObject();
                try {
                    InputStream is=context.openFileInput("History");
                    jsonStr= IOUtils.toString(is);
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    JSONObject jsonObject=new JSONObject(jsonStr);
                    JSONArray jsonArray=jsonObject.getJSONArray("history");
                    jsonArray.remove(position);
                    tofile.put("history",jsonArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    OutputStream os=context.openFileOutput("History",Context.MODE_PRIVATE);
                    os.write(tofile.toString().getBytes());
                    os.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return rowview;
    }
}
