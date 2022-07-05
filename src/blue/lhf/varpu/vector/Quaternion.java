package blue.lhf.varpu.vector;

import java.text.DecimalFormat;

import static java.lang.Math.*;

/**
 * <p>
 *      A record of four real values, <i>r</i>, <i>x</i>, <i>y</i>, and <i>z</i>, representing the quaternion
 *      <i>r</i> + <i>x</i> 𝐢 + <i>y</i> 𝐣 + <i>z</i> 𝐤.
 * </p>
 * <p>
 *     Useful for describing orientation in 3D space, especially in cases where traditional
 *     euler angles might suffer from <a href="https://en.wikipedia.org/wiki/Gimbal_lock">gimbal lock</a>.
 * </p>
 * <p>
 *     This class implements {@link IVector}, making instances of this class elements of a four-dimensional
 *     real vector space. Additionally, several important quaternion operations, for example the hamilton product,
 *     are supported.
 * </p>
 * */
public record Quaternion(double r, double x, double y, double z) implements IVector<Double, Quaternion> {
    public static Quaternion euler(double yaw, double pitch, double roll) {
        return new Quaternion(
            cos(roll / 2) * cos(pitch / 2) * cos(yaw / 2) + sin(roll / 2) * sin(pitch / 2) * sin(yaw / 2),
            sin(roll / 2) * cos(pitch / 2) * cos(yaw / 2) - cos(roll / 2) * sin(pitch / 2) * sin(yaw / 2),
            cos(roll / 2) * sin(pitch / 2) * cos(yaw / 2) + sin(roll / 2) * cos(pitch / 2) * sin(yaw / 2),
            cos(roll / 2) * cos(pitch / 2) * sin(yaw / 2) - sin(roll / 2) * sin(pitch / 2) * cos(yaw / 2)
        );
    }

    @Override
    public int dimension() {
        return 4;
    }

    @Override
    public Quaternion sum(Quaternion that) {
        return new Quaternion(
            (this.r + that.r),
            (this.x + that.x),
            (this.y + that.y),
            (this.z + that.z)
        );
    }

    @Override
    public Quaternion product(Double that) {
        return new Quaternion(
            (this.r * that),
            (this.x * that),
            (this.y * that),
            (this.z * that)
        );
    }

    public Quaternion product(Quaternion that) {
        // writing this was fun
        return new Quaternion(
            (this.r * that.r) - (this.x * that.x) - (this.y * that.y) - (this.z * that.z),
            (this.r * that.x) + (this.x * that.r) + (this.y * that.z) - (this.z * that.y),
            (this.r * that.y) - (this.x * that.z) + (this.y * that.r) + (this.z * that.x),
            (this.r * that.z) + (this.x * that.y) - (this.y * that.x) + (this.z * that.r)
        );
    }

    public Double angle(Quaternion that) {
        return acos(2 * pow(versor().innerProduct(that.versor()), 2) - 1);
    }

    /**
     * If you don't know what this does, you probably want {@link Quaternion#product(Quaternion)}
     *
     * @return The inner product of this quaternion with the input quaternion
     */
    public Double innerProduct(Quaternion that) {
        return (this.r * that.r)
            + (this.x * that.x)
            + (this.y * that.y)
            + (this.z * that.z);
    }

    public Quaternion versor() {
        return product(1 / norm());
    }

    public Quaternion reciprocal() {
        return conjugate().product(1 / normSq());
    }

    public Quaternion conjugate() {
        return new Quaternion(this.r, -this.x, -this.y, -this.z);
    }

    public Double norm() {
        return Math.sqrt(normSq());
    }

    public Double normSq() {
        return this.r * this.r
            + this.x * this.x
            + this.y * this.y
            + this.z * this.z;
    }

    @Override
    public String toString() {
        final var fmt = new DecimalFormat("#.######");
        final var builder = new StringBuilder("(");
        if (this.r != 0) builder.append(fmt.format(r));
        if (this.x != 0)
            builder.append(this.x > 0 ? " + " : " - ").append(fmt.format(Math.abs(x))).append("\uD835\uDC22");
        if (this.y != 0)
            builder.append(this.y > 0 ? " + " : " - ").append(fmt.format(Math.abs(y))).append("\uD835\uDC23");
        if (this.z != 0)
            builder.append(this.z > 0 ? " + " : " - ").append(fmt.format(Math.abs(z))).append("\uD835\uDC24");
        builder.append(")");

        return builder.toString();
    }
}
