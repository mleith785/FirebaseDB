package mleith785.cs499.firebasedb;

import android.graphics.Bitmap;

import java.sql.Blob;

public class Campsite
{
    public int CampId;
    public String CampName;
    public String CampCity;
    public String CampLoc;
    public float AvgRating;
    public boolean FeatRiverside;
    public boolean FeatGrill;
    public boolean FeatRestroom;
    public String Details;
    public double latitude;
    public double longitude;
    public Bitmap BM;

    public Campsite()
    {
        CampName = "";
        CampCity = "";
        CampLoc = "";
        AvgRating = (float) 0.0;
        FeatRiverside = false;
        FeatGrill = false;
        FeatRestroom = false;
        Details = "No Details";

    }

    //Kept this constructor just for legacy reasons
    public Campsite(int id, String name, String city, String loc, float avg_rating, boolean feat_river
            , boolean feat_g, boolean feat_rest, String details)
    {

        new Campsite(id, name, city, loc, avg_rating, feat_river,
         feat_g, feat_rest, details, 0.0, 0.0);

    }

    public Campsite(int id, String name, String city, String loc, float avg_rating, boolean feat_river
            , boolean feat_g, boolean feat_rest, String details, double lat, double longy)
    {
        CampId = id;
        CampName = name;
        CampCity = city;
        CampLoc = loc;
        AvgRating = avg_rating;
        FeatRiverside = feat_river;
        FeatGrill = feat_g;
        FeatRestroom = feat_rest;
        Details = details;
        latitude = lat;
        longitude = longy;
        BM = null;
    }

    public String ConvertFeatures2String()
    {
        String feature_str="";
        //I know this has a dependence on these arrays matching and I should have done a struct
        //but I don't want to fail this class and gotta hustle.
        boolean features[] = new boolean[]{this.FeatRiverside,this.FeatRestroom,this.FeatGrill};
        String feature_txt[] = new String[]{"Riverside","Restroom","Grill"};


        for(int i=0;i<features.length;i++)
        {

            if (features[i])
            {
                if(feature_str !="")
                {
                    feature_str+=", ";
                }
                feature_str+=feature_txt[i];
            }
        }

        return feature_str;
    }



}
