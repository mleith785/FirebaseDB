package mleith785.cs499.firebasedb;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/*
 * Stole the click listener on recycler from here
 * https://www.youtube.com/watch?v=dmIfFIHnKsk&list=PLk7v1Z2rk4hjHrGKo9GqOtLs1e2bglHHA&index=8
 *
 */


public class FavoritesActivity extends AppCompatActivity implements FavoriteAdapter.ItemClickListener
{

    private RecyclerView recyclerView;
    //private RecyclerView.Adapter adapter;
    private FavoriteAdapter adapter;
    private List<FavoriteListItem> FavList;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private FirebaseAuth mAuth;

    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        mAuth = FirebaseAuth.getInstance();

        recyclerView = (RecyclerView) findViewById(R.id.RecyclerViewGui);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FavList = new ArrayList<FavoriteListItem>();


        QueryUserFavorites(mAuth.getUid());

        //This code is common on the creation as well as when the details activity is exited
        //which is called from this function
//TODO move this elsewhere
//        UpdateRecyclerHelper();

    }

    private void QueryUserFavorites(String uid)
    {
        //Now lets get the campsite and hold onto that into a global
        Query query = FirebaseDatabase.getInstance().getReference("Favorites")
                .orderByChild("mAuth")
                .equalTo(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange (DataSnapshot dataSnapshot)
            {
                FavList.clear();
                if (dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {

                        FavoriteListItem item = snapshot.getValue(FavoriteListItem.class);
                        FavList.add(item);
                    }
                    UpdateRecyclerHelper();
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



    /**
     * <h1>Helper function for updating screen</h1>
     * This is a helper function for adding the campsites to the recyclerview as well
     * as setting the onclick listener for a new site.  This is done at start of the activity
     * and can be called when they leave the details activity in case they removed a site from
     * favorites.
     */
    private void UpdateRecyclerHelper()
    {
        adapter = new FavoriteAdapter(FavList,this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }


    /**
     * <h1> Recycler View Click Result</h1>
     * This is one of those super complicated android things using the recycler view.  This
     * will toggle when someone clicks an item on the list.  We get the campsite ID of what
     * they chose and start that activity for result.  The result is handled here in this
     * class.
     * @param view
     * @param position
     * @param campsite_key
     */
    //The crazy custom onclick handling was taken from here
    //https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
    @Override
    public void onItemClick(View view, int position,String campsite_key) {
        //Create an activity for the details and pass the ID of the campsite to that
        // activity
        Intent intent = new Intent(this, CampsiteDetailsActivity.class);
        //Pass the id of what they chose from the favorites list to the activity
        intent.putExtra("theChosenId", campsite_key);

        //In this case, we launch the detail activity looking for a result.  Basically I want
        //to detect if they press the back button so that we can refresh the favorites list.
        //The reason to do this is that they may remove a site from their favorites so you want
        //the list to reflect that.
        startActivityForResult(intent,LAUNCH_SECOND_ACTIVITY);
    }


    /**
     * <h1> Handles closure of details activity</h1>
     * When the details activity closes/finishes, this will be called.  When that
     * happens we need to update this activity since they could have potentially
     * removed a site from their favorites.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                //They hit the backbutton, we have to update our recycler view in case they
                //unchecked a favorite.
                UpdateRecyclerHelper();

            }

        }
    }

}