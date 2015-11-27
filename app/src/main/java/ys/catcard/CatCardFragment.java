package ys.catcard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.gc.materialdesign.views.ButtonIcon;
import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import ys.catcard.adapter.GalleryAdapter;
import ys.catcard.annotation.UiThread;
import ys.catcard.api.ApiFactory;
import ys.catcard.api.virtual.CatApi;
import ys.catcard.model.CatApiResponse;
import ys.catcard.model.Image;
import ys.catcard.model.listener.ImageLongClickListener;

public class CatCardFragment extends Fragment {

    public static final int DEFAULT_FETCH_SIZE = 29;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GalleryAdapter mGalleryAdapter;
    private Spinner mCategorySpinner;
    private SpinnerAdapter mCategorySpinnerAdapter;

    private ButtonIcon mBtnRefresh;

    private ButtonRectangle mBtnSend;
    private EditText mEditTextMessage;

    private List<Image> imageUrls = new ArrayList<>();
    private CatApi.Category selectedCategory = CatApi.Category.RANDOM;

    private Callback<CatApiResponse> initialImageFetchListener = new Callback<CatApiResponse>() {
        @UiThread
        public void success(CatApiResponse catApiResponse, Response response) {
            if (!catApiResponse.getImageList().isEmpty()) {
                imageUrls.clear();
                imageUrls.add(Image.create(GalleryAdapter.RANDOM, GalleryAdapter.RANDOM, GalleryAdapter.RANDOM));
                imageUrls.addAll(catApiResponse.getImageList());

                mGalleryAdapter.setSelection(0);
                mGalleryAdapter.notifyDataSetChanged();
            }
        }

        @UiThread
        public void failure(RetrofitError error) { }
    };

    private ImageLongClickListener imageLongClickListener = new ImageLongClickListener() {
        @Override
        public void onLongClick(Image image) {
            ((MainActivity)getActivity()).processFavoriteImage(image);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.catcard_main, container, false);
        initUI(rootView);
        initListener();

        ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(29, initialImageFetchListener);
        return rootView;
    }

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.gallery_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mGalleryAdapter = new GalleryAdapter(imageUrls, imageLongClickListener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mCategorySpinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_dropdown_item, CatApi.Category.values());
        mCategorySpinner = (Spinner) rootView.findViewById(R.id.spinner_category);
        mCategorySpinner.setAdapter(mCategorySpinnerAdapter);

        mBtnSend = (ButtonRectangle) rootView.findViewById(R.id.btn_send);
        mBtnRefresh = (ButtonIcon) rootView.findViewById(R.id.btn_main_refresh);
        mEditTextMessage = (EditText) rootView.findViewById(R.id.edittext_message);
    }

    private void initListener() {
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CatApi.Category category = (CatApi.Category) mCategorySpinnerAdapter.getItem(position);
                selectedCategory = category;

                if (CatApi.Category.RANDOM.equals(selectedCategory)) {
                    ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(DEFAULT_FETCH_SIZE, initialImageFetchListener);
                } else {
                    ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(category, DEFAULT_FETCH_SIZE, initialImageFetchListener);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CatApi.Category.RANDOM.equals(selectedCategory)) {
                    ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(DEFAULT_FETCH_SIZE, initialImageFetchListener);
                } else {
                    ApiFactory.create(CatApi.BASE_URL, CatApi.class).getImageList(selectedCategory, DEFAULT_FETCH_SIZE, initialImageFetchListener);
                }
            }
        });

        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image imageSource = mGalleryAdapter.getSelectedItem();
                String message = mEditTextMessage.getText().toString();
                String btnText = getString(R.string.zoom);
                ((MainActivity)getActivity()).sendCatCard(selectedCategory, imageSource, message, btnText);
            }
        });

    }

}
