package mleith785.cs499.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;



//!!!!this is the import you need!
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

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

        //NEED THIS AT start for image stuff
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        String image_paths[] =
        {
                "campsite_images/Clearly Lake.jpeg",
                "campsite_images/Como_park.jpeg",
                "campsite_images/Lebanon_hills_regional.jpeg",
                "campsite_images/Minneapolis_sw_koa.jpeg",
                "campsite_images/Phalen_regional.jpeg",
                "campsite_images/afton_state_park.jpeg",
                "campsite_images/baker_campground.jpeg",
                "campsite_images/father_hennepin_bluff.jpeg",
                "campsite_images/lake_auburn_campground.jpeg",
                "campsite_images/minnehaha_regional.jpeg",
                "campsite_images/town_country_rv.jpeg",
                "campsite_images/voyagers_campground.jpeg"
        };
        int index = 0;

        String path = image_paths[index];
        setImage(path);

    }



    private void setImage(String path)
    {
        //Taken from here
        //https://www.youtube.com/watch?v=iTSFJwsKPns

        StorageReference pathReference = storageReference.child(path);
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


    public void UploadBtn(View view)
    {
        final ProgressDialog pd = new ProgressDialog(this);
        //progress bar and other info from
        //https://www.youtube.com/watch?v=CQ5qcJetYAI
        pd.setTitle("Uploading Image...");
        pd.show();

        Bitmap BM;
        BM = ((BitmapDrawable)CampImageGui.getDrawable()).getBitmap();

        final String randomKey = UUID.randomUUID().toString();
        final String base_path = "campsite_images/";
        String path_to_write = base_path + randomKey+".jpeg";

        StorageReference riversRefRef = storageReference.child(path_to_write);

        //Taken from
        //https://firebase.google.com/docs/storage/android/upload-files
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BM.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = riversRefRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull @NotNull Exception e)
            {
                pd.dismiss();
                Log.w(TAG, "failed to upload picture");
                Toast.makeText(getApplicationContext(),"Failed to upload", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                pd.dismiss();
                Snackbar.make(findViewById(android.R.id.content),"Image Uploaded.",Snackbar.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(@NonNull @NotNull UploadTask.TaskSnapshot snapshot)
            {
                double progressPercent = (100.00* snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                pd.setMessage("Percentage: " + (int)progressPercent+"%");
            }
        });

    }
}