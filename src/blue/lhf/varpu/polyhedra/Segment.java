package blue.lhf.varpu.polyhedra;

public interface Segment<P, Self extends Segment<P, Self>> {
    P a();
    P b();
}
