package mleith785.cs499.firebasedb;

public class UserFavorite
{
    public String CampsiteKey;
    public String CampsiteName;
    public String CampsiteCity;
    public String mAuth;

    public UserFavorite()
    {

    }

    public UserFavorite(String campsiteKey, String campsiteName, String campsiteCity, String mAuth)
    {
        CampsiteKey = campsiteKey;
        CampsiteName = campsiteName;
        CampsiteCity = campsiteCity;
        this.mAuth = mAuth;
    }
}
