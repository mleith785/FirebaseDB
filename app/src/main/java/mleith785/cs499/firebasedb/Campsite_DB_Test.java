package mleith785.cs499.firebasedb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

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
    private static final boolean DEBUG_UPDATE = false;

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


    }
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

        int campsite_id = CampsiteIds.get(CampsiteIndex);
        SearchCampsiteById(campsite_id);


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
        SearchCampsiteById(campsite_id);
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
                CampsiteDetailsGui.getText().toString());
        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
        dbHandler.AddCampsite(new_site);
        UpdateCampsiteIds();


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
