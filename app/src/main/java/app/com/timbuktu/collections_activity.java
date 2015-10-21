package app.com.timbuktu;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;

import app.com.timbuktu.layout.CollectionLayout;
import app.com.timbuktu.service.SyncMediaDetails;
import app.com.timbuktu.util.SystemUiHider;

public class collections_activity extends Activity {
    private static final String TAG = "collections_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collections_activity);
        CollectionLayout tagLayout = (CollectionLayout) findViewById(R.id.tagLayout);
        LayoutInflater layoutInflater = getLayoutInflater();
        String tag;
        SyncCache syncCache = SyncCache.getInstance();

        for (int i = 80; i <= 85; i++) {
            View tagView = layoutInflater.inflate(R.layout.collection_childlayout, null, false);
            ImageView tagTextView = (ImageView) tagView.findViewById(R.id.tagImgView);
            MediaItem item = syncCache.getMediaItem(i);
            if (item != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                options.inDither = true;
                Bitmap bmImg = BitmapFactory.decodeFile(item.getPath());
                tagTextView.setImageBitmap(bmImg);
            }
            tagLayout.addView(tagView);
        }
    }

}
