package mleith785.cs499.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;


//!!!! imported to try running database
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;


public class Campsite_DB_Test extends AppCompatActivity
{



    private TextView CampsiteIdGui;
    private EditText CampsiteNameGui;
    private EditText CampsiteCityGui;
    private EditText CampsiteLocationGui;
    private EditText CampsiteAvgRatingGui;
    private CheckBox RiversideCBGui;
    private CheckBox GrillCBGui;
    private CheckBox RestroomsCBGui;
    private EditText CampsiteDetailsGui;



    private Spinner RatingDropdown;
    private String[] items;
    private List<Integer> CampsiteIds;
    private int CampsiteIndex;


    //ALL THE CHANGES HAPPEN HERE FOR CS499
    private static final boolean DEBUG_UPDATE = false;


    private List<Campsite> CampsiteList;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    /**
     * <h1> Create this activity and grab hold of the widgets.</h1>
     * Populate the spinner with some values
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite_db_test);
        /* Stolen from
           https://stackoverflow.com/questions/13377361/how-to-create-a-drop-down-list
         */
        RatingDropdown = findViewById(R.id.spinnerRating);
        items = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, items);
        RatingDropdown.setAdapter(adapter);

        //Grab the remaining widgets
        CampsiteIdGui = findViewById(R.id.CampsiteIdGui);
        CampsiteNameGui = findViewById(R.id.CampsiteNameGui);
        CampsiteCityGui = findViewById(R.id.CampsiteCityGui);
        CampsiteLocationGui = findViewById(R.id.CampsiteLocationGui);
        CampsiteAvgRatingGui = findViewById(R.id.CampsiteAvgRatingGui);
        RiversideCBGui = findViewById(R.id.RiversideCB);
        GrillCBGui = findViewById(R.id.GrillCB);
        RestroomsCBGui = findViewById(R.id.RestroomsCB);
        CampsiteDetailsGui = findViewById(R.id.CampsiteDetailsGui);

        if(DEBUG_UPDATE)
            UpdateCampsiteIds();

        CampsiteList = new ArrayList();
        CampsiteIds = new ArrayList();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Campsites");
        Query query = FirebaseDatabase.getInstance().getReference("Campsites")
                .orderByChild("CampId");

        myRef.addValueEventListener(GetNumCampsites);


    }

    ValueEventListener GetNumCampsites = new ValueEventListener()        {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.exists()){
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Campsite single_site = snapshot.getValue(Campsite.class);
                    CampsiteIds.add(single_site.CampId);
                }
            }
//                Log.d(TAG, "Value is: " + value);

        }

        @Override
        public void onCancelled(DatabaseError error)
        {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };

    ValueEventListener valueEventListener = new ValueEventListener()        {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {
                if (dataSnapshot.exists()){
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        Campsite single_site = snapshot.getValue(Campsite.class);
//                        CampsiteList.add(single_site);
                        PopulateSearchResults(single_site);
                    }
                }
//                Log.d(TAG, "Value is: " + value);

        }

        @Override
        public void onCancelled(DatabaseError error)
        {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException());
        }
    };
