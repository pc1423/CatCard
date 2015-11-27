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
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.List;

import ys.catcard.adapter.GalleryAdapter;
import ys.catcard.api.virtual.CatApi;
import ys.catcard.helper.SharedPreferenceHelper;
import ys.catcard.model.Image;
import ys.catcard.model.dao.ImageDao;
import ys.catcard.model.listener.DataSetChangeListener;
import ys.catcard.model.listener.ImageLongClickListener;

public class FavoriteCardFragment extends Fragment implements DataSetChangeListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GalleryAdapter mGalleryAdapter;

    private ButtonRectangle mBtnSend;
    private EditText mEditTextMessage;

    private List<Image> imageUrls = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        imageUrls = ImageDao.selectImageList(SharedPreferenceHelper.getAllKeys());
        View rootView = inflater.inflate(R.layout.mycard_main, container, false);

        initUI(rootView);
        initListener();

        return rootView;
    }

    private ImageLongClickListener imageLongClickListener = new ImageLongClickListener() {
        @Override
        public void onLongClick(Image image) {
            ((MainActivity)getActivity()).processFavoriteImage(image);
        }
    };

    private void initUI(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.mycard_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mGalleryAdapter = new GalleryAdapter(imageUrls, imageLongClickListener);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mBtnSend = (ButtonRectangle) rootView.findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) rootView.findViewById(R.id.edittext_message);
    }

    private void initListener() {
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Image imageSource = mGalleryAdapter.getSelectedItem();
                String message = mEditTextMessage.getText().toString();
                String btnText = getString(R.string.zoom);
                ((MainActivity)getActivity()).sendCatCard(CatApi.Category.RANDOM, imageSource, message, btnText);
            }
        });
    }

    @Override
    public void onDataSetChanged() {
        imageUrls.clear();
        imageUrls.addAll(ImageDao.selectImageList(SharedPreferenceHelper.getAllKeys()));
        mGalleryAdapter.notifyDataSetChanged();
    }
}
