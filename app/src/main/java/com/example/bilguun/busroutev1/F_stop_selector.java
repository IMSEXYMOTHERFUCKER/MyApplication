package com.example.bilguun.busroutev1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link F_stop_selector.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link F_stop_selector#newInstance} factory method to
 * create an instance of this fragment.
 */
public class F_stop_selector extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public F_stop_selector() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment F_stop_selector.
     */
    public static F_stop_selector newInstance(String param1, String param2) {
        F_stop_selector fragment = new F_stop_selector();
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
        return inflater.inflate(R.layout.fragment_stop_selector, container, false);
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

    ListView lv;
    LA_stop_selector adapter;
    EditText editText;
    @Override
    public void onStart() {
        super.onStart();
        adapter_init();
        lv=(ListView)getView().findViewById(R.id.selector_list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AB_changer(((MainActivity)getActivity()).isA,position);
            }
        });

        editText=(EditText)getView().findViewById(R.id.selector_keyboard);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter_updater(editText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button button=(Button)getView().findViewById(R.id.selector_clear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                adapter_init();
            }
        });
    }

    void adapter_init(){
        Vector<String> tmps=new Vector<>();
        Vector<Integer> tmpt=new Vector<>();
        for(int n=1;n<20;n++){
            D_stops tmp=((MainActivity)getActivity()).stops[n];
            tmps.add(tmp.Name);
            tmpt.add(tmp.ID);
        }
        adapter=new LA_stop_selector(getActivity(),tmps,tmpt);
    }
    void adapter_updater(String target){
        if(target=="") return;
        Vector<D_stops> result=((MainActivity)getActivity()).stops_sort(target);
        Vector<String> names=new Vector<>();
        Vector<Integer> ids=new Vector<>();
        if(result.size()==0){
            names.add("Юу ч олдсонгүй");
            ids.add(0);
        } else {
            for(D_stops tmp : result){
                names.add(tmp.Name);
                ids.add(tmp.ID);
            }
        }
        adapter=new LA_stop_selector(getActivity(),names,ids);
        lv.setAdapter(adapter);
    }

    void AB_changer(boolean isA, int loc){
        loc=adapter.ids.elementAt(loc);
        if(isA){
            ((MainActivity)getActivity()).Astop=loc;
        } else {
            ((MainActivity)getActivity()).Bstop=loc;
        }
        ((MainActivity)getActivity()).change_fragment("Home");
    }
}
