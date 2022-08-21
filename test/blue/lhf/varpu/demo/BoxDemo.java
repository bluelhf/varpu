package blue.lhf.varpu.demo;

import blue.lhf.varpu.polyhedra.Box;
import blue.lhf.varpu.vector.Ternion;
import processing.core.PApplet;

import static blue.lhf.varpu.vector.Quaternion.euler;
import static blue.lhf.varpu.vector.Ternion.ZERO;
import static blue.lhf.varpu.vector.Ternion.ternion;

public class BoxDemo extends PApplet {
    Box central = Box.boxAt(ZERO, ternion(100, 30, 45));
    Box other = Box.box(ZERO, ternion(69, 27, 30));

    public static void main(String[] args) {
        BoxDemo.main(BoxDemo.class, "Sketch");
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

        central = central.rotated(euler(0.01, 0.02, 0.03));

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
        other = other.centred(ternion(mouseX - width / 2F, mouseY - height / 2F, 0)).rotated(euler(0.03, 0.02, 0.01));

        if (central.intersects(other)) {
            stroke(255, 0, 0);
        }

        beginShape(LINES);
        for (final Ternion[] edge : other.edges()) {
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
    }
}
