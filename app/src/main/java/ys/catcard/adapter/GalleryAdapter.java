package ys.catcard.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import ys.catcard.R;
import ys.catcard.model.ImageSource;
import ys.catcard.view.AnimatedCheckBox;

/**
 * 정사각형의 ImageView 로 구성된 콜라주 GalleryAdapter
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private List<ImageSource> imageSources = new ArrayList<ImageSource>();
    private ImageSource selectedItem;

    public GalleryAdapter(List<ImageSource> imageSources) {
        this.imageSources = imageSources;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_left_item, viewGroup, false), viewType);
            case 1 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_middle_item, viewGroup, false), viewType);
            case 2 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_right_item, viewGroup, false), viewType);
            default:
                return null;
        }
    }

    private void setProgressImageRequest(SimpleDraweeView simpleDraweeView, String url) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setProgressiveRenderingEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(simpleDraweeView.getController())
                .build();
        simpleDraweeView.setController(controller);
    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder viewHolder, int position) {
        final int index = position * 3;

        setProgressImageRequest(viewHolder.bigImageView, imageSources.get(index).getUrl());
        setProgressImageRequest(viewHolder.smallImageView1, imageSources.get(index + 1).getUrl());
        setProgressImageRequest(viewHolder.smallImageView2, imageSources.get(index + 2).getUrl());

        viewHolder.bigCheckBox.setChecked(imageSources.get(index).equals(selectedItem));
        viewHolder.smallCheckBox1.setChecked(imageSources.get(index + 1).equals(selectedItem));
        viewHolder.smallCheckBox2.setChecked(imageSources.get(index + 2).equals(selectedItem));

        viewHolder.bigCheckBox.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItem = imageSources.get(index);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.smallCheckBox1.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItem = imageSources.get(index + 1);
                    notifyDataSetChanged();
                }
            }
        });

        viewHolder.smallCheckBox2.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(View buttonView, boolean isChecked) {
                if (isChecked) {
                    selectedItem = imageSources.get(index + 2);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil((double) imageSources.size() / 3);
    }

    public ImageSource getSelectedItem() {
        return selectedItem;
    }

    /** ChatGallery ViewHolder */
    class GalleryViewHolder extends RecyclerView.ViewHolder {

        private SimpleDraweeView bigImageView;
        private SimpleDraweeView smallImageView1;
        private SimpleDraweeView smallImageView2;

        private AnimatedCheckBox bigCheckBox;
        private AnimatedCheckBox smallCheckBox1;
        private AnimatedCheckBox smallCheckBox2;

        public GalleryViewHolder(final View itemView, int viewType) {
            super(itemView);
            switch (viewType) {
                case 0 :
                    bigImageView = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_left);
                    smallImageView1 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_right1);
                    smallImageView2 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_right2);

                    bigCheckBox = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_left_checkbox);
                    smallCheckBox1 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_right1_checkbox);
                    smallCheckBox2 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_right2_checkbox);
                    break;
                case 1 :
                    bigImageView = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_1);
                    smallImageView1 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_2);
                    smallImageView2 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_3);

                    bigCheckBox = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_1_checkbox);
                    smallCheckBox1 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_2_checkbox);
                    smallCheckBox2 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_3_checkbox);
                    break;
                case 2 :
                    bigImageView = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_right);
                    smallImageView1 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_left1);
                    smallImageView2 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_image_view_left2);

                    bigCheckBox = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_right_checkbox);
                    smallCheckBox1 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_left1_checkbox);
                    smallCheckBox2 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_image_view_left2_checkbox);
                    break;
            }

            /**
             *  가중치가 가장 높은 ImageView 의 width 와 동일하게 height 를 설정한다.
             *  (가중치가 낮은 ImageView : 가중치가 높은 ImageView 가로 = 1:2) 이므로
             *  모든 ImageView 는 정사각형의 모양이 된다.
             * */
            itemView.post(new Runnable() {
                @Override
                public void run() {
                    itemView.getLayoutParams().height = bigImageView.getWidth();
                }
            });
        }

    }
}
