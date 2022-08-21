package blue.lhf.varpu.tests;

import blue.lhf.varpu.polyhedra.Box;
import org.junit.jupiter.api.Test;

import static blue.lhf.varpu.polyhedra.Box.box;
import static blue.lhf.varpu.vector.Quaternion.euler;
import static blue.lhf.varpu.vector.Ternion.ternion;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unused")
class VarpuTest {
    @Test
    void testBoxes() {
        final Box box = Box.empty()
            .sized(ternion(5, 5, 5))
            .centred(ternion(30, 30, 30))
            .rotated(euler(0, 0, toRadians(-45)));

        final Box expected = new Box(
            ternion(27.5, 30 - 5 / sqrt(2), 30),
            ternion(5, 0, 0),
            ternion(0, 5 / sqrt(2), -5 / sqrt(2)),
            ternion(0, 5 / sqrt(2), 5 / sqrt(2))
        );

        assertTrue(box.isSimilar(expected, 0.1));
    }

    @Test
    void testIntersections() {
        final Box box = box(ternion(0, 0, 0), ternion(1, 1, 1), euler(toRadians(-45), 0, 0));
        final Box other = new Box(
            ternion(0.91, 1.31, 0.9),
            ternion(-0.01, -0.61, 0),
            ternion(-0.1, 0, 0.6),
            ternion(0.6, -0.01, 0.1)
        );
        assertTrue(box.intersects(other), "Failed intersection that should've passed");
    }

    @Test
    void testQuaternions() {
        assertEquals(euler(0, 0, toRadians(-45))
                .angle(euler(0, 0, toRadians(90))), toRadians(135),
            "Quaternion angle was incorrect");
    }

    public void testDistance() {
        assertEquals(ternion(-1.0, -1.0, -1.0)
                .distance(ternion(1.0, 1.0, 1.0)), sqrt(12),
            "Ternions miscomputed distance");
    }
}
