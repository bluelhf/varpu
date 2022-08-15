package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

public interface Orthotope<S, Self extends Orthotope<S, Self>> {
    /**
     * @return The dimension of this orthotope, i.e., how many origin-connected edges it has.
     * */
    int dimension();
    /**
     * @return The origin point of this orthotope.
     * */
    IVector<S, ?> origin();
    /**
     * @return The origin-connected edges of this orthotope.
     * */
    IVector<S, ?>[] originalEdges();
}
