package gallerypro.galleryapp.bestgallery.model;

public class DateItem extends ListItem {

    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
