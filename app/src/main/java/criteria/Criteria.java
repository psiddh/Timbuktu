package criteria;

import android.util.Pair;

import java.util.ArrayList;

public class Criteria {
    private ArrayList<String> mPlaces;
    private Pair<Long, Long> mDateRange;
    private Long mRadius;

    public static long DEFAULT_TIMESTAMP_DIFF = 86400000; // 1 day

    public Criteria() {
        init();
    }

    public Criteria(Criteria criterion) {
        init();
        mPlaces = criterion.getPlaces();
        mDateRange = criterion.getDateRange();
        mRadius = criterion.getRadius();
    }

    public void add(Object criterion) {
        if (criterion == null)
            return;
        if (criterion instanceof ArrayList<?> && (((ArrayList<?>) criterion).size() > 0) ) {
            if (((ArrayList<?>) criterion).get(0) instanceof String) {
                for ( String s: (ArrayList<String>) criterion) {
                    mPlaces.add(s);
                }
            }

        } else if (criterion instanceof Pair) {
            if (((Pair<?, ?>) criterion).first instanceof Long) {
                mDateRange = ((Pair<Long,Long>) criterion);
            }

        } else if (criterion instanceof Long) {
            mRadius = (Long) criterion;
        }

    }

    public void clear(Object criterion) {
        if (criterion instanceof ArrayList<?>) {
            if (((ArrayList<?>) criterion).get(0) instanceof String) {
                mPlaces = new ArrayList<>();
            }

        } else if (criterion instanceof Pair) {
            if (((Pair<?, ?>) criterion).first instanceof Long) {
                mDateRange = new Pair<Long, Long>(null, null);
            }

        } else if (criterion instanceof Long) {
            mRadius = new Long(-1);
        }
    }

    public void clearAll() {
        init();
    }

    public ArrayList<String> getPlaces() {
        return mPlaces;
    }

    public Pair<Long, Long> getDateRange() {
        return mDateRange;
    }

    public Long getRadius() {
        return mRadius;
    }

    public boolean isCriteriaSet() {
        return !(mPlaces.size() == 0 && mDateRange.first == null && mDateRange.second == null && mRadius == -1);
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob.getClass() != getClass()) return false;
        Criteria other = (Criteria)ob;
        Pair <Long, Long> curRange = getDateRange();
        Pair <Long, Long> newRange = other.getDateRange();

        if (curRange != null && newRange != null) {
            if (!(curRange.first == newRange.first && curRange.second == newRange.second)) {
                return false;
            }
        } else if ((curRange == null && newRange != null ) || (curRange != null && newRange == null)) {
            return false;
        }


        ArrayList<String> curPlaces = getPlaces();
        ArrayList<String> newPlaces = other.getPlaces();
        if (curPlaces != null && newPlaces != null) {
            if (curPlaces.size() != newPlaces.size())
                return false;

            for (String s: curPlaces) {
                if (!newPlaces.contains(s)) {
                    return false;
                }
            }
        } else if ((curPlaces == null && newPlaces != null ) || (curPlaces != null && newPlaces == null)) {
            return false;
        }

        if (!(getRadius().equals(other.getRadius())))
            return false;

        return true;
    }

    private void init() {
        mPlaces = new ArrayList<>();
        mDateRange = new Pair<Long, Long>(null, null);
        mRadius = new Long(-1);
    }

}