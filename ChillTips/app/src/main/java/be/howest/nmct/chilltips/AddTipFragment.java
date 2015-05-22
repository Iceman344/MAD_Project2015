package be.howest.nmct.chilltips;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import be.howest.nmct.chilltips.models.Chilltip;

public class AddTipFragment extends Fragment {
    private static Chilltip savedTip=null;

    private OnAddChillListener mListener;

    public static AddTipFragment newInstance(Chilltip chilltip) {
        AddTipFragment fragment = new AddTipFragment();
        savedTip = chilltip;
        return fragment;
    }

    public AddTipFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }
    private EditText txb_title;
    private EditText txb_desc;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_tip, container, false);
        Button photobutton = (Button) v.findViewById(R.id.btn_takephoto);
        txb_title = (EditText) v.findViewById(R.id.txb_title);
        txb_desc = (EditText) v.findViewById(R.id.txb_desc);
        if(savedTip!=null)
        {
            txb_title.setText(savedTip.getTitle());
            txb_desc.setText(savedTip.getDescription());
        }
        photobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String local = "";
                if(savedTip!=null)
                    local=savedTip.getLocation();
                mListener.onAddChillInteraction(new Chilltip(txb_title.getText().toString(),txb_desc.getText().toString(),local,""));
            }
        });
        ((Button)v.findViewById(R.id.btn_savechilltip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chilltip tip = savedTip;
                LocationManager locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                tip.setLocation(""+ lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude());
                savedTip=null;
                mListener.onAddChillInteraction(tip);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAddChillListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /* Class My Location Listener */
    public class MyLocationListener implements LocationListener
    {

        @Override
        public void onLocationChanged(Location loc)
        {

            String Text = loc.getLatitude() + "," + loc.getLongitude();
            Toast.makeText(getActivity(), Text, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getActivity(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getActivity(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }

    public interface OnAddChillListener {
        public void onAddChillInteraction(Chilltip tip);
    }

}
