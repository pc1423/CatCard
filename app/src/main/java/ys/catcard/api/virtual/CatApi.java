package ys.catcard.api.virtual;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;
import ys.catcard.model.CatApiResponse;


public interface CatApi {

    String BASE_URL = "http://thecatapi.com/";
    String PATH = "/api/images/get?format=xml&size=small&type=jpg,png&api_key=NDMxOTY";

    enum Category {
        RANDOM,
        HATS,
        SUNGLASSES,
        SPACE,
        FUNNY,
        BOXES,
        CATURDAY,
        TIES,
        DREAM,
        SINKS,
        CLOTHES
    }

    @GET(PATH)
    void getImageList(
            @Query("category") Category category,
            @Query("results_per_page") int size,
            Callback<CatApiResponse> callback
    );

    @GET(PATH)
    void getImageList(
            @Query("results_per_page") int size,
            Callback<CatApiResponse> callback
    );


}
