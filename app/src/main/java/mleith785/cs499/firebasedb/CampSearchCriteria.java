package mleith785.cs499.firebasedb;

import java.io.Serializable;

//Serializable is stolen from here
//https://stackoverflow.com/questions/2736389/how-to-pass-an-object-from-one-activity-to-another-on-android
//so I can pass an object to the next activity
public class CampSearchCriteria implements Serializable
{

    private String CampName;
    private String CampCity;
    private boolean FeatureGrill;
    private boolean FeatureRiverside;
    private boolean FeatureRestroom;


    public CampSearchCriteria(String campName, String campCity, boolean featureGrill, boolean featureRiverside, boolean featureRestroom)
    {
        CampName = campName;
        CampCity = campCity;
        FeatureGrill = featureGrill;
        FeatureRiverside = featureRiverside;
        FeatureRestroom = featureRestroom;
    }

    public String getCampCity()
    {
        return CampCity;
    }

    public boolean isFeatureGrill()
    {
        return FeatureGrill;
    }

    public boolean isFeatureRiverside()
    {
        return FeatureRiverside;
    }

    public boolean isFeatureRestroom()
    {
        return FeatureRestroom;
    }

    public String getCampName()
    {
        return CampName;
    }

    public String createDbWhereClause()
    {
        String where_clause="";
        if(0!=CampName.compareTo(""))
        {
            where_clause="Name=\""+CampName+"\" collate nocase ";
        }
        if(0!=CampCity.compareTo(""))
        {
            if(0!=where_clause.compareTo(""))
            {
                where_clause+=" AND ";
            }
            where_clause+="City=\""+CampCity+"\" collate nocase ";
        }

        if(FeatureRiverside)
        {
            if(0!=where_clause.compareTo(""))
            {
                where_clause+=" AND ";
            }
            where_clause+="FeatureRiverside=1";
        }

        if(FeatureGrill)
        {
            if(0!=where_clause.compareTo(""))
            {
                where_clause+=" AND ";
            }
            where_clause+="FeatureGrill=1";
        }

        if(FeatureRestroom)
        {
            if(0!=where_clause.compareTo(""))
            {
                where_clause+=" AND ";
            }
            where_clause+="FeatureRestroom=1";
        }

        return where_clause;
    }

}
