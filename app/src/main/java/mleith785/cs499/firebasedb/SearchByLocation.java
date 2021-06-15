package mleith785.cs499.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

//All of this crazy location stuff I stole from all over stack overflow n stuff.
// https://stackoverflow.com/questions/33865445/gps-location-provider-requires-access-fine-location-permission-for-android-6-0
// https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
// https://stackoverflow.com/questions/5314155/get-current-position-location-android
// https://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android

public class SearchByLocation extends AppCompatActivity  implements LocationListener, OnMapReadyCallback
{

    private LocationManager mLocationManager;
    private Location CurrentLoc;
    private GoogleMap mMap;
    private Marker LastMarker;
    private String CityName;
    private TextView CampNameCityMapGui;
    private TextView CampSearchLocLatGui;
    private TextView CampSearchLocLongGui;

    private FirebaseDatabase database;
    private DatabaseReference myRef;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        CityName="";

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        CampNameCityMapGui =findViewById(R.id.CampNameCityMapGui);
        CampSearchLocLatGui = findViewById(R.id.CampSearchLocLatGui);
        CampSearchLocLongGui = findViewById(R.id.CampSearchLocLongGui);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED )
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
    }




    @Override
    public void onLocationChanged(Location location)
    {
        CurrentLoc=location;
        // Move to their location
        LatLng their_new_loc = new LatLng(location.getLatitude(),location.getLongitude());

        if(mMap!=null)
        {
            //Zoom this thing to their location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(their_new_loc, 9.0f));
            if(LastMarker!=null)
            {
                LastMarker.remove();
            }
            LastMarker = mMap.addMarker(new MarkerOptions().position(their_new_loc).title("Current Location"));

            Geocoder gcd = new Geocoder(this, Locale.getDefault());
            //Now let's get the address based on their location or something
            try
            {
                List<Address> list = gcd.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                if (list != null & list.size() > 0)
                {
                    Address address = list.get(0);
                    CityName = address.getLocality();
                }
            }
            catch(IOException e)
            {
                CityName = "";
            }
            UpdateUiItems();


        }

    }

    private void UpdateUiItems()
    {
        CampNameCityMapGui.setText(CityName);
        CampSearchLocLatGui.setText(String.valueOf(CurrentLoc.getLatitude()));
        CampSearchLocLongGui.setText(String.valueOf(CurrentLoc.getLongitude()));

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

    public void searchSiteLocation(View view)
    {
        //Add the search criteria using firebase
        Query query = FirebaseDatabase.getInstance().getReference("Campsites")
                .orderByChild("CampCity")
                .equalTo(CityName);

        query.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if( dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Intent intent = new Intent(getApplicationContext(), MapListNavActivity.class);
                        intent.putExtra("Called By", "search");
                        //TODO need to make this work better too

                        //intent.putExtra("CampSearchObj", searchy);
                        startActivity(intent);
                    }


                }
                else
                {
                    int duration = Toast.LENGTH_SHORT;
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "No Campsites Found!", duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error)
            {

            }
        });

    }
}
