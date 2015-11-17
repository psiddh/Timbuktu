package app.com.timbuktu.fragment;

/**
 * Copyright 2015 Bartosz Lipinski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;

import app.com.timbuktu.R;
import app.com.timbuktu.collage.CollageHelper;
import app.com.timbuktu.collections.Collection;


/**
 * Created by Bartosz Lipinski
 * 28.01.15
 */
public class ColorFragment extends Fragment {

    private static final String EXTRA_COLOR = "EXTRA_COLOR";
    private static final String EXTRA_POS = "EXTRA_POS";
    private static final String EXTRA_COLLECTION = "EXTRA_COLLECTION";


    FrameLayout mFragmentLayout;

    private class CollageWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private Collection collection;
        //CustomViewFlipper flipper;
        public CollageWorkerTask(ImageView imageView, Collection collection) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.collection = collection;
        }

        @Override
        protected void onPreExecute () {
            final ImageView imageView = (ImageView)imageViewReference.get();
            int height, width;
            height = imageView.getHeight();
            width = imageView.getWidth();

            height = imageView.getMeasuredHeight();
            width = imageView.getMeasuredWidth();

            Log.d("", "height = " + height + "  width : " + width);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return CollageHelper.doCollage(collection);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = (ImageView)imageViewReference.get();
                imageView.setImageBitmap(bitmap);
                //imageView.setScaleType(ImageView.ScaleType.CENTER);

            }

            final ImageView imageView = (ImageView)imageViewReference.get();
            int height, width;
            height = imageView.getHeight();
            width = imageView.getWidth();

            height = imageView.getMeasuredHeight();
            width = imageView.getMeasuredWidth();

            if (imageViewReference != null && bitmap == null) {
                final ImageView imageView1 = (ImageView)imageViewReference.get();
                imageView1.setImageResource(android.R.drawable.ic_menu_gallery);
                //imageView1.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
    }

    private CollageWorkerTask mTask;

    public static ColorFragment newInstance(int position, Collection collection, int backgroundColor) {
        ColorFragment fragment = new ColorFragment();
        Bundle bdl = new Bundle();
        bdl.putInt(EXTRA_COLOR, backgroundColor);
        bdl.putInt(EXTRA_POS, position);
        bdl.putParcelable(EXTRA_COLLECTION, collection);

        /*if (collection != null && collection.getBitmap() != null) {
            Bitmap b = collection.getBitmap();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 50, bs);
            bdl.putByteArray("byteArray", bs.toByteArray());
            bdl.putBoolean("isBmpSet", false);

        } else {
            bdl.putBoolean("isBmpSet", false);
        }*/

        fragment.setArguments(bdl);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_collectionview, container, false);
        Bundle bdl = getArguments();

        mFragmentLayout = (FrameLayout) v.findViewById(R.id.fragment_layout);

        LayerDrawable bgDrawable = (LayerDrawable) mFragmentLayout.getBackground();
        GradientDrawable shape = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.background_shape);
        shape.setColor(bdl.getInt(EXTRA_COLOR));

        TextView tv = (TextView) v.findViewById(R.id.header);
        Collection collection = bdl.getParcelable(EXTRA_COLLECTION);
        tv.setText("Position - " + bdl.getInt(EXTRA_POS));

        if (collection != null) {
            tv.setText("Position - " + bdl.getInt(EXTRA_POS) + " # of Pics :" + collection.size());

            ImageView imgView = (ImageView) v.findViewById(R.id.collage);
            mTask = new CollageWorkerTask(imgView, collection);
            mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return v;
    }
}