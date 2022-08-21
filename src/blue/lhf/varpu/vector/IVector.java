package blue.lhf.varpu.vector;

import java.util.Collection;

/**
 * An interface for elements of vector spaces of arbitrary dimension.
 * @param <S> The scalar type of the vector space
 * @param <Self> A nasty trick to allow implementations to only support operations on themselves
 * */
public interface IVector<S, Self extends IVector<S, Self>> {
    int dimension();
    Self sum(Self that);
    Self product(S that);
    S[] components();
}
