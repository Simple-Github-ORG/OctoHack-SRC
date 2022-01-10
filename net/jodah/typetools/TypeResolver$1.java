package net.jodah.typetools;

import java.lang.reflect.Field;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

final class TypeResolver$1
implements PrivilegedExceptionAction<Unsafe> {
    TypeResolver$1() {
    }

    @Override
    public Unsafe run() throws Exception {
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        return (Unsafe)f.get(null);
    }
}
