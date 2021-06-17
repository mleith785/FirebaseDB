package mleith785.cs499.firebasedb;

public class UserFavorite
{
    public String CampsiteKey;
    public String mAuth;

    public UserFavorite()
    {

    }

    public UserFavorite(String campsiteKey, String mAuth)
    {
        CampsiteKey = campsiteKey;
        this.mAuth = mAuth;
    }
}
