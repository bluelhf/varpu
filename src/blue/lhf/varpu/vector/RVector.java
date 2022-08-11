package blue.lhf.varpu.vector;

import java.util.Collection;
import java.util.stream.*;

import static java.lang.Math.sqrt;

public interface RVector<Self extends RVector<Self>> extends IVector<Double, Self> {
    default Double distance(final RVector<Self> other) {
        final Self anti = this.product(-1D);
        final Self sub = other.sum(anti);

        double sum = 0D;
        for (final Double component : sub.components()) {
            sum += component * component;
        }

        return sqrt(sum);
    }

    record ZeroVector<Self extends RVector<Self>>(int dimension) implements RVector<Self> {
        @Override
        public Self sum(Self that) {
            return that;
        }

        @Override
        public Self product(Double that) {
            return (Self) this;
        }

        @Override
        public Collection<Double> components() {
            return DoubleStream.generate(() -> 0D).limit(this.dimension()).boxed().toList();
        }
    }

    default Double length() {
        return distance(new ZeroVector<>(dimension()));
    }
}
