package ys.catcard.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "response")
public class CatApiResponse {
    @Element
    private Data data;

    public List<Image> getImageList() {
        return data.getImages();
    }
}