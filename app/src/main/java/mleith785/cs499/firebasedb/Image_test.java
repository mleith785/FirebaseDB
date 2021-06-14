package mleith785.cs499.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

//!!!!this is the import you need!
import com.google.firebase.storage.StorageReference;

import static android.content.ContentValues.TAG;

public class Image_test extends AppCompatActivity
{

    private int ThisCampId;
    private Campsite DisHereCampsite;
    private ImageView CampImageGui;
    private TextView CampDetailNameGui;
    private TextView CampDetailDetailGui;
    private TextView CampDetailFeaturesGui;
    private TextView CityText;
    private RatingBar CampDetailYourRatingBarGui;
    private TextView CampDetailAvgRatingGui;
    private CheckBox CampDetailFavoriteGui;
    private float UsersRating;
    private boolean UserRatingExists;
    private boolean UserFavorite;
    private boolean UserFavoriteExists;
    private FirebaseAuth mAuth;

    //!!!! things needed
    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_test);
        // Create a Cloud Storage reference from the app

        CampDetailNameGui = findViewById(R.id.CampDetailNameGui);
        CampDetailDetailGui = findViewById(R.id.CampDetailDetailGui);
        CampDetailFeaturesGui = findViewById(R.id.CampDetailFeaturesGui);
        CampDetailAvgRatingGui = findViewById(R.id.CampDetailAvgRatingGui);
        CampDetailYourRatingBarGui = findViewById(R.id.CampDetailYourRatingBarGui);
        CampDetailFavoriteGui = findViewById(R.id.CampDetailFavoriteGui);
        CampImageGui = (ImageView) findViewById(R.id.imCampImageGui);
        CityText = findViewById(R.id.CityText);

        DisHereCampsite = new Campsite();

        //Gpt the site, let's see if this thing has an image on it, if it does populate the widget
        //with that.
        if (null != DisHereCampsite.BM)
        {
            CampImageGui.setImageBitmap(DisHereCampsite.BM);
        }

        //Update those widgets from our campsite
        CampDetailNameGui.setText(DisHereCampsite.CampName);
        CampDetailDetailGui.setText(DisHereCampsite.Details);
        CampDetailFeaturesGui.setText(DisHereCampsite.ConvertFeatures2String());
        CampDetailAvgRatingGui.setText(String.valueOf(DisHereCampsite.AvgRating));
        CityText.setText(DisHereCampsite.CampCity);


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String path = "temp";
        setImage(path);

    }



    private void setImage(String path)
    {
        //Taken from here
        //https://www.youtube.com/watch?v=iTSFJwsKPns

        StorageReference pathReference = storageReference.child("campsite_images/Como_park.jpeg");
        long MAXBYTES = 1024*1024;
        pathReference.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>()
        {
            @Override
            public void onSuccess(byte[] bytes)
            {
                //convert bytes to bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                CampImageGui.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull @org.jetbrains.annotations.NotNull Exception e)
            {
                Log.w(TAG, "failed to read picture");
            }
        });
    }
}