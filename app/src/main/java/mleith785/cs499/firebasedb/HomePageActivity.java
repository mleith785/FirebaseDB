package mleith785.cs499.firebasedb;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class HomePageActivity extends AppCompatActivity
{
    static public final int REQUEST_LOCATION = 1;
    boolean pressed_search_loc_gps;


    /**
     * <h1> Create of HomePageActivity</h1>
     * This is the navigation homepage for the application
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

    }

    /**
     * <h1> Search by name city location</h1>
     * This is the callback to the button press.  This queries the database so it does not require
     * permission to access the user's location.  Start the activity to move onto this
     *
     * @param view
     */
    public void SearchNameCityLocClick(View view)
    {
        //todo bring this in later
//        Intent intent = new Intent(this, SearchByNameCityFeat.class);
//        startActivity(intent);

    }


    /**
     * <h1> Search by current location</h1>
     * This is the search by current location callback button.  We test if we have fine access
     * permissions first. This will do the callback for onRequestPermissionResult that will get what
     * the user entered as allowed or denied
     *
     * @param view
     */
    public void SearchCurrentLocationClick(View view)
    {
        //We will look at their current location and launch the activity if we find
        //something.

        //First we check for permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        pressed_search_loc_gps = true;
    }

    /**
     * <h1> Request permission callback</h1>
     * This gets the results if they hit allow or deny on the app itself.  Toast if they reject
     * Allow them to proceed if they hit OK.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION)
        {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //They accepted everything, it is ready to go
                if (pressed_search_loc_gps)
                {
                    Intent intent = new Intent(this, SearchByLocation.class);
                    startActivity(intent);
                }
                else
                {
                    //TODO: bring this in later
//                    Intent intent = new Intent(this, AddSiteMapActivity.class);
//                    startActivity(intent);
                }


            }
            else
            {
                //Now toast that they added this thing and hide the button
                int duration = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Oh NO!  Need to turn on location", duration);
                toast.show();
            }
        }
        pressed_search_loc_gps = false;

    }


    /**
     * <h1> Launch a search for sites by map list</h1>
     * This launches the activity to search for the campsite with a list in the database
     *
     * @param view
     */
    public void SearchByListClick(View view)
    {
        //Todo bring this in later
//        Intent intent = new Intent(this, MapListNavActivity.class);
//        intent.putExtra("Called By", "List");
//
//        startActivity(intent);
    }

    /**
     * <h1> Go to the favorites list</h1>
     * starts the favorites activity which is a list of campsites
     *
     * @param view
     */
    public void YourFavoriteClick(View view)
    {

        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    /**
     * <h1>Add site to database</h1>
     * This launches the activity to add a site to the database. uses callback to make sure
     * permissions are granted
     *
     * @param view
     */
    public void AddSiteClick(View view)
    {

        //First we check for permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }


}
