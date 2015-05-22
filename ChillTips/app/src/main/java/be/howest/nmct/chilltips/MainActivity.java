package be.howest.nmct.chilltips;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import be.howest.nmct.chilltips.models.Chilltip;


public class MainActivity extends Activity implements SideBarFragment.OnSideBarSelectionListener, OverViewFragment.OnOverViewSelectListener , AddTipFragment.OnAddChillListener {

    private Chilltip edittip;
    private Bitmap image;
    public final int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new OverViewFragment())
                    .commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
  /*  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
        }
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if(resultCode == RESULT_OK){
                    Bundle extras = data.getExtras();
                    edittip.setPreviewImage(UUID.randomUUID().toString());
                    image = (Bitmap) extras.get("data");
                    FragmentManager manager = getFragmentManager();
                    AddTipFragment fragment = new AddTipFragment().newInstance(edittip);
                    manager.beginTransaction().replace(R.id.container,fragment).commit();
                }
                break;
            default:break;
        }
    }

    @Override
    public void OnOverViewSelectInteraction(Chilltip tip) {

    }

    @Override
    public void onSidebarSelectInteraction(int id) {
        FragmentManager manager = getFragmentManager();
        if(id==1)
        {
           OverViewFragment fragment = new OverViewFragment();
           manager.beginTransaction().replace(R.id.container,fragment).commit();
        }
        else
        if(id==2)
        {
            AddTipFragment fragment = new AddTipFragment();
            manager.beginTransaction().replace(R.id.container,fragment).commit();
        }
    }

    @Override
    public void onAddChillInteraction(Chilltip tip) {
        if(tip.getPreviewImage()=="")
        {
            edittip = tip;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
        else
        {
            FileOutputStream out = null;
            try {
                String filename = tip.getPreviewImage();
                File file = new File(getFilesDir() + "/" + filename +".png");
                out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.PNG, 100, out);
                edittip.setPreviewImage(filename);
                FragmentManager manager = getFragmentManager();
                OverViewFragment fragment = OverViewFragment.newInstance(edittip);
                manager.beginTransaction().replace(R.id.container,fragment).commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {

                }
            }
        }
    }
}
