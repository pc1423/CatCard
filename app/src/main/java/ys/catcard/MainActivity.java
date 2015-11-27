package ys.catcard;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ys.catcard.adapter.GalleryAdapter;
import ys.catcard.annotation.UiThread;
import ys.catcard.api.ApiFactory;
import ys.catcard.api.virtual.CatApi;
import ys.catcard.api.virtual.CatApi.Category;
import ys.catcard.helper.KakaoLinker;
import ys.catcard.model.CatApiResponse;
import ys.catcard.model.Image;
import ys.catcard.model.dao.ImageDao;
import ys.catcard.model.listener.ProgressChangeListener;
import ys.catcard.view.ProgressDialog;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private ProgressChangeListener progressChangeListener = new ProgressChangeListener() {
        @UiThread
        public void onProgressChanged(final int totalAmount, final int progress) {
            Log.v("progress", progress + "/" + totalAmount);
            progressDialog.getTextView().setText(String.valueOf((int) (((double) progress / (double) totalAmount) * 100)) + "%");
        }

        @UiThread
        public void onPreExecute() {
            progressDialog.show(MainActivity.this);
        }

        @UiThread
        public void onPostExecute() {
            progressDialog.dismiss();
        }
    };
    private ViewPager sectionPager;
    private PagerAdapter sectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = ProgressDialog.create(this);

        sectionPager = (ViewPager) findViewById(R.id.pager);
        sectionAdapter = new PagerAdapter(getSupportFragmentManager(), new CatCardFragment(), new FavoriteCardFragment());

        sectionPager.setAdapter(sectionAdapter);
        ((PagerSlidingTabStrip) findViewById(R.id.tabs)).setViewPager(sectionPager);
    }

    public void processFavoriteImage(Image image) {
        if (GalleryAdapter.RANDOM.equals(image.getId())) {
            return;
        }

        if (ImageDao.insertImage(image)) {
            Toast.makeText(getApplicationContext(), "추가 되었습니다.", Toast.LENGTH_SHORT).show();
        } else {
            ImageDao.deleteImage(image);
            Toast.makeText(getApplicationContext(), "삭제 되었습니다.", Toast.LENGTH_SHORT).show();
        }

        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

        sectionAdapter.getFavoriteCardFragment().onDataSetChanged();
    }

    public void sendCatCard(Category selectedCategory, Image selectedSource, final String message, final String btnText) {
        progressDialog.show(MainActivity.this);
        hideKeyboard();

        if (GalleryAdapter.RANDOM.equals(selectedSource.getId())) {
            Callback<CatApiResponse> callBack = new Callback<CatApiResponse>() {
                @UiThread
                public void success(CatApiResponse catApiResponse, Response response) {
                    KakaoLinker.sendImageSourceMessage(MainActivity.this, message, btnText, catApiResponse.getImageList().get(0), progressChangeListener);
                }

                @UiThread
                public void failure(RetrofitError error) {
                    progressDialog.dismiss();
                }
            };

            if (GalleryAdapter.RANDOM.equals(selectedSource.getId())) {
                ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(1, callBack);
            } else {
                ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(selectedCategory, 1, callBack);
            }
        } else {
            KakaoLinker.sendImageSourceMessage(MainActivity.this, message, btnText, selectedSource, progressChangeListener);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        private CatCardFragment catCardFragment;
        private FavoriteCardFragment favoriteCardFragment;

        public PagerAdapter(FragmentManager fm, CatCardFragment catCardFragment, FavoriteCardFragment favoriteCardFragment) {
            super(fm);

            this.favoriteCardFragment = favoriteCardFragment;
            this.catCardFragment = catCardFragment;
        }

        public CatCardFragment getCatCardFragment() {
            return catCardFragment;
        }

        public FavoriteCardFragment getFavoriteCardFragment() {
            return favoriteCardFragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) { }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return catCardFragment;
                case 1:
                    return favoriteCardFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch(position) {
                case 0 :
                    return "Search";
                case 1 :
                    return "My";
                default :
                    return "";
            }
        }
    }

}