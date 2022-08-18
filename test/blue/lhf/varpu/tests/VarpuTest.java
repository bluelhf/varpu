package blue.lhf.varpu.tests;

import blue.lhf.varpu.polyhedra.Box;
import blue.lhf.varpu.vector.Ternion;
import processing.core.PApplet;
import processing.core.PSketch;

import java.util.concurrent.atomic.AtomicLong;

import static blue.lhf.varpu.polyhedra.Box.box;
import static blue.lhf.varpu.vector.Quaternion.euler;
import static blue.lhf.varpu.vector.Quaternion.quaternion;
import static blue.lhf.varpu.vector.Ternion.ternion;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

@SuppressWarnings("unused")
public class VarpuTest {
    public static class Applet extends PApplet {
        final Box central = Box.box(
            ternion(-25, -25, -25),
            ternion(25, 25, 25),
            euler(toRadians(-20), toRadians(30), toRadians(-45))
        );
        Box other = Box.box(
            ternion(30, 30, 30),
            ternion(60, 60, 60));
        public static void main(String[] args) {
            Applet.main(Applet.class, "Sketch");
        }

        @Override
        public void settings() {
            size(666, 666, P3D);
        }

        @Override
        public void draw() {
            background(0);
            lights();
            fill(127, 127, 127);
            stroke(0xFFFFFFFF);
            strokeWeight(1);

            translate(width / 2F, height / 2F, 0);
            push();

            translate(
                central.centre().x().floatValue(),
                central.centre().y().floatValue(),
                central.centre().z().floatValue());

            beginShape(LINES);
            for (final Ternion[] edge : central.edges()) {
                vertex(
                    edge[0].x().floatValue(),
                    edge[0].y().floatValue(),
                    edge[0].z().floatValue()
                );
                vertex(
                    edge[1].x().floatValue(),
                    edge[1].y().floatValue(),
                    edge[1].z().floatValue()
                );
            }
            endShape();

            pop();
            push();
            float t = System.nanoTime() / 5E8F;
            final Box trueOther = other.offset(ternion(
                mouseX - width / 2F, mouseY - height / 2F, sin(t) * 150
                ));
            if (central.intersects(trueOther)) {
                fill(255, 0, 0);
            }
            if (central.centre().z().intValue() == trueOther.centre().z().intValue()) {
                System.out.println("same z");
                trueOther.intersects(central);
            }
            translate(
                trueOther.centre().x().floatValue(),
                trueOther.centre().y().floatValue(),
                trueOther.centre().z().floatValue()
            );
            box(
                trueOther.a().length().floatValue(),
                trueOther.b().length().floatValue(),
                trueOther.c().length().floatValue()
            );
            pop();
        }
    }
    public void testIntersections() {
        final Box box = box(ternion(0, 0, 0), ternion(1, 1, 1), euler(toRadians(-45), 0, 0));
        final Box other = new Box(
            ternion(0.91, 1.31, 0.9),
            ternion(-0.01, -0.61, 0),
            ternion(-0.1, 0, 0.6),
            ternion(0.6, -0.01, 0.1)
        );

        System.out.println(box.intersects(other));
        final AtomicLong counter = new AtomicLong();
        final long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 5000) {
            final Box a = box(ternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ),
                ternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ),
                quaternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ));
            final Box b = box(ternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ),
                ternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ),
                quaternion(
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200,
                    Math.random() * 200
                ));
            a.intersects(b);
            counter.incrementAndGet();
        }
        System.out.println("Did " + counter.get() + " intersection tests in 5 seconds");
        System.out.println(counter.get() / 5D / 1000 + " collisions per millisecond");
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
