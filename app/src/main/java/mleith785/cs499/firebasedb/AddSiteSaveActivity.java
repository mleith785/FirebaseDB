package mleith785.cs499.firebasedb;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddSiteSaveActivity extends AppCompatActivity implements AddSiteDialogFrag.NoticeDialogListener
{
    private double Latitude;
    private double Longitude;
    private TextView AddCampsiteNameGui;
    private TextView CampAddLatGui;
    private TextView CampAddLongGui;

    private CheckBox RiversideAddGuiCB;
    private CheckBox GrillAddGuiCB;
    private CheckBox RestroomAddGuiCB;
    private EditText CampDetailEditGui;
    private ImageView CampImageAddGui;
    private Button AddSiteGuiBtn;
    private Button AddSitePictureButton;
    private Button AddSiteRotatePicButton;

    private static final int REQUEST_IMAGE_CAPTURE = 101;

    /**
     * <h1> onCreate</h1>
     * Make this grab the lat and lang of their chosen new campsite location.  Grab widget
     * references so that they can be used to grab their settings for the new site
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_site_save);

        //Get Data from the caller which was the nav map guy, they told us which camp ID
        // they chose:
        //Stolen from
        //https://www.thecrazyprogrammer.com/2016/12/pass-data-one-activity-another-in-android.html
        Intent intent = getIntent();
        AddSitePictureButton = findViewById(R.id.AddSitePictureButton);
        AddSiteRotatePicButton = findViewById(R.id.AddSiteRotatePicButton);
        Latitude = Double.parseDouble(intent.getStringExtra("laty"));
        Longitude = Double.parseDouble(intent.getStringExtra("longy"));

        //Get all the silly android widgets:
        CampAddLatGui = findViewById(R.id.CampAddLatGui);
        CampAddLongGui = findViewById(R.id.CampAddLongGui);
        AddCampsiteNameGui = findViewById(R.id.AddCampsiteNameGui);
        RiversideAddGuiCB = findViewById(R.id.RiversideAddGuiCB);
        GrillAddGuiCB = findViewById(R.id.GrillAddGuiCB);
        RestroomAddGuiCB = findViewById(R.id.RestroomAddGuiCB);
        CampDetailEditGui = findViewById(R.id.CampDetailEditGui);
        CampImageAddGui = findViewById(R.id.CampImageAddGui);
        AddSiteGuiBtn = findViewById(R.id.AddSiteGuiBtn);

        //Let's set the widgets to the lat and long values
        CampAddLatGui.setText(String.valueOf(Latitude));
        CampAddLongGui.setText(String.valueOf(Longitude));

        //Hide this at start, no picture avail
        AddSiteRotatePicButton.setVisibility(View.GONE);


    }

    /**
     * <h1> Take Picture</h1>
     * This launches the native camera activity.  On my emulator it rotates it by 90 so that
     * gets corrected later on when the activity closes with the matrix rotation
     * @param view
     */
    public void TakePictureOfSite(View view)
    {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageTakeIntent.resolveActivity(getPackageManager())!=null)
        {

            startActivityForResult(imageTakeIntent,REQUEST_IMAGE_CAPTURE);
        }

    }

    /**
     * <h1>Dialog result handler</h1>
     * This handles the result from the picture when taken of the campsite.  The
     * picture on this emulator has it rotated 90 degrees so I force the rotation back
     * so it shows up correctly on the imageview
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            CampImageAddGui.setImageBitmap(imageBitmap);
            AddSiteRotatePicButton.setVisibility(View.VISIBLE);

        }
    }

    /**
     * <h1>AddSiteToDb</h1>
     * They want to add the site.  Make sure they at least have a non blank name. Spawn dialog for
     * confirmation on this, if they choose it the callback will enter into the database.
     *
     * @param view
     */
    public void AddSiteToDb(View view)
    {
        //Ask if they really want to do this:

        //They think they want to add the site, but they gotta give it a name, so let's check
        //that first
        String campnamy = AddCampsiteNameGui.getText().toString();
        if (0 != campnamy.trim().length())
        {
            //K - there is a  name that ins't "" and not full of spaces, good enough for me
            //All of this is from the docs, see the AddSiteDialogFrag class for that...more confusing
            //than in bloody needs to be :(

            //Make a dialog, let the callbacks from said dialog handle the rest of writing to DB
            DialogFragment diag = new AddSiteDialogFrag();
            diag.show(getSupportFragmentManager(), "AddSiteDialogFrag");
        }
        else
        {
            //Now toast that they added this thing and hide the button
            int duration = Toast.LENGTH_SHORT;
            Context context = getApplicationContext();
            Toast toast = Toast.makeText(context, "You Must provide a Name!", duration);
            toast.show();
        }

    }


    /**
     * <h1>Doing callback from AddSiteDialogFrag </h1>
     * This is the most confusing way of doing anything in android...Getting a result from a dialog
     * into a class to not worry about the async nature....what a NIGHTMARE!!!!! No FREAKIN wonder
     * people like iOS so much if they don't have to do dumb stuff like this!
     *
     * @param dialog parameter from the callback
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {

        Campsite new_site = new Campsite();
        new_site.CampName = AddCampsiteNameGui.getText().toString();
        //todo this should be cleaned on this but I left it so the old code doesn't bust
        new_site.CampLoc = "dummy column";
        new_site.FeatRiverside = RiversideAddGuiCB.isChecked();
        new_site.FeatGrill = GrillAddGuiCB.isChecked();
        new_site.FeatRestroom = RestroomAddGuiCB.isChecked();
        new_site.Details = CampDetailEditGui.getText().toString();
        new_site.latitude = Latitude;
        new_site.longitude = Longitude;

        //Now let's try to convert the longitude and latitude to a city name
        //This code was borrowed from
        // https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
        // https://stackoverflow.com/questions/2296377/how-to-get-city-name-from-latitude-and-longitude-coordinates-in-google-maps
        Geocoder gcd = new Geocoder(this, Locale.getDefault());
        String city="";
        try
        {
            List<Address> list = gcd.getFromLocation(Latitude, Longitude,1);
            if (list != null & list.size() > 0)
            {
                Address address = list.get(0);
                city = address.getLocality();
            }
        }
        catch(IOException e)
        {
            city = "No City";
        }

        new_site.CampCity = city;

        //TODO need to fix
//        if (null != CampImageAddGui.getDrawable())
//        {
//
//            new_site.BM = ((BitmapDrawable)CampImageAddGui.getDrawable()).getBitmap();
//
//        }
//        else
//        {
//            new_site.BM = null;
//        }
//
//
//        //OK now write this to the database
//        CampsiteDbHelper dbHandler = new CampsiteDbHelper(this, null, null, 1);
//        dbHandler.AddCampsite(new_site);

        //Now toast that they added this thing and hide the button
        int duration = Toast.LENGTH_SHORT;
        Context context = getApplicationContext();
        Toast toast = Toast.makeText(context, "Campsite Added!", duration);
        toast.show();

        //Turn off some widgets since they just added the site
        AddSiteGuiBtn.setEnabled(false);
        AddCampsiteNameGui.setEnabled(false);
        CampDetailEditGui.setEnabled(false);
        RiversideAddGuiCB.setEnabled(false);
        GrillAddGuiCB.setEnabled(false);
        RestroomAddGuiCB.setEnabled(false);
        AddSitePictureButton.setEnabled(false);
    }

    /**
     * <h1> onDialogNegativeClick</h1>
     * Cancel dialog callback...probably not required
     *
     * @param dialog
     */
    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        //Nothing to do here...just implemented for the dialog mess thing

    }

    /**
     * OK so something annoying here with the emulator rotating the image when photo
     * captured.  Just let the user choose to do it because on my phone works totally fine.
     *
     * @param view
     */
    public void RotatePic(View view)
    {
        //got this idea from
        //https://stackoverflow.com/questions/26865787/get-bitmap-from-imageview-in-android-l
        if(null != CampImageAddGui.getDrawable())
        {
            BitmapDrawable drawable = (BitmapDrawable) CampImageAddGui.getDrawable();
            Bitmap imageBitmap = drawable.getBitmap();
            //The nutty emulator captures the picture rotated, so I am allowing it to rotate back
            //so it shows upright
            Matrix matrix = new Matrix();
            matrix.postRotate(90); // rotate this clockwise for the bitmap

            // create a new bitmap from the original using the matrix to transform the result
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0,
                    imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            CampImageAddGui.setImageBitmap(rotatedBitmap);

        }

    }
}
