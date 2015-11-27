package ys.catcard.model;

import org.simpleframework.xml.ElementList;

import java.util.List;

public class Data {
    @ElementList
    List<Image> images;

    public List<Image> getImages() {
        return images;
    }
}
