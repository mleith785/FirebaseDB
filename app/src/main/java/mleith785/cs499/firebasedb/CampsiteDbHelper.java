package mleith785.cs499.firebasedb;

import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObservable;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;


public class CampsiteDbHelper extends SQLiteOpenHelper
{

    private static final int DB_VER = 1;
    private static final String DB_NAME = "Campsites.db";
    private static final String DB_PATH = "/data/data/markleitheiser.cs360.campsite/databases/";
    private final Context dbContext;
    private List<Campsite> CampsiteList;

    // constructor

    public CampsiteDbHelper(Context context, String name,
                            SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DB_NAME, factory, DB_VER);
        this.dbContext = context;
        //Stole this from
        //https://stackoverflow.com/questions/5627037/how-can-i-embed-an-sqlite-database-into-an-application
        createDatabaseFromAsset(false);


    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

    public void createDatabaseFromAsset(boolean force)
    {

        try
        {
            if (!checkDatabase() || force)
            {

                this.getReadableDatabase();
                copyDataBase();
                this.close();
            }
        }
        catch (IOException e)
        {
            throw new Error("Error Copying Database");
        }
    }

    private void copyDataBase() throws IOException
    {

        InputStream myInput = dbContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    private boolean checkDatabase() throws IOException
    {

        SQLiteDatabase checkDB = null;
        boolean exist = false;
        try
        {
            String dbPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(dbPath, null,
                    SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e)
        {
            Log.v("db log", "database does't exist");
        }

        if (checkDB != null)
        {
            exist = true;
            checkDB.close();
        }
        return exist;

    }

    public Campsite SearchCampsiteById(int id)
    {

        String query_str = CreateSearchStringById(id);
        return SearchCampsiteByQueryString(query_str);
    }

    public Campsite SearchCampsiteByName(String name)
    {

        String query_str = CreateSearchStringByName(name);
        return SearchCampsiteByQueryString(query_str);
    }


    private Campsite SearchCampsiteByQueryString(String query)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Campsite search_site = new Campsite();

        if (cursor.moveToFirst())
        {
            search_site.CampId = Integer.parseInt(cursor.getString(0));
            search_site.CampName = cursor.getString(1);
            search_site.CampCity = cursor.getString(2);
            search_site.CampLoc = cursor.getString(3);

            //Stole bitmap handling from here
            //https://stackoverflow.com/questions/15849843/get-blob-image-and-convert-that-image-into-bitmap-image
            byte[] byteArray = cursor.getBlob(4);
            if (null != byteArray )
            {
                search_site.BM = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }


            //Need to do these stupid binary operations to cast to a boolean
            search_site.FeatRiverside = (Integer.parseInt(cursor.getString(5)) > 0);
            search_site.FeatGrill = (Integer.parseInt(cursor.getString(6)) > 0);
            search_site.FeatRestroom = (Integer.parseInt(cursor.getString(7)) > 0);


            search_site.AvgRating = Float.parseFloat(cursor.getString(8));

            search_site.Details = cursor.getString(9);
            search_site.latitude = (Double.parseDouble(cursor.getString(10)));
            search_site.longitude = (Double.parseDouble(cursor.getString(11)));

        }
        else
        {
            search_site = null;
        }

        return search_site;
    }

    private String CreateSearchStringById(int id)
    {
        String query = "Select * FROM CAMPSITES WHERE CAMPSITEID = \"" + String.valueOf(id) + "\"";
        return query;
    }

    private String CreateSearchStringByName(String name)
    {
        String query = "Select * FROM CAMPSITES WHERE Name = \"" + name + "\"";
        return query;
    }

    public int GetDbNumRows()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "Campsites");

        /*
         * stole this from here
         * https://stackoverflow.com/questions/1590831/safely-casting-long-to-int-in-java
         */
        return toIntExact(count);
    }

    public void AddCampsite(Campsite site)
    {

        ContentValues values = new ContentValues();
        values.put("Name", site.CampName);
        values.put("City", site.CampCity);
        values.put("Location", site.CampLoc);
        values.put("FeatureRiverside", site.FeatRiverside);
        values.put("FeatureGrill", site.FeatGrill);
        values.put("FeatureRestroom", site.FeatRestroom);
        values.put("Avg_Rating", site.AvgRating);
        values.put("Details", site.Details);
        values.put("Lat",site.latitude);
        values.put("Long",site.longitude);
        if(null != site.BM)
        {
            //This is taken from
            //https://stackoverflow.com/questions/7331310/how-to-store-image-as-blob-in-sqlite-how-to-retrieve-it
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            site.BM.compress(Bitmap.CompressFormat.JPEG,100,baos);
            byte[] photo = baos.toByteArray();
            values.put("Picture",photo);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("Campsites", null, values);
        db.close();
    }

    public boolean DeleteCampsiteById(int id)
    {
        boolean result = false;
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("Campsites", "CampsiteId" + " = ?",
                new String[]{String.valueOf(id)});

        return result;

    }

    /**
     * GetFavoriteList - Get the currently logged in user and with that get the
     * favorites for that specific user.
     * @param mAuth - The user ID from the mAuth for google to lookup favorites
     * @return
     */
    public List<FavoriteListItem> GetFavoriteList(String mAuth)
    {
        List<FavoriteListItem> fav_list=null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select Campsites.Name, Campsites.City, Campsites.CampsiteId  from Campsites \n" +
                "inner join Favorites on Campsites.CampsiteId=Favorites.CampsiteId \n" +
                "where Favorites.mAuth=\"" + mAuth+"\"";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst())
        {
            fav_list = new ArrayList<>();
            do
            {
                String site_name = cursor.getString(0);
                String site_location = cursor.getString(1);
                int    site_id = cursor.getInt(2);
                FavoriteListItem new_item = new FavoriteListItem(site_name,site_location,site_id);
                fav_list.add(new_item);
            }while(cursor.moveToNext());
        }

        return fav_list;
    }

    public List<Integer> GetCampsiteIds()
    {
        List<Integer> campsite_id_list=null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "Select Campsites.CampsiteId from Campsites";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToNext())
        {
            campsite_id_list = new ArrayList<Integer>();
            do
            {
                campsite_id_list.add(cursor.getInt(0));
            } while(cursor.moveToNext());
        }

        return campsite_id_list;
    }

    /**
     * I am going to remove this one completely later.  The campsearchcriteria is tied in too tight
     * @param searchy
     */

