package ys.catcard.api;

import retrofit.RestAdapter;
import retrofit.converter.SimpleXMLConverter;

public class ApiFactory {

    public static <T> T create(String url, Class<T> clazz) {
        return new RestAdapter.Builder()
                //로그 레벨 설정
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(url)
                .setConverter(new SimpleXMLConverter())
                .build().create(clazz);
    }
}
