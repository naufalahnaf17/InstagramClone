package app.android.instagramclone;

public class Upload {
    String idPost;
    String captionPost;
    String urlPost;

    public Upload(){}

    public Upload(String idPost, String captionPost, String urlPost) {
        this.idPost = idPost;
        this.captionPost = captionPost;
        this.urlPost = urlPost;
    }

    public String getIdPost() {
        return idPost;
    }

    public String getCaptionPost() {
        return captionPost;
    }

    public String getUrlPost() {
        return urlPost;
    }
}
