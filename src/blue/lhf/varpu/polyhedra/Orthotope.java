package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

import java.util.Iterator;

public interface Orthotope<V extends RVector<V>, Self extends Orthotope<V, Self>> {
    /**
     * @return The dimension of this orthotope, i.e., how many origin-connected edges it has.
     * */
    int dimension();
    /**
     * @return The origin point of this orthotope.
     * */
    V origin();
    /**
     * @return The origin-connected edges of this orthotope.
     * */
    V[] originalEdges();

    default V centre() {
        V centre = origin();
        for (V original : originalEdges()) centre = centre.sum(original.product(0.5));
        return centre;
    }

    default V[] halves() {
        final V[] halves = originalEdges();
        for (int i = 0; i < halves.length; ++i) halves[i] = halves[i].product(0.5D);
        return halves;
    }

    default Iterable<V> orthonormals() {
        final Iterator<V> iterator = new Iterator<>() {
            private final V[] originals = originalEdges();
            private int i = -1;
            @Override
            public boolean hasNext() {
                return i < originals.length - 1;
            }

            @Override
            public V next() {
                return originals[++i].normalised();
            }
        };

        return () -> iterator;
    }


    default Double[] extents() {
        final RVector<?>[] originals = originalEdges();
        final Double[] extents = new Double[dimension()];
        for (int i = 0; i < originals.length; ++i) {
            extents[i] = originals[i].length() / 2;
        }

        return extents;
    }
}