//
//    public void GoToFavorites(View view)
//    {
//        Intent intent = new Intent(this, FavoritesActivity.class);
//        startActivity(intent);
//
//    }

    public void SearchByName(View view)
    {
        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
        String camp_name = CampsiteNameGui.getText().toString();
        Campsite site_q = dbHandler.SearchCampsiteByName(camp_name);
        PopulateSearchResults(site_q);
    }

    public void CreateDBBtn(View view)
    {
//        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
//        dbHandler.createDatabaseFromAsset(true);
//        UpdateCampsiteIds();

    }

    public void BtnPrevGui(View view)
    {

        String campsite_id_text = CampsiteIdGui.getText().toString();
        if (0 != campsite_id_text.compareTo("Auto Assigned"))
        {
            if (CampsiteIndex != 0)
            {
                CampsiteIndex--;
            }
        }
        else
        {
            CampsiteIndex=CampsiteIds.size()-1;
        }

//This works
//        Query query = FirebaseDatabase.getInstance().getReference("Campsites")
//                .orderByChild("CampCity").equalTo("Prior Lake");

        Query query = FirebaseDatabase.getInstance().getReference("Campsites")
                .orderByChild("CampId").equalTo(CampsiteIds.get(CampsiteIndex));

        query.addListenerForSingleValueEvent(valueEventListener);

    }

    public void BtnNextGui(View view)
    {

        String campsite_id_text = CampsiteIdGui.getText().toString();
        if (0 != campsite_id_text.compareTo("Auto Assigned"))
        {
            if (CampsiteIndex < CampsiteIds.size() - 1)
            {
                CampsiteIndex++;
            }
        }
        else
        {
            CampsiteIndex = 0;
        }

        int campsite_id = CampsiteIds.get(CampsiteIndex);
        Query query = FirebaseDatabase.getInstance().getReference("Campsites")
                .orderByChild("CampId").equalTo(CampsiteIds.get(CampsiteIndex));

        query.addListenerForSingleValueEvent(valueEventListener);
        //SearchCampsiteById(campsite_id);
    }


    private void SearchCampsiteById(int id)
    {
        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
        Campsite site_q = dbHandler.SearchCampsiteById(id);
        PopulateSearchResults(site_q);

    }


    private void PopulateSearchResults(Campsite campsite)
    {
        if (null != campsite)
        {
            CampsiteIdGui.setText(String.valueOf(campsite.CampId));
            CampsiteNameGui.setText(campsite.CampName);
            CampsiteCityGui.setText(campsite.CampCity);
            CampsiteLocationGui.setText(campsite.CampLoc);
            CampsiteAvgRatingGui.setText(String.valueOf(campsite.AvgRating));
            RiversideCBGui.setChecked(campsite.FeatRiverside);
            GrillCBGui.setChecked(campsite.FeatGrill);
            RestroomsCBGui.setChecked(campsite.FeatRestroom);
            RatingDropdown.setSelection(2); //test to see if this comes out as 1
            CampsiteDetailsGui.setText(campsite.Details);

        }
        else
        {
            //Blank out values
            CampsiteIdGui.setText("Auto Assigned");
            CampsiteNameGui.setText("");
            CampsiteCityGui.setText("");
            CampsiteLocationGui.setText("");
            CampsiteAvgRatingGui.setText("");
            RiversideCBGui.setChecked(false);
            GrillCBGui.setChecked(false);
            RestroomsCBGui.setChecked(false);
            RatingDropdown.setSelection(4);//test to see if this comes out as 3
            CampsiteDetailsGui.setText("");
        }

    }

    public void AddRecord(View view)
    {
        Campsite new_site = new Campsite(0,
                CampsiteNameGui.getText().toString(),
                CampsiteCityGui.getText().toString(),
                CampsiteLocationGui.getText().toString(),
                (float) 0.0,
                RiversideCBGui.isChecked(),
                GrillCBGui.isChecked(),
                RestroomsCBGui.isChecked(),
                CampsiteDetailsGui.getText().toString(),
                3.0f,4.0f
                );
//        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
//        dbHandler.AddCampsite(new_site);
//        UpdateCampsiteIds();

        //Firebase get a key for the data
        //get the key for the campsite
        myRef = database.getReference("Campsites");
        String new_add_id = myRef.push().getKey();
        //Now write to the database
        myRef.child(new_add_id).setValue(new_site);
        Toast.makeText(this, "campsite added", Toast.LENGTH_LONG).show();



    }

    public void DeleteRecord(View view)
    {
        boolean result;
        String id;
        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);

        id = CampsiteIdGui.getText().toString();

        if (0 != id.compareTo("Auto Assigned"))
        {

            result = dbHandler.DeleteCampsiteById(Integer.parseInt(id));
            UpdateCampsiteIds();

        }
    }

    private void UpdateCampsiteIds()
    {
        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
        CampsiteIds = dbHandler.GetCampsiteIds();
        PopulateSearchResults(null);
    }

}
