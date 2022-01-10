package me.zero.alpine.fork.listener;

@FunctionalInterface
public interface EventHook<T> {
    public void invoke(T var1);
}
