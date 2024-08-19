package com.Dylan.www.util;

public interface Peekable<T> {

    T[] peekBack();

    default T[] peekForward() { return null; }

}
