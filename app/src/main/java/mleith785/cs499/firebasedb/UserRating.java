package mleith785.cs499.firebasedb;

/**
 * This class is used to get the user rating from firebase table
 * UserRatings
 */
public class UserRating
{
    public String CampsiteKey;
    public float Rating;
    public String mAuth;

    public UserRating()
    {

    }

    public UserRating(String campsiteKey, float rating, String mAuth)
    {
        CampsiteKey = campsiteKey;
        Rating = rating;
        this.mAuth = mAuth;
    }

}
