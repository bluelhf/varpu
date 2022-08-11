package blue.lhf.varpu.vector;

import java.text.DecimalFormat;
import java.util.*;

import static java.util.stream.Collectors.joining;

/**
 * <p>
 *     A vector of three real values, <i>x</i>, <i>y</i>, and <i>z</i>,
 *     representing the vector <i>x</i> 𝐢 + <i>y</i> 𝐣 + <i>z</i> 𝐤.
 * </p>
 * <p>
 *     If you've studied vector arithmetic, this is just a 3D vector with a fancy name to fit in with the quaternions.
 * </p>
 * */
public record Ternion(Double x, Double y, Double z) implements RVector<Ternion> {
    public static final Ternion ZERO = ternion(0, 0, 0);

    public static Ternion ternion(final double x, final double y, final double z) {
        return new Ternion(x, y, z);
    }

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public Ternion sum(Ternion that) {
        return new Ternion(this.x + that.x, this.y + that.y, this.z + that.z);
    }

    @Override
    public Ternion product(Double that) {
        return new Ternion(this.x * that, this.y * that, this.z * that);
    }

    @Override
    public Collection<Double> components() {
        return List.of(x, y, z);
    }

    @Override
    public String toString() {
        final var fmt = new DecimalFormat("#.######");
        final var builder = new StringBuilder("(");
        class EquationBuilder {
            boolean first = true;

            public void addTerm(final double value, final String parameter) {
                if (!first) builder.append(value >= 0 ? " + " : " - ");
                builder.append(fmt.format(Math.abs(value))).append(parameter);
                first = false;
            }
        }

        final EquationBuilder e = new EquationBuilder();
        e.addTerm(x, "\uD835\uDC22");
        e.addTerm(y, "\uD835\uDC23");
        e.addTerm(z, "\uD835\uDC24");
        builder.append(")");

        return builder.toString();
    }
}
