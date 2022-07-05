package blue.lhf.varpu.vector;

/**
 * <p>
 *     A vector of three real values, <i>x</i>, <i>y</i>, and <i>z</i>,
 *     representing the vector <i>x</i> 𝐢 + <i>y</i> 𝐣 + <i>z</i> 𝐤.
 * </p>
 * <p>
 *     If you've studied vector arithmetic, this is just a 3D vector with a fancy name to fit in with the quaternions.
 * </p>
 * */
public record Ternion(Double x, Double y, Double z) implements IVector<Double, Ternion> {
    @Override
    public int dimension() {
        return 3;
    }


    @Override
    public Ternion sum(Ternion that) {
        return new Ternion(this.x + that.x, this.y + that.y, this.z + that.z);
    }

    @Override
    public Ternion product(Double that) {
        return new Ternion(this.x * that, this.y * that, this.z * that);
    }
}
