package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

import static blue.lhf.varpu.vector.Quaternion.pure;
import static blue.lhf.varpu.vector.Ternion.*;
import static java.lang.Math.*;

/**
 * <p>
 *     Represents a 3-orthotope, i.e. a box.
 * </p>
 * <p>
 *     A box, in varpu, maintains a record of its origin vertex and the
 *     three orthogonal edges the origin connects to.
 * </p>
 * <p>
 *     Boxes support several methods for simple quaternion transformation.
 * </p>
 * */
@SuppressWarnings("unused")
public record Box(Ternion origin, Ternion a, Ternion b, Ternion c) implements Orthotope<Double, Box> {
    public Box(final Ternion origin, final Ternion a, final Ternion b, final Ternion c) {
        this.origin = origin;
        this.a = a; this.b = b; this.c = c;
        ensureOrthogonal();
    }

    /**
     * Ensures that this box' three origin-connected edges are orthogonal.
     * @throws IllegalArgumentException If they are not.
     * */
    private void ensureOrthogonal() {
        final IllegalArgumentException ex = new IllegalArgumentException(
                "A box' three origin-connected edges must be orthogonal.");
        if (a.dot(b) != 0) throw ex;
        if (b.dot(c) != 0) throw ex;
        if (a.dot(c) != 0) throw ex;
    }

    /**
     * @return An axis-aligned box with two opposing corners at the two points.
     * */
    public static Box box(final Ternion one, final Ternion two) {
        return box(one, two, null);
    }

    /**
     * @return A box constructed by rotating
     * the axis-aligned box constructed from the specified points around its centre by
     * the given {@link Quaternion} rotation.
     * */
    public static Box box(final Ternion one, final Ternion two, final Quaternion rotation) {
        final Ternion min = ternion(
            min(one.x(), two.x()),
            min(one.y(), two.y()),
            min(one.z(), two.z())
        );

        final Ternion max = ternion(
            max(one.x(), two.x()),
            max(one.y(), two.y()),
            max(one.z(), two.z())
        );

        final Box aligned = new Box(min,
            ternion(max.x() - min.x(), 0, 0),
            ternion(0, max.y() - min.y(), 0),
            ternion(0, 0, max.z() - min.z()));

        return rotation != null ? aligned.rotate(rotation) : aligned;
    }

    /**
     * @return A box equivalent to this one with its origin offset by the given {@link Ternion}.
     * @param offset The ternion by which to offset the box' origin.
     * */
    public Box offset(final Ternion offset) {
        return new Box(origin.sum(offset), a, b, c);
    }

    public Ternion centre() {
        return origin.sum(a.product(0.5)).sum(b.product(0.5)).sum(c.product(0.5));
    }

    /**
     * @return A box equivalent to this one rotated around the <b>centre</b> of the box by the given quaternion rotation.
     * @param rotation The quaternion rotation to apply.
     * @see Box#transform(Quaternion)
     * */
    public Box rotate(final Quaternion rotation) {
        final Box t = transform(rotation);
        return new Box(
            centre().sum(rotation.product(pure(origin.sum(centre().product(-1D)))).product(rotation.conjugate()).toTernion()),
            t.a, t.b, t.c
        );
    }

    /**
     * @return A box equivalent to this one rotated around the <b>origin</b> of the box by the given quaternion rotation.
     * @param rotation The quaternion rotation to apply.
     * @see Box#rotate(Quaternion)
     * */
    public Box transform(final Quaternion rotation) {
        return new Box(
            origin,
            rotation.product(pure(a)).product(rotation.conjugate()).toTernion(),
            rotation.product(pure(b)).product(rotation.conjugate()).toTernion(),
            rotation.product(pure(c)).product(rotation.conjugate()).toTernion()
        );
    }

    /**
     * @return An array of all 8 of this box' vertices
     * */
    public Ternion[] vertices() {
        return new Ternion[]{
            origin.sum(ZERO).sum(ZERO).sum(ZERO),
            origin.sum(ZERO).sum(ZERO).sum(   c),
            origin.sum(ZERO).sum(   b).sum(   c),
            origin.sum(ZERO).sum(   b).sum(ZERO),
            origin.sum(   a).sum(ZERO).sum(ZERO),
            origin.sum(   a).sum(ZERO).sum(   c),
            origin.sum(   a).sum(   b).sum(ZERO),
            origin.sum(   a).sum(   b).sum(   c),
        };
    }

    /**
     * @return An array of all 12 of this box' edges.
     * */
    public Ternion[][] edges() {
        return new Ternion[][]{
            {origin, origin.sum(a)},
            {origin, origin.sum(b)},
            {origin, origin.sum(c)},

            {origin.sum(a).sum(b).sum(c), origin.sum(a).sum(b).sum(c).sum(a.product(-1D))},
            {origin.sum(a).sum(b).sum(c), origin.sum(a).sum(b).sum(c).sum(b.product(-1D))},
            {origin.sum(a).sum(b).sum(c), origin.sum(a).sum(b).sum(c).sum(c.product(-1D))},

            {origin.sum(c), origin.sum(c).sum(a)},
            {origin.sum(c), origin.sum(c).sum(b)},

            {origin.sum(b), origin.sum(b).sum(c)},
            {origin.sum(a), origin.sum(a).sum(c)},
            {origin.sum(a).sum(b), origin.sum(a)},
            {origin.sum(a).sum(b), origin.sum(b)}
        };
    }

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public Ternion[] originalEdges() {
        return new Ternion[]{a, b, c};
    }

    /**
     * @return The volume of this {@link Box}, i.e., the product of its three origin-connected edges.
     * */
    public Double volume() {
        return a.length() * b.length() * c.length();
    }
}