//    public void UpdateCampsiteList(CampSearchCriteria searchy)
//    {
//
//        //Blank out campsite list, we're going to fill the whole thing here
//        CampsiteList = new ArrayList<>();
//        String query = "Select * FROM Campsites ";
//        if(null !=searchy)
//        {
//            String where_clause = searchy.createDbWhereClause();
//            //Now we see if they actually chose something, otherwise this is a blank search of all
//            if(0!=where_clause.compareTo(""))
//            {
//                //add where clause if they have it
//                query+=" where " + where_clause ;
//            }
//        }
//        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor.moveToFirst())
//        {
//            do
//            {
//
//                Campsite single_site = new Campsite();
//                single_site.CampId = Integer.parseInt(cursor.getString(0));
//                single_site.CampName = cursor.getString(1);
//                single_site.CampCity = cursor.getString(2);
//                single_site.CampLoc = cursor.getString(3);
//
//                //Skip index 4 which is the blob for a picture:  only need that on details
//
//                //Need to do these stupid binary operations to cast to a boolean
//                single_site.FeatRiverside = (Integer.parseInt(cursor.getString(5)) > 0);
//                single_site.FeatGrill = (Integer.parseInt(cursor.getString(6)) > 0);
//                single_site.FeatRestroom = (Integer.parseInt(cursor.getString(7)) > 0);
//
//
//                single_site.AvgRating = Float.parseFloat(cursor.getString(8));
//
//                single_site.Details = cursor.getString(9);
//                single_site.latitude = (Double.parseDouble(cursor.getString(10)));
//                single_site.longitude = (Double.parseDouble(cursor.getString(11)));
//                CampsiteList.add(single_site);
//
//
//            }
//            while(cursor.moveToNext());
//        }
//
//    }
//

    public List<Campsite> copyCampsiteList()
    {
        return new ArrayList<Campsite>(CampsiteList);
    }

    public float GetUserRatingById(int CampsiteId, String mAuth)
    {
        //negative 1 is going to be our magic flag for not found.  Would be better to use a bool flag
        //but I'm all outta time!  I suck....just make sure you compare it to less than 0 vs a
        //direct compare of -1 dummy for floating point precision errors.
        float userRating=-1.0f;
        String query = "Select UserRating FROM UserRating Where CampsiteId="+String.valueOf(CampsiteId) +
                " AND mAuth=\"" + mAuth + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst())
        {
            userRating = Float.parseFloat(cursor.getString(0));
        }

        return userRating;
    }

    public void SetDBUserRatingbyId(int site_id, boolean new_site, float user_rating, String mAuth)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        if(new_site)
        {
            ContentValues values = new ContentValues();
            values.put("CampsiteId",site_id);
            values.put("UserRating",user_rating);
            values.put("mAuth", mAuth);
            db.insert("UserRating", null, values);

        }
        else {
            //Update a current row with a new rating
            ContentValues values = new ContentValues();
            values.put("UserRating",String.valueOf(user_rating));

            db.update("UserRating", values,
                    "CampsiteId="+String.valueOf(site_id),null);
        }
        db.close();

    }

    public boolean GetCampsiteFavoriteById(int site_id, String mAuth)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //Not much to lookup here, if we detect the row, it is a fav.  If not, not favorite

        String query = "Select CampsiteId From Favorites where CampsiteId="+String.valueOf(site_id) +
                " AND mAuth=\"" + mAuth + "\"";
        Cursor cursor = db.rawQuery(query, null);
        boolean found_fav = cursor.moveToFirst();
        db.close();
        return found_fav;
    }


    public void UpdateCampsiteFavorite(int site_id, boolean fav, String mAuth)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        //Not much to lookup here, if we detect the row, it is a fav.  If not, not favorite

        if (fav)
        {
            //they checked the box, add a row into the table
            ContentValues values = new ContentValues();
            values.put("CampsiteId",site_id);
            values.put("mAuth",mAuth);
            db.insert("Favorites", null, values);

        }
        else
        {
            //they unchecked, delete the row from the DB
            String where_clause = "CampsiteId="+String.valueOf(site_id) + " AND mAuth=\""+mAuth+"\"";
            db.delete("Favorites", where_clause, null);
        }

        db.close();

    }

    /**
     * Going to delete this one later I think
     */
//    public boolean SearchForCampsites(CampSearchCriteria searchy)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        String where_clause = searchy.createDbWhereClause();
//        String query = "Select * From Campsites";
//
//        //Now we see if they actually chose something, otherwise this is a blank search of all
//        if(0!=where_clause.compareTo(""))
//        {
//            //If there is a where clause add that to the end
//            query+=" where " + where_clause ;
//        }
//        Cursor cursor = db.rawQuery(query, null);
//        boolean found_sites = cursor.moveToFirst();
//        db.close();
//        return found_sites;
//
//    }

}
