package ys.catcard.model.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ys.catcard.helper.SharedPreferenceHelper;
import ys.catcard.model.Image;

public class ImageDao {
    public static List<Image> selectImageList(Set<String> imageIds) {
        List<Image> imageUrls = new ArrayList<>();

        for(String imageId : imageIds) {
            Image image = selectImage(imageId);

            if (image != null) {
                imageUrls.add(image);
            }
        }

        return imageUrls;
    }

    public static Image selectImage(String imageId) {
        Iterator<String> valueIterator = SharedPreferenceHelper.getStringSetByKey(imageId).iterator();

        if (valueIterator.hasNext()) {
            String id = imageId;
            String sourceUrl = valueIterator.next();
            String url = valueIterator.next();

            if (!sourceUrl.contains("http://thecatapi.com")) {
                String temp = sourceUrl;
                sourceUrl = url;
                url = temp;
            }

            return Image.create(id, url, sourceUrl);
        }

        return null;
    }

    public static boolean insertImage(Image image) {
        boolean isEmpty = SharedPreferenceHelper.getStringSetByKey(image.getId()).isEmpty();

        if (isEmpty) {
            Set<String> valueSet = new LinkedHashSet<>();
            valueSet.add(image.getSourceUrl());
            valueSet.add(image.getUrl());

            SharedPreferenceHelper.putStringSetByKey(image.getId(), valueSet);
            return true;
        }

        return false;
    }

    public static boolean deleteImage(Image image) {
        boolean isEmpty = SharedPreferenceHelper.getStringSetByKey(image.getId()).isEmpty();

        if (!isEmpty) {
            SharedPreferenceHelper.removeStringSetByKey(image.getId());
            return true;
        }

        return false;
    }
}
