package classes;

/**
 * Created by benjamin.harvey on 8/4/15.
 */
public class BandoPost {
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

    public BandoPost(){

    }

    public boolean postHasImage(){
        return postHasImage;
    }

    public void setPostHasImage(boolean hasImage){
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
}

