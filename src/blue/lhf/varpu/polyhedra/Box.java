package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

import java.util.Objects;

import static blue.lhf.varpu.vector.Quaternion.pure;
import static blue.lhf.varpu.vector.Ternion.ZERO;
import static blue.lhf.varpu.vector.Ternion.ternion;
import static java.lang.Math.*;

/**
 * <p>
 * Represents a 3-orthotope, i.e. a box.
 * </p>
 * <p>
 * A box, in varpu, maintains a record of its origin vertex and the
 * three orthogonal edges the origin connects to.
 * </p>
 * <p>
 * Boxes support several methods for simple quaternion transformation.
 * </p>
 */
@SuppressWarnings("unused")
public final class Box implements Orthotope<Ternion, Box> {
    private final Ternion origin;
    private final Ternion a;
    private final Ternion b;
    private final Ternion c;

    public Box(final Ternion origin, final Ternion a, final Ternion b, final Ternion c) {
        this.origin = origin;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * @return An axis-aligned box with two opposing corners at the two points.
     */
    public static Box box(final Ternion one, final Ternion two) {
        return box(one, two, null);
    }

    public static Box boxAt(final Ternion centre, final Ternion wdh) {
        return boxAt(centre, wdh, null);
    }
    public static Box boxAt(final Ternion centre, final Ternion wdh, final Quaternion rotation) {
        return box(centre.sum(wdh.product(-0.5D)), centre.sum(wdh.product(0.5D)), rotation);
    }

    public static Box empty() {
        return box(Ternion.ZERO, Ternion.ZERO);
    }

    /**
     * @return A box constructed by rotating
     * the axis-aligned box constructed from the specified points around its centre by
     * the given {@link Quaternion} rotation.
     */
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

        return rotation != null ? aligned.rotated(rotation) : aligned;
    }

    /**
     * @param offset The ternion by which to offset the box' origin.
     * @return A box equivalent to this one with its origin offset by the given {@link Ternion}.
     */
    public Box offset(final Ternion offset) {
        return new Box(origin.sum(offset), a, b, c);
    }

    /**
     * @param rotation The quaternion rotation to apply.
     * @return A box equivalent to this one rotated around the <b>centre</b> of the box by the given quaternion rotation.
     * @see Box#transform(Quaternion)
     */
    public Box rotated(final Quaternion rotation) {
        return transform(rotation).centred(centre());
    }

    /**
     * @param rotation The quaternion rotation to apply.
     * @return A box equivalent to this one rotated around the <b>origin</b> of the box by the given quaternion rotation.
     * @see Box#rotated(Quaternion)
     */
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
     */
    public Ternion[] vertices() {
        return new Ternion[]{
            origin.sum(ZERO).sum(ZERO).sum(ZERO),
            origin.sum(ZERO).sum(ZERO).sum(c),
            origin.sum(ZERO).sum(b).sum(c),
            origin.sum(ZERO).sum(b).sum(ZERO),
            origin.sum(a).sum(ZERO).sum(ZERO),
            origin.sum(a).sum(ZERO).sum(c),
            origin.sum(a).sum(b).sum(ZERO),
            origin.sum(a).sum(b).sum(c),
        };
    }

    /**
     * @return An array of all 12 of this box' edges.
     */
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

    public boolean intersects(final Box box) {
        final Ternion[] edges = originalEdges();
        final Ternion[] boxEdges = box.originalEdges();
        final Ternion diff = box.centre().difference(centre());

        for (final Ternion orthonormal : orthonormals())
            if (hasSeparatingPlane(diff, orthonormal, edges, boxEdges)) return false;

        for (final Ternion orthonormal : box.orthonormals())
            if (hasSeparatingPlane(diff, orthonormal, edges, boxEdges)) return false;

        for (final Ternion one : orthonormals())
            for (final Ternion two : box.orthonormals())
                if (hasSeparatingPlane(diff, one.cross(two), edges, boxEdges)) return false;

        return true;
    }

    private boolean hasSeparatingPlane(
        final Ternion centreDiff,
        final Ternion plane,
        final Ternion[] edgesA,
        final Ternion[] edgesB
    ) {
        return (
            abs(centreDiff.dot(plane)) > (
                abs(edgesA[0].product(0.5).dot(plane)) +
                    abs(edgesA[1].product(0.5).dot(plane)) +
                    abs(edgesA[2].product(0.5).dot(plane)) +
                    abs(edgesB[0].product(0.5).dot(plane)) +
                    abs(edgesB[1].product(0.5).dot(plane)) +
                    abs(edgesB[2].product(0.5).dot(plane))
            )
        );
    }

    @Override
    public int dimension() {
        return 3;
    }

    @Override
    public Ternion[] originalEdges() {
        return new Ternion[]{a, b, c};
    }

    @Override
    public Ternion[] halves() {
        final Ternion[] halves = originalEdges();
        for (int i = 0; i < halves.length; ++i) halves[i] = halves[i].product(0.5);
        return halves;
    }


    /**
     * @return The volume of this {@link Box}, i.e., the product of its three origin-connected edges.
     */
    public Double volume() {
        return a.length() * b.length() * c.length();
    }

    public Ternion origin() {
        return origin;
    }

    public Ternion a() {
        return a;
    }

    public Ternion b() {
        return b;
    }

    public Ternion c() {
        return c;
    }

    @Override
    public String toString() {
        return "Box[" +
            "origin=" + origin + ", " +
            "a=" + a + ", " +
            "b=" + b + ", " +
            "c=" + c + ']';
    }

    public Box centred(final Ternion ternion) {
        return new Box(ternion.difference(a.product(0.5).sum(b.product(0.5)).sum(c.product(0.5))), a, b, c);
    }

    public boolean similar(final Box that, final double error) {
        final Ternion[] ours = this.vertices();
        final Ternion[] theirs = that.vertices();
        for (int i = 0; i < ours.length; ++i) {
            final Ternion a = ours[i], b = theirs[i];
            if (abs(b.x() - a.x()) > error) return false;
            if (abs(b.y() - a.y()) > error) return false;
            if (abs(b.z() - a.z()) > error) return false;
        }
        return true;
    }

    public Box sized(final Ternion wdh) {
        return new Box(
            origin,
            a.normalisedOr(ternion(1, 0, 0)).product(wdh.x()),
            b.normalisedOr(ternion(0, 1, 0)).product(wdh.y()),
            c.normalisedOr(ternion(0, 0, 1)).product(wdh.z())
        ).centred(centre());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Box box = (Box) o;

        if (!origin.equals(box.origin)) return false;
        if (!a.equals(box.a)) return false;
        if (!b.equals(box.b)) return false;
        return c.equals(box.c);
    }

    @Override
    public int hashCode() {
        int result = origin.hashCode();
        result = 31 * result + a.hashCode();
        result = 31 * result + b.hashCode();
        result = 31 * result + c.hashCode();
        return result;
    }
}
