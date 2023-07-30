package ddwucom.mobile.finalproject.ma01_20180961;


import java.io.Serializable;

public class bookmarkDto implements Serializable {
    private long _id;
    private String name;
    private String phone;
    private String address;
    private String opening;
    private String rating;
    private String site;
    private String photo;
    private String memo;


    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }


    public long get_id() { return _id; }

    public void set_id(long _id) { this._id = _id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }

    public String getPhoto() { return photo; }

    public void setPhoto(String photo) { this.photo = photo; }

    public String getMemo() { return memo; }

    public void setMemo(String memo) { this.memo = memo; }

    @Override
    public String toString() { return new String(_id + " : " + photo); }
}
