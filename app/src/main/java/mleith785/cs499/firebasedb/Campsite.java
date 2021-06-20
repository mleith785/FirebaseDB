package mleith785.cs499.firebasedb;

import android.graphics.Bitmap;
import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class Campsite
{
    public String CampName;
    public String CampCity;
    public float AvgRating;
    public boolean FeatRiverside;
    public boolean FeatGrill;
    public boolean FeatRestroom;
    public String Details;
    public double latitude;
    public double longitude;
    public String Picture_Storage;

    public Campsite()
    {
        CampName = "";
        CampCity = "";
        AvgRating = (float) 0.0;
        FeatRiverside = false;
        FeatGrill = false;
        FeatRestroom = false;
        Details = "No Details";
        Picture_Storage=null;

    }


    public Campsite(String name, String city, String loc, float avg_rating, boolean feat_river
            , boolean feat_g, boolean feat_rest, String details, double lat, double longy)
    {

        CampName = name;
        CampCity = city;
        AvgRating = avg_rating;
        FeatRiverside = feat_river;
        FeatGrill = feat_g;
        FeatRestroom = feat_rest;
        Details = details;
        latitude = lat;
        longitude = longy;
        Picture_Storage = null;
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
