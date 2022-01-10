package net.jodah.typetools;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.AccessibleObject;
import net.jodah.typetools.TypeResolver;

final class TypeResolver$4
implements TypeResolver.AccessMaker {
    final MethodHandle val$overrideSetter;

    TypeResolver$4(MethodHandle methodHandle) {
        this.val$overrideSetter = methodHandle;
    }

    @Override
    public void makeAccessible(AccessibleObject object) throws Throwable {
        this.val$overrideSetter.invokeWithArguments(object, true);
    }
}
