package blue.lhf.varpu.vector;

import java.util.Collection;
import java.util.stream.DoubleStream;

import static java.lang.Math.sqrt;

/**
 * <p>Represents an element in ℝN, where N is any positive integer.</p>
 * */
public interface RVector<Self extends RVector<Self>> extends IVector<Double, Self> {
    default Double distance(final Self other) {
        return difference(other).length();
    }

    default Self difference(final Self that) {
        return sum(that.product(-1.0));
    }

    default Self inverse() {
        return product(-1.0);
    }

    default Self normalised() {
        return product(1 / length());
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
        double sum = 0D;
        for (final Double component : components()) {
            sum += component * component;
        }

        return sqrt(sum);
    }
}
