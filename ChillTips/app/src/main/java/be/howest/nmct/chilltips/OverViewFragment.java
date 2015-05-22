package be.howest.nmct.chilltips;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import be.howest.nmct.chilltips.loader.ChillTipLoader;
import be.howest.nmct.chilltips.models.Chilltip;

public class OverViewFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Chilltip>> {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public static List<Chilltip> cachedlist = null;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnOverViewSelectListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverViewFragment newInstance(String param1, String param2) {
        OverViewFragment fragment = new OverViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public OverViewFragment() {
        // Required empty public constructor
    }

    public static OverViewFragment newInstance(Chilltip tip)
    {
        OverViewFragment fragment = new OverViewFragment();
        if(cachedlist== null)
            cachedlist = new ArrayList<Chilltip>();
        cachedlist.add(tip);
        return  fragment;
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
        View v = inflater.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnOverViewSelectListener) activity;
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
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(cachedlist==null)
        {
            getLoaderManager().initLoader(0,null,this);
        }
        else
        {
            WriteJson();
            mAdapter = new ChilltipsAdapter(cachedlist);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public Loader<List<Chilltip>> onCreateLoader(int id, Bundle args) {
        return new ChillTipLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<Chilltip>> loader, List<Chilltip> data) {
        cachedlist=data;
        mAdapter = new ChilltipsAdapter(data);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<List<Chilltip>> loader) {
        mAdapter = new ChilltipsAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
    }
    class ChilltipsAdapter extends RecyclerView.Adapter<ChilltipsAdapter.ViewHolder> {
        private List<Chilltip> data;
        public ChilltipsAdapter(List<Chilltip> chilllist)
        {
            data=chilllist;
        }
        @Override
        public ChilltipsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            // create a new view
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_chillcard, viewGroup, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            final Chilltip chilltip = data.get(i);
            viewHolder.textViewTitle.setText(chilltip.getTitle());
            viewHolder.textViewDesc.setText(chilltip.getDescription());
            viewHolder.imageViewprev.setImageURI(Uri.parse(getActivity().getFilesDir() + "/" + chilltip.getPreviewImage() + ".png"));
            viewHolder.btn_visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + chilltip.getLocation());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        class ViewHolder extends RecyclerView.ViewHolder{
            public TextView textViewTitle;
            public TextView textViewDesc;
            public ImageView imageViewprev;
            public Button btn_visit;
            public ViewHolder(View itemView) {
                super(itemView);
                this.btn_visit = (Button) itemView.findViewById(R.id.btn_visit);
                this.textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
                this.textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
                this.imageViewprev = (ImageView) itemView.findViewById(R.id.imageViewprev);
            }
        }
    }
    public interface OnOverViewSelectListener {
        public void OnOverViewSelectInteraction(Chilltip tip);
    }

    private void WriteJson()
    {
        try {
            // Here we convert Java Object to JSON
            JSONArray jsonObj = new JSONArray();
            //cachedlist.add(new Chilltip("Test","Testy",""));
            for (Chilltip chill : cachedlist)
            {
                JSONObject jsonAdd = new JSONObject();
                jsonAdd.put("title",chill.getTitle());
                jsonAdd.put("description",chill.getDescription());
                jsonAdd.put("previewImage",chill.getPreviewImage());
                jsonAdd.put("location",chill.getLocation());
                jsonObj.put(jsonAdd);
            }
            String test = jsonObj.toString();
            OutputStreamWriter outputStream = new OutputStreamWriter(getActivity().openFileOutput("data.json", Context.MODE_PRIVATE));
            outputStream.write(jsonObj.toString());
            outputStream.close();

        }
        catch(JSONException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

}
