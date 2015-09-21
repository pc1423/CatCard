package ys.catcard;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;
import com.kakao.util.KakaoParameterException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ys.catcard.adapter.GalleryAdapter;
import ys.catcard.model.Category;
import ys.catcard.model.ImageSource;
import ys.catcard.model.Size;

public class MainActivity extends AppCompatActivity {

    public static final String GET_IMAGE_LIST_URL = "http://thecatapi.com/api/images/get?format=xml&size=small&type=jpg,png&results_per_page=29";
    public static final String GET_IMAGE_URL = "http://thecatapi.com/api/images/get?format=xml&size=small&type=jpg,png&results_per_page=1";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private GalleryAdapter mGalleryAdapter;
    private Spinner mCategorySpinner;
    private SpinnerAdapter mCategorySpinnerAdapter;

    private Button mBtnSend;
    private EditText mEditTextMessage;

    private List<ImageSource> imageUrls = new ArrayList<ImageSource>();
    private Category selectedCategory = Category.RANDOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        fetchImageUrls(imageUrls, Category.RANDOM);

        mRecyclerView = (RecyclerView) findViewById(R.id.gallery_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mGalleryAdapter = new GalleryAdapter(imageUrls);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mCategorySpinnerAdapter = new ArrayAdapter<Category>(getApplicationContext(), R.layout.spinner_item, Category.values());
        mCategorySpinner = (Spinner) findViewById(R.id.spinner_category);
        mCategorySpinner.setAdapter(mCategorySpinnerAdapter);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category category = (Category) mCategorySpinnerAdapter.getItem(position);
                selectedCategory = category;
                fetchImageUrls(imageUrls, category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ImageSource selectedSource = mGalleryAdapter.getSelectedItem();

                            if (GalleryAdapter.RANDOM.equals(selectedSource.getId())) {
                                fetchImageUrlAndSend(selectedCategory);
                            } else {
                                send(getApplicationContext(), mEditTextMessage.getText().toString(), getResources().getString(R.string.zoom), selectedSource);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        mEditTextMessage = (EditText) findViewById(R.id.edittext_message);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void fetchImageUrlAndSend(final Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url;
                try {
                    if (Category.RANDOM == category) {
                        url = new URL(GET_IMAGE_URL);
                    } else {
                        url = new URL(GET_IMAGE_URL + "&category=" + category.name());
                    }

                    List<ImageSource> imageSources = getRandomImageSources(url);
                    send(getApplicationContext(), mEditTextMessage.getText().toString(), getResources().getString(R.string.zoom), imageSources.get(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //fetch image urls from api
    private void fetchImageUrls(final List<ImageSource> imageSourceList, final Category category) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url;
                    if (Category.RANDOM == category) {
                        url = new URL(GET_IMAGE_LIST_URL);
                    } else {
                        url = new URL(GET_IMAGE_LIST_URL + "&category=" + category.name());
                    }

                    List<ImageSource> imageSources = getRandomImageSources(url);
                    imageSourceList.clear();
                    imageSourceList.add(new ImageSource("random", "random"));
                    imageSourceList.addAll(imageSources);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mGalleryAdapter.setSelection(0);
                            mGalleryAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @NonNull
    private List<ImageSource> getRandomImageSources(URL url) {
        List<ImageSource> imageSourceList = new ArrayList<>();

        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = db.parse(url.openConnection().getInputStream());

            NodeList idList = doc.getElementsByTagName("id");
            NodeList imageUrls = doc.getElementsByTagName("url");

            for (int i=0; i<idList.getLength(); i++) {
                String id = idList.item(i).getFirstChild().getNodeValue();
                String imageUrl = imageUrls.item(i).getFirstChild().getNodeValue();

                imageSourceList.add(new ImageSource(imageUrl, id));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageSourceList;
    }

    private void send(Context context, String message, String btnText, ImageSource imageSource) throws IOException {
        try {
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(context);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String url = imageSource.getUrl();
            String id = imageSource.getId();
            Size imageSize = bitmapSizeFromUrl(url);

            kakaoTalkLinkMessageBuilder.addText(message);
            kakaoTalkLinkMessageBuilder.addImage(url, imageSize.getWidth(), imageSize.getHeight());
            kakaoTalkLinkMessageBuilder.addWebButton(btnText, "http://thecatapi.com/api/images/get?size=mid&id=" + id);
            kakaoTalkLinkMessageBuilder.setForwardable(true);

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), this);
        } catch (KakaoParameterException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private Size bitmapSizeFromUrl (String imageURL){
        try {
            byte[] datas = getImageDataFromUrl( new URL(imageURL) );

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(datas, 0, datas.length, opts);

            int width = opts.outWidth;
            int height = opts.outHeight;

            return new Size(width, height);
        } catch (IOException e) {
            return null;
        }
    }

    @NonNull
    private byte[] getImageDataFromUrl (URL url) {
        byte[] datas = {};

        try {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setDoInput(true);
            connection.connect();

            InputStream input = connection.getInputStream();
            datas = inputStreamToByteArray(input);

            input.close();
            connection.disconnect();
        } catch (IOException e) {
        }

        return datas;
    }

    @NonNull
    private byte[] inputStreamToByteArray(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}

