package ys.catcard.helper;

import android.content.Context;

import com.kakao.kakaolink.KakaoLink;
import com.kakao.kakaolink.KakaoTalkLinkMessageBuilder;

import ys.catcard.annotation.Background;
import ys.catcard.model.Image;
import ys.catcard.model.listener.ProgressChangeListener;
import ys.catcard.model.Size;

public class KakaoLinker {

    @Background
    public static void sendImageSourceMessage(Context context, String message, String btnText, Image image, final ProgressChangeListener progressChangeListener) {
        try {
            progressChangeListener.onPreExecute();

            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(context);
            final KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

            String url = image.getUrl();
            String id = image.getId();
            Size imageSize = BitmapHelper.bitmapSizeFromUrl(url, progressChangeListener);

            kakaoTalkLinkMessageBuilder.addText(message);
            kakaoTalkLinkMessageBuilder.addImage(url, imageSize.getWidth(), imageSize.getHeight());
            kakaoTalkLinkMessageBuilder.addWebButton(btnText, "http://thecatapi.com/api/images/get?size=mid&id=" + id);
            kakaoTalkLinkMessageBuilder.setForwardable(true);

            kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), context);
        } catch (Exception e) {
            progressChangeListener.onPostExecute();
            e.printStackTrace();
        }
    }

}
