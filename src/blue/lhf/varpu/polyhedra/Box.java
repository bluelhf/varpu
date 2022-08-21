package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

import java.util.Objects;

import static blue.lhf.varpu.vector.Quaternion.pure;
import static blue.lhf.varpu.vector.Ternion.ORIGO;
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

        return rotation != null ? aligned.rotate(rotation) : aligned;
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
    public Box rotate(final Quaternion rotation) {
        final Box t = transform(rotation);
        return new Box(
            centre().sum(rotation.product(pure(origin.sum(centre().product(-1D)))).product(rotation.conjugate()).toTernion()),
            t.a, t.b, t.c
        );
    }

    /**
     * @param rotation The quaternion rotation to apply.
     * @return A box equivalent to this one rotated around the <b>origin</b> of the box by the given quaternion rotation.
     * @see Box#rotate(Quaternion)
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
            origin.sum(ORIGO).sum(ORIGO).sum(ORIGO),
            origin.sum(ORIGO).sum(ORIGO).sum(c),
            origin.sum(ORIGO).sum(b).sum(c),
            origin.sum(ORIGO).sum(b).sum(ORIGO),
            origin.sum(a).sum(ORIGO).sum(ORIGO),
            origin.sum(a).sum(ORIGO).sum(c),
            origin.sum(a).sum(b).sum(ORIGO),
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
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Box) obj;
        return Objects.equals(this.origin, that.origin) &&
            Objects.equals(this.a, that.a) &&
            Objects.equals(this.b, that.b) &&
            Objects.equals(this.c, that.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, a, b, c);
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
}
