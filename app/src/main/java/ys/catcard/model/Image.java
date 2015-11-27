package ys.catcard.model;


import org.simpleframework.xml.Element;

public class Image {
    @Element(name = "id")
    String id;
    @Element(name = "url")
    String url;
    @Element(name = "source_url")
    String sourceUrl;

    public static Image create(String id, String url, String source_url) {
        Image image = new Image();
        image.setId(id);
        image.setUrl(url);
        image.setSourceUrl(source_url);
        return image;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}