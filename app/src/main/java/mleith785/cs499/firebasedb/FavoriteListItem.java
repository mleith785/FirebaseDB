package mleith785.cs499.firebasedb;


public class FavoriteListItem
{
    private String CampsiteName;
    private String CampsiteCity;
    private String CampsiteKey;

    public FavoriteListItem()
    {

    }

    public FavoriteListItem(String CampsiteName, String CampsiteCity, String CampsiteKey)
    {
        this.CampsiteName = CampsiteName;
        this.CampsiteCity = CampsiteCity;
        this.CampsiteKey = CampsiteKey;
    }

    public String getCampsiteName()
    {
        return CampsiteName;
    }

    public String getCampsiteCity()
    {
        return CampsiteCity;
    }

    public String getCampsiteKey()
    {
        return CampsiteKey;
    }
}
