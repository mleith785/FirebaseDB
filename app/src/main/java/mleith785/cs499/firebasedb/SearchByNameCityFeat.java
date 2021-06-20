package mleith785.cs499.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchByNameCityFeat extends AppCompatActivity
{

    private EditText CampSearchNameGui;
    private EditText CampSearchCityGui;
    private CheckBox CampSearchRiversideCBGui;
    private CheckBox CampSearchGrillCBGui;
    private CheckBox CampSearchRestroomsGui;
    private List<String> CampsiteKeys;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_by_name_city_feat);
        CampsiteKeys = new ArrayList<String>();

        CampSearchNameGui          = findViewById(R.id.CampSearchNameGui);
        CampSearchCityGui          = findViewById(R.id.CampSearchCityGui);
        CampSearchRiversideCBGui   = findViewById(R.id.CampSearchRiversideCBGui);
        CampSearchGrillCBGui       = findViewById(R.id.CampSearchGrillCBGui);
        CampSearchRestroomsGui     = findViewById(R.id.CampSearchRestroomsGui);
    }

    public void SearchForSites(View view)
    {
        //let's make an object for the database handler
        CampSearchCriteria searchy=new CampSearchCriteria(
                CampSearchNameGui.getText().toString().trim(),
                CampSearchCityGui.getText().toString().trim(),
                CampSearchGrillCBGui.isChecked(),
                CampSearchRiversideCBGui.isChecked(),
                CampSearchRestroomsGui.isChecked());

        searchForSite(searchy);

    }


    public void searchForSite(CampSearchCriteria searchy)
    {

        //Add the search criteria using firebase
        Query query = FirebaseDatabase.getInstance().getReference("Campsites");

        query.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                CampsiteKeys.clear();
                if( dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        //This is all the campsites since we cannot query for specific values
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
                            //This is a campsite being searched for
                            CampsiteKeys.add(snapshot.getKey());

                        }

                    }

                    if(CampsiteKeys.isEmpty())
                    {
                        int duration = Toast.LENGTH_SHORT;
                        Context context = getApplicationContext();
                        Toast toast = Toast.makeText(context, "No Campsites Found!", duration);
                        toast.show();
                    }
                    else
                    {
                        Intent intent = new Intent(getApplicationContext(), MapListNavActivity.class);
                        intent.putExtra("Called By", "search");
                        intent.putExtra("CampSearchObj", searchy);
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
                int duration = Toast.LENGTH_SHORT;
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, "Error Reading Firebase", duration);
                toast.show();
            }
        });

    }
}
