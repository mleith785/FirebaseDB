package mleith785.cs499.firebasedb;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddSiteMapActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, LocationListener
{

    private LocationManager mLocationManager;
    private GoogleMap mMap;
    private Marker LastMarker;
    private Location GPS_location;
    private TextView CampLongAddMapGui;
    private TextView CampLatAddMapGui;
    private boolean  ZoomToSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        CampLongAddMapGui = findViewById(R.id.CampLongAddMapGui);
        CampLatAddMapGui = findViewById(R.id.CampLatAddMapGui);
        ZoomToSpot=false;

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED )
        {
            //android whines if you don't put this protection in place which is dumb
            //permissions checked before even launching this activity
            //but if something would change we bail out of here


            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);


    }


    /**
     * <h1>onMapReady</h1>
     * This is the magic for adding a site.  Not much here besides choosing a default location to
     * zoom to but very imporant to set the callback listenter for when they click a location
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        // Move to the default TC-612 area baby!
        LatLng Twin_Cities = new LatLng(44.978905, -93.2658082);

        //Zoom this thing to TC metro.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Twin_Cities, 9.0f));

        //Set some callbacks, must go here vs on create
        mMap.setOnMapClickListener(this);
    }

    /**
     * <h1> AddMapSiteClickGetLoc</h1>
     * They chose to go to add the site.  First we make sure a location is actually on the map and
     * if so we go to the activity.  We have to pass data to that activtity so we just use the extra
     * put of latitude and longitude.
     *
     * @param view
     */
    public void AddMapSiteClickGetLoc(View view)
    {
        if (null == LastMarker)
        {

            int duration = Toast.LENGTH_SHORT;
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "Please Choose a Point", duration);
            toast.show();
        }
        else
        {
            Intent intent = new Intent(this, AddSiteSaveActivity.class);
            intent.putExtra("laty", String.valueOf(LastMarker.getPosition().latitude));
            intent.putExtra("longy", String.valueOf(LastMarker.getPosition().longitude));
            startActivity(intent);
        }

    }

    /**
     * <h1>onMapClick </h1>
     * They clicked a spot on the map.  We only want one marker at a time so remove the last one and
     * add this new on on there.  Also called by GPS if they wanna do that
     *
     * @param point
     */
    @Override
    public void onMapClick(LatLng point)
    {
        if (LastMarker != null)
        {
            LastMarker.remove();

        }
        LastMarker = mMap.addMarker(new MarkerOptions().position(point).title("You clicked dis!"));
        CampLatAddMapGui.setText(String.valueOf(point.latitude));
        CampLongAddMapGui.setText(String.valueOf(point.longitude));
        if(ZoomToSpot)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 10.0f));
            ZoomToSpot=false;
        }

    }

    /**
     * Button press that lets them choose their location, call the same handling for
     * placing the marker but put a nice Zoom effect on it.  Oooooo.....KEWL?  :)
     * @param view
     */
    public void AddMapUpdateYourLocation(View view)
    {
        //Warnings whine about this being protected but you can't get here without granting permission
        //i made that before the activity starts.
        mLocationManager.requestLocationUpdates("gps", 0, 0, this);
        LatLng point = new LatLng(GPS_location.getLatitude(),GPS_location.getLongitude());
        ZoomToSpot=true;
        this.onMapClick(point);


    }

    /**
     * Hold onto this sucker when they change location.  don't do anything with it unless they
     * choose too use it or whatever.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location)
    {

        GPS_location = location;


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {

    }

    @Override
    public void onProviderEnabled(String provider)
    {

    }

    @Override
    public void onProviderDisabled(String provider)
    {

    }
}
