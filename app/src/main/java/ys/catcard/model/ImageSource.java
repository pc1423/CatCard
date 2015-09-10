package ys.catcard.model;

/**
 * CLASS NAME
 * DESCRIPTION
 *
 * @author CA10221
 */
public class ImageSource {
    private String url;
    private String id;

    public ImageSource(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }
}