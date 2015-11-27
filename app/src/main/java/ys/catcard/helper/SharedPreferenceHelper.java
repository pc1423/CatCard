package ys.catcard.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SharedPreferenceHelper {

    private static SharedPreferences sharedPreferences;

    public static void init(String prefName, Context context) {
        sharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static Set<String> getStringSetByKey(String key) {
        return sharedPreferences.getStringSet(key, new HashSet<String>());
    }

    public static void putStringSetByKey(String key, Set<String> set) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, set);
        editor.commit();
    }

    public static void removeStringSetByKey(String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }


    public static Set<String> getAllKeys() {
        return sharedPreferences.getAll().keySet();
    }
}
