package me.zero.alpine.fork.event.type;

public interface ICancellable {
    public void cancel();

    public boolean isCancelled();
}
