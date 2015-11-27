package ys.catcard.adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import ys.catcard.R;
import ys.catcard.model.Image;
import ys.catcard.model.listener.ImageLongClickListener;
import ys.catcard.view.AnimatedCheckBox;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    public static final String RANDOM = "random";

    private List<Image> images = new ArrayList<Image>();
    private Image selectedItem;

    private ImageLongClickListener longClickListener;

    public GalleryAdapter(List<Image> images, ImageLongClickListener longClickListener) {
        this.images = images;
        this.longClickListener = longClickListener;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        switch (viewType) {
            case 0 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_left_item, viewGroup, false));
            case 1 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_middle_item, viewGroup, false));
            case 2 :
                return new GalleryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.gallery_right_item, viewGroup, false));
            default:
                return null;
        }
    }

    public void setSelection(int index) {
        selectedItem = images.get(index);
        notifyDataSetChanged();
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

    private void setCheckBoxProperties(SimpleDraweeView draweeView, final AnimatedCheckBox checkBox, final int index) {

        try {
            draweeView.setVisibility(View.VISIBLE);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(images.get(index).equals(selectedItem));
            draweeView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkBox.performClick();
                }
            });

            checkBox.setOnCheckedChangeListener(new AnimatedCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectedItem = images.get(index);
                        notifyDataSetChanged();
                    }
                }
            });

            draweeView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClickListener.onLongClick(images.get(index));
                    return false;
                }
            });
        } catch (IndexOutOfBoundsException e) {
            draweeView.setVisibility(View.INVISIBLE);
            checkBox.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onBindViewHolder(final GalleryViewHolder viewHolder, int position) {
        final int index = position * 3;

        setImage(viewHolder.bigImageView, index);
        setImage(viewHolder.smallImageView1, index + 1);
        setImage(viewHolder.smallImageView2, index + 2);

        setCheckBoxProperties(viewHolder.bigImageView, viewHolder.bigCheckBox, index);
        setCheckBoxProperties(viewHolder.smallImageView1, viewHolder.smallCheckBox1, index + 1);
        setCheckBoxProperties(viewHolder.smallImageView2, viewHolder.smallCheckBox2, index + 2);

    }

    private void setImage(SimpleDraweeView imageView, int index) {
        try {
            imageView.setVisibility(View.VISIBLE);
            if (RANDOM.equals(images.get(index).getId())) {
                Uri uri = new Uri.Builder()
                        .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                        .path(String.valueOf(R.drawable.random))
                        .build();

                imageView.setImageURI(uri);
            } else {
                setProgressImageRequest(imageView, images.get(index).getUrl());
            }
        } catch (IndexOutOfBoundsException e) {
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position % 3;
    }

    @Override
    public int getItemCount() {
        return (int) Math.ceil((double) images.size() / 3);
    }

    public Image getSelectedItem() {
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

        public GalleryViewHolder(final View itemView) {
            super(itemView);

            bigImageView = (SimpleDraweeView) itemView.findViewById(R.id.gallery_item_big).findViewById(R.id.gallery_item_each_drawee);
            smallImageView1 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_item_small1).findViewById(R.id.gallery_item_each_drawee);
            smallImageView2 = (SimpleDraweeView) itemView.findViewById(R.id.gallery_item_small2).findViewById(R.id.gallery_item_each_drawee);

            bigCheckBox = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_item_big).findViewById(R.id.gallery_item_each_checkbox);
            smallCheckBox1 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_item_small1).findViewById(R.id.gallery_item_each_checkbox);
            smallCheckBox2 = (AnimatedCheckBox) itemView.findViewById(R.id.gallery_item_small2).findViewById(R.id.gallery_item_each_checkbox);

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
