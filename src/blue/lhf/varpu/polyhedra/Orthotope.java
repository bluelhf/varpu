package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

public interface Orthotope<S, Self extends Orthotope<S, Self>> {
    int dimension();
    IVector<S, ?> origin();
    IVector<S, ?>[] originalEdges();
}
