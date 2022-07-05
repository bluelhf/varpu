package blue.lhf.varpu.tests;

import static blue.lhf.varpu.vector.Quaternion.euler;
import static java.lang.Math.toRadians;

@SuppressWarnings("unused")
public class VarpuTest {
    public void testQuaternions() {
        assert euler(0, 0, toRadians(-45))
            .angle(euler(0, 0, toRadians(90))) == toRadians(135):
            "Quaternion angle was incorrect";
    }
}
