package be.howest.nmct.chilltips.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.media.Image;
import android.util.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;

import be.howest.nmct.chilltips.models.Chilltip;

/**
 * Created by JukeBox on 11/05/2015.
 */
public class ChillTipLoader extends AsyncTaskLoader<List<Chilltip>> {
    private List<Chilltip> mChilltips;
    private Context context;
    public ChillTipLoader(Context context) {
        super(context);
        this.context = context;
    }
    private static Object lock = new Object();
    private void loadData()
    {
        synchronized (lock){
            if(mChilltips!=null)return;
            List<Chilltip> templist = new ArrayList<Chilltip>();
            InputStream inputStream = null;
            JsonReader reader = null;
            try {
                File file = new File(context.getFilesDir() + "/" + "data.json");
                inputStream = new BufferedInputStream(new FileInputStream(file));
                reader = new JsonReader(new InputStreamReader(inputStream));
                reader.beginArray();
                while (reader.hasNext())
                {
                    reader.beginObject();

                    String title = "";
                    String description = "";
                    String previewImage="";
                    String location="";

                    while (reader.hasNext())
                    {
                        String name = reader.nextName();
                        if(name.equals("title"))
                        {
                           title = reader.nextString();
                        }
                        else
                        {
                            if (name.equals("description"))
                            {
                                description = reader.nextString();
                            }
                            else
                            {
                                if (name.equals("previewImage"))
                                {
                                    previewImage = reader.nextString();
                                }
                                else
                                {
                                    if (name.equals("location"))
                                    {
                                        location = reader.nextString();
                                    }
                                    else
                                    {
                                        reader.skipValue();
                                    }

                                }
                            }
                        }

                    }

                    templist.add(new Chilltip(title,description,location,previewImage));
                    reader.endObject();

                }
                reader.endArray();
            }
            catch (FileNotFoundException ex)
            {
                System.out.println(ex);
            }
            catch (UnsupportedEncodingException ex)
            {
                System.out.println(ex);
            }
            catch (IOException ex)
            {
                System.out.println(ex);
            }
            finally {
                mChilltips=templist;
            }
        }
    }
    @Override
    protected void onStartLoading() {
        if (mChilltips != null) {
            deliverResult(mChilltips);
        }
        if (takeContentChanged() || mChilltips == null) {
            forceLoad();
        }
    }

    @Override
    public List<Chilltip> loadInBackground() {
        if (mChilltips == null) {
            loadData();
        }
        return mChilltips;
    }
}
