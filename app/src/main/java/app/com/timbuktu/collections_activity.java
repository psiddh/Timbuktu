package app.com.timbuktu;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.bartoszlipinski.flippablestackview.FlippableStackView;
import com.bartoszlipinski.flippablestackview.StackPageTransformer;
import com.bartoszlipinski.flippablestackview.utilities.ValueInterpolator;

import java.util.ArrayList;
import java.util.List;

import app.com.timbuktu.fragment.ColorFragment;
import app.com.timbuktu.collections.Collections;

public class collections_activity extends FragmentActivity {
    private static final String TAG = "collections_activity";

    private int NUMBER_OF_FRAGMENTS = 0;
    private int MAX_NUMBER_OF_FRAGMENTS = 25;


    private FlippableStackView mFlippableStack;
    private ColorFragmentAdapter mPageAdapter;
    private List<Fragment> mViewPagerFragments;
    private Collections mCollections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collections_activity);

        Bundle b = getIntent().getExtras();
        mCollections = b.getParcelable("collections");

        if (mCollections != null) {
            Log.d(TAG, "collections size : " + mCollections.size());

            NUMBER_OF_FRAGMENTS = mCollections.size();
            if (NUMBER_OF_FRAGMENTS > MAX_NUMBER_OF_FRAGMENTS)
                NUMBER_OF_FRAGMENTS = MAX_NUMBER_OF_FRAGMENTS;

            createViewPagerFragments();
            mPageAdapter = new ColorFragmentAdapter(getSupportFragmentManager(), mViewPagerFragments);

            boolean portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

            mFlippableStack = (FlippableStackView) findViewById(R.id.stack);
            mFlippableStack.initStack(NUMBER_OF_FRAGMENTS, portrait ?
                    StackPageTransformer.Orientation.VERTICAL :
                    StackPageTransformer.Orientation.HORIZONTAL);
            mFlippableStack.setAdapter(mPageAdapter);
        }
    }

    private void createViewPagerFragments() {
        mViewPagerFragments = new ArrayList<>();

        int startColor = getResources().getColor(R.color.emerald);
        int startR = Color.red(startColor);
        int startG = Color.green(startColor);
        int startB = Color.blue(startColor);

        int endColor = getResources().getColor(R.color.wisteria);
        int endR = Color.red(endColor);
        int endG = Color.green(endColor);
        int endB = Color.blue(endColor);

        ValueInterpolator interpolatorR = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endR, startR);
        ValueInterpolator interpolatorG = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endG, startG);
        ValueInterpolator interpolatorB = new ValueInterpolator(0, NUMBER_OF_FRAGMENTS - 1, endB, startB);

        for (int i = 0; i < NUMBER_OF_FRAGMENTS; ++i) {
            mViewPagerFragments.add(ColorFragment.newInstance(i, mCollections.at(i), Color.argb(255, (int) interpolatorR.map(i), (int) interpolatorG.map(i), (int) interpolatorB.map(i))));
        }
    }

    private class ColorFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public ColorFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }
    }

}
