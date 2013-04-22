package edu.mit.cci.roma.excel.responsesurfaces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
*   A Slice is a composition of {@link SliceSegment}s; it describes the surface between
 *  two adjacent datasets.
 *
 *  The Slice maintains segments in order according to {@link edu.mit.cci.roma.excel.responsesurfaces.SliceSegment#getIndex()}
 *
 *  Slice implements the {@link Comparable} interface.  Comparing two slices will order them according to
 *  {@link edu.mit.cci.roma.excel.responsesurfaces.SliceSegment#getToCriterion()}, which is the upper bound of the slice
 *
 *
 * User: jintrone
* Date: 2/24/11
* Time: 9:13 AM
*/


public class Slice<T extends Comparable<T>> extends ArrayList<SliceSegment<T>> implements Comparable<Slice<T>>  {

    Comparator<SliceSegment<T>> segmentComparator = new Comparator<SliceSegment<T>> () {

        @Override
        public int compare(SliceSegment<T> segment1, SliceSegment<T> segment2) {
          return segment1.getIndex().compareTo(segment2.getIndex());
        }
    };

    @Override
    public SliceSegment<T> set(int i, SliceSegment<T> sliceSegment) {
        throw new UnsupportedOperationException("Slice does not support explicit positioning of segments; modify the segment's index to obtain a different ordering");
    }

    @Override
    public boolean add(SliceSegment<T> sliceSegment) {
        boolean result = super.add(sliceSegment);
        List<SliceSegment<T>> tmp = new ArrayList<SliceSegment<T>>(this);
        Collections.sort(tmp,segmentComparator);
        this.clear();
        this.addAll(tmp);
        return result;
    }

    @Override
    public void add(int i, SliceSegment<T> sliceSegment) {
        throw new UnsupportedOperationException("Slice does not support explicit positioning of segments; modify the segment's index to obtain a different ordering");
    }

    public int compareTo(Slice<T> other) {
        SliceSegment<T> otherrange = other.get(other.size()-1);
        SliceSegment<T> myrange = get(this.size()-1);
        return myrange.getToCriterion().compareTo(otherrange.getToCriterion());

    }




}
