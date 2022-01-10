package net.jodah.typetools;

import java.lang.reflect.AccessibleObject;
import net.jodah.typetools.TypeResolver;
import sun.misc.Unsafe;

final class TypeResolver$3
implements TypeResolver.AccessMaker {
    final Unsafe val$unsafe;
    final long val$overrideFieldOffset;

    TypeResolver$3(Unsafe unsafe, long l) {
        this.val$unsafe = unsafe;
        this.val$overrideFieldOffset = l;
    }

    @Override
    public void makeAccessible(AccessibleObject accessibleObject) {
        this.val$unsafe.putBoolean((Object)accessibleObject, this.val$overrideFieldOffset, true);
    }
}
