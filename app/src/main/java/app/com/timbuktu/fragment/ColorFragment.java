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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

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
        tv.setText("Position - " + bdl.getInt(EXTRA_POS));

        Collection collection = bdl.getParcelable(EXTRA_COLLECTION);

        if (collection != null) {
            Bitmap bmp = null;
            bmp = CollageHelper.doCollage(collection);
            if (bmp != null) {
                ImageView imgView = (ImageView) v.findViewById(R.id.collage);
                imgView.setImageBitmap(bmp);
            }
        }

        return v;
    }
}