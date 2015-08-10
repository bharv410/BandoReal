package classes;

import java.util.Date;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class BandoPost implements Comparable<BandoPost> {
    private String dateString;
    private String uniqueId;
    private String postText;
    private String postUrl;
    private String postDeepLink;
    private String postSourceSite;
    private String postType;
    private String imageUrl;
    private String username;
    private String userProfilePic;
    private boolean postHasImage;
    private Date dateTime;

    private int viewCOunt;

    public BandoPost() {

    }

    public boolean postHasImage() {
        return postHasImage;
    }

    public void setPostHasImage(boolean hasImage) {
        this.postHasImage = hasImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPostSourceSite() {
        return postSourceSite;
    }

    public String getPostDeepLink() {
        return postDeepLink;
    }

    public String getPostText() {
        return postText;
    }

    public String getPostType() {
        return postType;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isPostHasImage() {
        return postHasImage;
    }

    public void setPostDeepLink(String postDeepLink) {
        this.postDeepLink = postDeepLink;
    }

    public void setPostSourceSite(String postSourceSite) {
        this.postSourceSite = postSourceSite;
    }

    public void setPostText(String postText) {
        this.postText = postText;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public String getDateString() {
        return dateString;
    }

    public int getViewCOunt() {
        return viewCOunt;
    }

    public void setViewCOunt(int viewCOunt) {
        this.viewCOunt = viewCOunt;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    @Override
    public int compareTo(BandoPost o) {
        return o.getDateTime().compareTo(getDateTime());
    }

    @Override
    public boolean equals(Object c) {
        if (c instanceof BandoPost) {
            BandoPost bp = (BandoPost) c;
            if (bp.postText.contains(this.postText)) {
                return true;
            }
        }
        return false;
    }
}

