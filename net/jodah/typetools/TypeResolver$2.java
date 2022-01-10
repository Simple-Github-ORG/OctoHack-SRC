package net.jodah.typetools;

import java.lang.reflect.AccessibleObject;
import net.jodah.typetools.TypeResolver;

final class TypeResolver$2
implements TypeResolver.AccessMaker {
    TypeResolver$2() {
    }

    @Override
    public void makeAccessible(AccessibleObject accessibleObject) {
        accessibleObject.setAccessible(true);
    }
}
