package blue.lhf.varpu.tests;

import static blue.lhf.varpu.vector.Quaternion.euler;
import static blue.lhf.varpu.vector.Ternion.ternion;
import static java.lang.Math.*;

@SuppressWarnings("unused")
public class VarpuTest {
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
