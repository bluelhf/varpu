package blue.lhf.varpu.tests;

import blue.lhf.varpu.polyhedra.Box;

import static blue.lhf.varpu.polyhedra.Box.box;
import static blue.lhf.varpu.vector.Quaternion.euler;
import static blue.lhf.varpu.vector.Ternion.ternion;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

@SuppressWarnings("unused")
public class VarpuTest {
    public void testIntersections() {
        final Box box = box(ternion(0, 0, 0), ternion(1, 1, 1), euler(toRadians(-45), 0, 0));
        final Box other = new Box(
            ternion(0.91, 1.31, 0.9),
            ternion(-0.01, -0.61, 0),
            ternion(-0.1, 0, 0.6),
            ternion(0.6, -0.01, 0.1)
        );
        assert box.intersects(other) : "Failed intersection that should've passed";
    }

    public void testQuaternions() {
        assert euler(0, 0, toRadians(-45))
            .angle(euler(0, 0, toRadians(90))) == toRadians(135) :
            "Quaternion angle was incorrect";
    }

    public void testDistance() {
        assert ternion(-1.0, -1.0, -1.0)
            .distance(ternion(1.0, 1.0, 1.0)) == sqrt(12) :
            "Ternions miscomputed distance";
    }
}
