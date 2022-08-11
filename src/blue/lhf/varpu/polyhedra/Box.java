package blue.lhf.varpu.polyhedra;

import blue.lhf.varpu.vector.*;

import static blue.lhf.varpu.vector.Quaternion.pure;
import static blue.lhf.varpu.vector.Ternion.*;
import static java.lang.Math.*;

@SuppressWarnings("unused")
public record Box(Ternion origin, Ternion a, Ternion b, Ternion c) implements Orthotope<Double, Box> {
    public static Box box(final Ternion one, final Ternion two) {
        return box(one, two, null);
    }

    public static Box box(final Ternion one, final Ternion two, final Quaternion transformation) {
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

        return transformation != null ? aligned.transform(transformation) : aligned;
    }

    public Box offset(final Ternion offset) {
        return new Box(origin.sum(offset), a, b, c);
    }

    public Ternion centre() {
        return origin.sum(a.product(0.5)).sum(b.product(0.5)).sum(c.product(0.5));
    }

    public Box rotate(final Quaternion rotation) {
        final Box t = transform(rotation);
        return new Box(
            centre().sum(rotation.product(pure(origin.sum(centre().product(-1D)))).product(rotation.conjugate()).toTernion()),
            t.a, t.b, t.c
        );
    }

    public Box transform(final Quaternion transformation) {
        return new Box(
            origin,
            transformation.product(pure(a)).product(transformation.conjugate()).toTernion(),
            transformation.product(pure(b)).product(transformation.conjugate()).toTernion(),
            transformation.product(pure(c)).product(transformation.conjugate()).toTernion()
        );
    }

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

    public Double volume() {
        return a.length() * b.length() * c.length();
    }
}
