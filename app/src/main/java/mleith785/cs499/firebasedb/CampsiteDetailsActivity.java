package mleith785.cs499.firebasedb;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

import static android.content.ContentValues.TAG;

public class CampsiteDetailsActivity extends AppCompatActivity
        //Yuck...figured out this piece from the map piece cuz I hate annon inner class..gross
        implements RatingBar.OnRatingBarChangeListener
{

    private Campsite DisHereCampsite;
    private UserRating DisUserRating;
    private UserFavorite UserFavorite;
    private String UserRatingKey;
    private ImageView CampImageGui;
    private TextView CampDetailNameGui;
    private TextView CampDetailDetailGui;
    private TextView CampDetailFeaturesGui;
    private TextView CityText;
    private RatingBar CampDetailYourRatingBarGui;
    private TextView CampDetailAvgRatingGui;
    private CheckBox CampDetailFavoriteGui;
    private String UserFavoriteKey;

    private FirebaseAuth mAuth;

    private String Camp_Key;

    //Need these references to get the images from firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;





    /**
     * <h1>onCreate </h1>
     * Lots of work to do here.  Grab the key pair of what site they are interested in.  Use that to
     * update and get the values out of the database for the campsite of interest.  Need to query
     * the campsite table and the favorite table to get that value if they checked it.  Also put in
     * an image if they have that from the database.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campsite_details);
        mAuth = FirebaseAuth.getInstance();

        //Grab the anroid widgets
        CampDetailNameGui = findViewById(R.id.CampDetailNameGui);
        CampDetailDetailGui = findViewById(R.id.CampDetailDetailGui);
        CampDetailFeaturesGui = findViewById(R.id.CampDetailFeaturesGui);
        CampDetailAvgRatingGui = findViewById(R.id.CampDetailAvgRatingGui);
        CampDetailYourRatingBarGui = findViewById(R.id.CampDetailYourRatingBarGui);
        CampDetailFavoriteGui = findViewById(R.id.CampDetailFavoriteGui);
        CampImageGui = (ImageView) findViewById(R.id.CampImageGui);
        CityText = findViewById(R.id.CityText);

        CampDetailYourRatingBarGui.setOnRatingBarChangeListener(this);


        //Get Data from the caller which was the nav map guy, they told us which camp ID
        // they chose:
        //Stolen from
        //https://www.thecrazyprogrammer.com/2016/12/pass-data-one-activity-another-in-android.html
        Intent intent = getIntent();
        Camp_Key = intent.getStringExtra("theChosenId");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Send query with listener for the campsite data
        QueryCampOnId(Camp_Key);

        //Query for the user rating if one exists
        QueryUserRating(Camp_Key,mAuth.getUid());

        //Query if the user favorited this site.
        QueryUserFavorite(Camp_Key,mAuth.getUid());
    }

    private void QueryCampOnId(String camp_key)
    {
        //Now lets get the campsite and hold onto that into a global
        Query query = FirebaseDatabase.getInstance().getReference("Campsites").child(camp_key);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    DisHereCampsite = dataSnapshot.getValue(Campsite.class);
                    UpdateCampUIData();
                    setImage(DisHereCampsite.Picture_Storage);
                }
                else
                {
                    int duration = Toast.LENGTH_SHORT;
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, "No Campsites Found! Press Back", duration);
                    toast.show();
                }
            }

            @Override
            public void onCancelled (DatabaseError error)
            {
                //TODO filll this out with a toast
            }
        });
    }

    private void QueryUserRating(String camp_key,String uid)
    {
        //Now lets get the campsite and hold onto that into a global


        Query query = FirebaseDatabase.getInstance().getReference("UserRatings")
                .orderByChild("mAuth")
                .equalTo(uid)
                .limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener()
         {
             @Override
             public void onDataChange (DataSnapshot dataSnapshot)
             {
                 if (dataSnapshot.exists())
                 {
                     //We have data for the user, now check if it matches the camp key
                     for(DataSnapshot snapshot: dataSnapshot.getChildren())
                     {
                         DisUserRating = snapshot.getValue(UserRating.class);
                         UserRatingKey = snapshot.getKey();

                         //We have a site that matched auth/user, now see if the camp id matches
                         if( 0== DisUserRating.CampsiteKey.compareTo(camp_key))
                         {
                             UpdateCampUIData();
                         }
                         else
                         {
                             DisUserRating=null;
                             UserRatingKey=null;
                         }


                     }



                 }
                 else
                 {
                     //They never had a user rating so just set it to naught
                     DisUserRating = new UserRating(camp_key,(float)0.0,mAuth.getUid());
                     UserRatingKey=null;
                 }

             }

             @Override
             public void onCancelled (DatabaseError error)
             {
                 //TODO filll this out with a toast
             }
         });
    }

    private void QueryUserFavorite(String camp_key,String uid)
    {
        //Now lets get the campsite and hold onto that into a global


        Query query = FirebaseDatabase.getInstance().getReference("Favorites")
                .orderByChild("mAuth")
                .equalTo(uid)
                .limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot)
            {
                boolean checked=false;
                if (dataSnapshot.exists())
                {
                    //We found a record for this user, now check if it matches the camp_key
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        //If we made it here, this is a favorite for that user.
                        UserFavoriteKey = snapshot.getKey();
                        UserFavorite = snapshot.getValue(UserFavorite.class);

                        //We have a site that matched auth/user, now see if the camp id matches
                        if( 0== UserFavorite.CampsiteKey.compareTo(camp_key))
                        {
                            checked=true;
                        }
                        else
                        {
                            UserFavoriteKey=null;
                            UserFavorite=null;
                        }

                    }

                }
                else
                {
                    UserFavoriteKey = null;
                }

                CampDetailFavoriteGui.setChecked(checked);

            }

            @Override
            public void onCancelled (DatabaseError error)
            {
                //TODO filll this out with a toast
            }
        });
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


    private void UpdateCampUIData()
    {
        if (null != DisHereCampsite)
        {
            //Update those widgets from our campsite
            CampDetailNameGui.setText(DisHereCampsite.CampName);
            CampDetailDetailGui.setText(DisHereCampsite.Details);
            CampDetailFeaturesGui.setText(DisHereCampsite.ConvertFeatures2String());
            CampDetailAvgRatingGui.setText(String.valueOf(DisHereCampsite.AvgRating));
            CityText.setText(DisHereCampsite.CampCity);
        }

        if( null != DisUserRating)
        {
            CampDetailYourRatingBarGui.setRating(DisUserRating.Rating);
        }

    }

    /**
     * user wants to update their rating in the database.  Update
     * Or add a new one.
     * @param view
     */
    public void UpdateRatingDetailClick(View view)
    {
        //check if they have ever made a rating for this site before
        if(null == UserRatingKey)
        {
            //they have never done one so let's make a new key
            UserRatingKey = UUID.randomUUID().toString();
        }

        FirebaseDatabase.getInstance().getReference().child("UserRatings")
                .child(UserRatingKey).setValue(DisUserRating);

    }

    /**
     * <h1>onRatingChanged</h1>
     * They change the rating bar so update the database for their own rating
     *
     * @param ratingBar
     * @param rating
     * @param fromUser
     */
    public void onRatingChanged(RatingBar ratingBar, float rating,
                                boolean fromUser)
    {
        //update
        DisUserRating.Rating = rating;
    }


    /**
     * <h1>FavoriteCBClicked</h1>
     * they chose to favorite this site.  update the database for check uncheck.  Favorites are in a
     * separate table with primary key of the campsite id
     *
     * @param view
     */
    public void FavoriteCBClicked(View view)
    {
        //Check if a favorite existed

        if ( CampDetailFavoriteGui.isChecked())
        {
            //There should not be a record in the DB for this, but check anyway
            if (null == UserFavoriteKey)
            {
                //nope, they need a new key
                UserFavoriteKey = UUID.randomUUID().toString();
                UserFavorite = new UserFavorite(Camp_Key, mAuth.getUid());
            }

            //write the DB, if they had one it would just replace the one already up there
            FirebaseDatabase.getInstance().getReference().child("Favorites")
                    .child(UserFavoriteKey).setValue(UserFavorite);

        }
        else
        {
            //There should be a record in the DB for this.  Let's check
            if (null != UserFavoriteKey)
            {
                //yes, delete the document record
                Query query = FirebaseDatabase.getInstance().getReference("Favorites").child(UserFavoriteKey);

                query.addListenerForSingleValueEvent(new ValueEventListener(){
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().removeValue();
                        UserFavoriteKey=null;
                        UserFavorite=null;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled", databaseError.toException());
                    }
                });

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2)
        {
            String message=data.getStringExtra("MESSAGE");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) ) {
            Log.d(this.getClass().getName(), "back button pressed");
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK);
            finish();
        }
        return true;
    }
}
