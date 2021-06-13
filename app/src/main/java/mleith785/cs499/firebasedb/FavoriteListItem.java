package mleith785.cs499.firebasedb;


public class FavoriteListItem
{
    private String CampsiteName;
    private String CampsiteCity;
    private int CampsiteId;


    public FavoriteListItem(String CampsiteName, String CampsiteCity, int CampsiteId)
    {
        this.CampsiteName = CampsiteName;
        this.CampsiteCity = CampsiteCity;
        this.CampsiteId = CampsiteId;
    }

    public String getCampsiteName()
    {
        return CampsiteName;
    }

    public String getCampsiteCity()
    {
        return CampsiteCity;
    }

    public int getCampsiteId()
    {
        return CampsiteId;
    }
}
