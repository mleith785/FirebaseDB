package mleith785.cs499.firebasedb;



import androidx.fragment.app.FragmentActivity;


import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MapListNavActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener
{

    private GoogleMap mMap;
    //Use this to get the list from the DB handler
    private List<CampsiteMarkersKeys> CampsiteListWithMarkersAndKeys;

    private TextView CampNameNavMapGui;
    private TextView CampNameCityMapGui;
    private TextView CampLatMapGui;
    private TextView CampLongMapGui;
    private TextView CampAverageRatingMapGui;
    private TextView CampFeatureNavMapGui;
    private TextView CampDetailsNavMapGui;
    private Marker LastChosenMarker;
    private CampSearchCriteria CampCriteria;
    /**
     * <h1>Private class only used here for managing sites</h1>
     * Needed to create a new class to contain the campsites with marker objects to navigate through
     * the class
     */
    private class CampsiteMarkersKeys
    {
        public Campsite site;
        public Marker camp_marker;
        public String camp_key;

        public CampsiteMarkersKeys(Campsite site, Marker camp_marker, String camp_key)
        {
            this.site = site;
            this.camp_marker = camp_marker;
            this.camp_key = camp_key;
        }

    }

    /**
     * <h1> onCreate for the Map</h1>
     * Need to grab widgets here...standard android business
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_list_nav);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Grab the remaining widgets
        CampNameNavMapGui = findViewById(R.id.CampNameNavMapGui);
        CampNameCityMapGui = findViewById(R.id.CampNameCityMapGui);
        CampLatMapGui = findViewById(R.id.CampLatMapGui);
        CampLongMapGui = findViewById(R.id.CampLongMapGui);
        CampAverageRatingMapGui = findViewById(R.id.CampAverageRatingMapGui);
        CampFeatureNavMapGui = findViewById(R.id.CampFeatureNavMapGui);
        CampDetailsNavMapGui = findViewById(R.id.CampDetailsNavMapGui);
        CampsiteListWithMarkersAndKeys = new ArrayList<>();


    }


    /**
     * <h1> onMapReady for the activity</h1>
     * This is where the magic happens for maps.  We need to set the original location and then set
     * the markers by sucking this out of the database for lat lang.  Then we set the listener for
     * someone clicking the map.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        mMap = googleMap;
        String CityName;

        //This can be called by favorites or by list, let's look for that
        Intent i = getIntent();
        String city_name;
        if (0==i.getStringExtra("Called By").compareTo("search"))
        {
            //This activity passes us a search object, so lets get that
            //This serializable bidness taken from
            //https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
            CampCriteria = (CampSearchCriteria)i.getSerializableExtra("CampSearchObj");
            query_by_search_criteria(CampCriteria);

        }
        else
        {
            //we were called by the search activity.
            //This activity passes us a search object, so lets get that
            //This serializable bidness taken from
            //https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
            CityName = (String)i.getSerializableExtra("CityStr");
            query_by_city(CityName);

        }
        // Move to the default TC-612 area
        LatLng Twin_Cities = new LatLng(44.978905, -93.2658082);

        //Zoom this thing to TC metro.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Twin_Cities, 9.0f));

        //Set some callbacks, must go here vs on create
        mMap.setOnMarkerClickListener(this);

    }


    public void query_by_city(String CityName)
    {
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
                        Campsite single_site = snapshot.getValue(Campsite.class);
                        String site_key = snapshot.getKey();
                        addMapPins(single_site,site_key);
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
                //TODO filll this out with a toast
            }
        });
    }

    public void query_by_search_criteria(CampSearchCriteria searchy)
    {
        Query query = FirebaseDatabase.getInstance().getReference("Campsites");

        query.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if( dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Campsite site = snapshot.getValue(Campsite.class);
                        if(
                                (0==searchy.getCampName().compareTo("") ||
                                    0==searchy.getCampName().compareTo(site.CampName)) &&
                                (0==searchy.getCampCity().compareTo("") ||
                                        0==searchy.getCampCity().compareTo(site.CampCity)) &&
                                ((searchy.isFeatureGrill() && site.FeatGrill ||!searchy.isFeatureGrill()) &&
                                        (searchy.isFeatureRestroom() && site.FeatRestroom || !searchy.isFeatureRestroom()) &&
                                        (searchy.isFeatureRiverside() && site.FeatRiverside) || !searchy.isFeatureRiverside())
                        )
                        {

                            String site_key = snapshot.getKey();
                            addMapPins(site, site_key);
                        }
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
                //TODO filll this out with a toast
            }
        });
    }


    public void addMapPins(Campsite site, String site_key)
    {
        LatLng site_pin = new LatLng(site.latitude, site.longitude);
        String site_name = site.CampName;
        Marker mac = mMap.addMarker(new MarkerOptions().position(site_pin).title(site_name));

        //Add this to our list of campsites with markers to use on the widgets
        CampsiteListWithMarkersAndKeys.add(new CampsiteMarkersKeys(site, mac,site_key));

        //Set some callbacks, must go here vs on create
        mMap.setOnMarkerClickListener(this);

    }


    /**
     * Called when the user clicks a marker.
     */
    @Override
    public boolean onMarkerClick(final Marker marker)
    {

        //find the site associated with this marker
        for (CampsiteMarkersKeys site : CampsiteListWithMarkersAndKeys)
        {
            if (0 == marker.getId().compareTo(site.camp_marker.getId()))
            {
                updateUiWidgets(site.site);
                break;
            }
        }
        LastChosenMarker = marker;
        //This is stolen from google example....not sure what they are getting at with the return
        //of false but.....whatever.
        //Stolen from
        // https://developers.google.com/maps/documentation/android-sdk/marker
        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }


    /**
     * <h1> Update widgets for this site</h1>
     * If someone clicked a pin or goes back next this is a helper function to update all the text
     * on the screen for details on that site.
     *
     * @param site
     */
    private void updateUiWidgets(Campsite site)
    {
        CampNameNavMapGui.setText(site.CampName);
        CampNameCityMapGui.setText(site.CampCity);
        CampLatMapGui.setText(String.valueOf(site.latitude));
        CampLongMapGui.setText(String.valueOf(site.longitude));
        CampAverageRatingMapGui.setText(String.valueOf(site.AvgRating));


        CampFeatureNavMapGui.setText(site.ConvertFeatures2String());
        CampDetailsNavMapGui.setText(site.Details);
    }

    /**
     * <h1>goToSiteActivity</h1>
     * They click the button to go to a site, make sure a site was chosen and then launch that
     * activity.
     *
     * @param view
     */
    public void goToSiteActivity(View view)
    {
        boolean passed = false;
        //first lets see if they picked something
        if (null != LastChosenMarker)
        {
            //yup, now we need to pass the info for the current campsite
            Intent intent = new Intent(this, CampsiteDetailsActivity.class);
            //Stole data sharing from this
            //https://www.thecrazyprogrammer.com/2016/12/pass-data-one-activity-another-in-android.html

            String camp_key = "";
            for (CampsiteMarkersKeys site : CampsiteListWithMarkersAndKeys)
            {
                //Does the marker match the campsite in a list?
                if (0 == LastChosenMarker.getId().compareTo(site.camp_marker.getId()))
                {

                    camp_key = site.camp_key;
                    break;
                }
            }
            //Stole data sharing from this
            //https://www.thecrazyprogrammer.com/2016/12/pass-data-one-activity-another-in-android.html
            if (0 != camp_key.compareTo(""))
            {

                intent.putExtra("theChosenId", camp_key);
                startActivity(intent);
                passed = true;
            }

        }

        if (!passed)
        {
            Context context = getApplicationContext();
            CharSequence text = "Please Choose a Site";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }


    /**
     * <h1>PreviousBtnClick</h1>
     * go through the list of campsites.  If they never picked something just start at the end.
     * List nav does not auto wrap
     *
     * @param view
     */
    public void PreviousBtnClick(View view)
    {

        int camp_index = 0;
        boolean site_found = false;
        //They want to go back a site so lets go through the list
        if (null == LastChosenMarker)
        {
            //They never chose a site, that is fine, we just start at the end.  OK to start
            //with size being index +1 since we decrement upon the discovery of the site
            camp_index = CampsiteListWithMarkersAndKeys.size();
            if (camp_index > 0)
            {
                CampsiteMarkersKeys new_site = CampsiteListWithMarkersAndKeys.get(camp_index-1);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_site.camp_marker.getPosition(),
                        16));

                LastChosenMarker = new_site.camp_marker;
                //Now we need to update our widgets
                updateUiWidgets(new_site.site);

            }
        }
        else
        {
            for (CampsiteMarkersKeys site_mark : CampsiteListWithMarkersAndKeys)
            {
                if (0 == site_mark.camp_marker.getId().compareTo(LastChosenMarker.getId()))
                {
                    site_found = true;

                    break;
                }
                camp_index++;
            }


        }

        //Now we test for the site being found that we are at
        if (site_found)
        {
            //protect going negative on the list
            if (camp_index > 0)
            {
                //there is room to move to the next site, do that
                camp_index--;
                CampsiteMarkersKeys new_site = CampsiteListWithMarkersAndKeys.get(camp_index);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_site.camp_marker.getPosition(),
                        16));

                LastChosenMarker = new_site.camp_marker;
                //Now we need to update our widgets
                updateUiWidgets(new_site.site);
            }
        }

    }


    /**
     * <h1>NextBtnClick</h1>
     * go through the list of campsites.  If they never picked something just start at the
     * beginning.  List nav does not auto wrap
     *
     * @param view
     */
    public void NextBtnClick(View view)
    {
        int camp_index = 0;
        CampsiteMarkersKeys the_current_site_mark = null;
        //They want to go to the next site so we're going to go through the list
        if (null == LastChosenMarker)
        {
            //They never chose a campsite, that is fine, we just start at the beginning
            //They should always have 1 site but just in case
            if(CampsiteListWithMarkersAndKeys.size() > 0)
            {
                the_current_site_mark = CampsiteListWithMarkersAndKeys.get(0);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(the_current_site_mark.camp_marker.getPosition(),
                        16));

                LastChosenMarker = the_current_site_mark.camp_marker;
                //Now we need to update our widgets
                updateUiWidgets(the_current_site_mark.site);
            }


        }
        else
        {
            //Find the next campsite, no autowrap
            for (CampsiteMarkersKeys site_mark : CampsiteListWithMarkersAndKeys)
            {
                if (0 == site_mark.camp_marker.getId().compareTo(LastChosenMarker.getId()))
                {
                    the_current_site_mark = site_mark;

                    break;
                }
                camp_index++;
            }


            //Now we test for the site being found that we are at
            if (null != the_current_site_mark)
            {
                //Move onto the next one if we have it
                if (camp_index < CampsiteListWithMarkersAndKeys.size() - 1)
                {
                    //there is room to move to the next site, do that
                    camp_index++;
                    CampsiteMarkersKeys new_site = CampsiteListWithMarkersAndKeys.get(camp_index);

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new_site.camp_marker.getPosition(),
                            16));

                    LastChosenMarker = new_site.camp_marker;
                    //Now we need to update our widgets
                    updateUiWidgets(new_site.site);

                }
            }
        }

    }
}
