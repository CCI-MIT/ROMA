package edu.mit.cci.roma.excel.responsesurfaces;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: jintrone
 * Date: 2/25/11
 * Time: 1:06 AM
 */
public class SliceTest {


    @Test
    public void testModifications() throws Exception {
        Polynomial p = new Polynomial(new double[]{4, 5});
        SliceSegment<Float> s1 = new SliceSegment<Float>(3f, 4f, 100, p);
        SliceSegment<Float> s2 = new SliceSegment<Float>(4f, 5f, 103, p);

        Slice<Float> s = new Slice<Float>();

        try {
            s.add(s1);
            s.add(0,s2);
            Assert.fail("Should not be able to add segments at specific points");
        } catch (UnsupportedOperationException ex) {

        }

        try {
            s.set(0,s2);
            Assert.fail("Should not be able to set segments at specific points");
        } catch (UnsupportedOperationException ex) {

        }
    }

    @Test
    public void testCompareTo() throws Exception {
        Polynomial p = new Polynomial(new double[]{4, 5});
        SliceSegment<Float> s1 = new SliceSegment<Float>(3f, 4f, 100, p);
        SliceSegment<Float> s2 = new SliceSegment<Float>(4f, 5f, 103, p);

        SliceSegment<Float> s3 = new SliceSegment<Float>(1f, 2f, 1, p);
        SliceSegment<Float> s4 = new SliceSegment<Float>(3f, 6f, 2, p);

        Slice<Float> sl1 = new Slice<Float>();
        sl1.add(s2);
        sl1.add(s1);

        Slice<Float> sl2 = new Slice<Float>();
        sl2.add(s3);
        sl2.add(s4);

        Assert.assertTrue(sl2.compareTo(sl1) > 0);

    }

    @Test
    public void testOrdering() throws Exception {
        Polynomial p = new Polynomial(new double[]{4, 5});
        SliceSegment<Float> s1 = new SliceSegment<Float>(3f, 4f, 100, p);
        SliceSegment<Float> s2 = new SliceSegment<Float>(4f, 5f, 103, p);

        Slice<Float> s = new Slice<Float>();
        s.add(s2);
        s.add(s1);

        Assert.assertTrue(s.indexOf(s1) < s.indexOf(s2));
    }
}
