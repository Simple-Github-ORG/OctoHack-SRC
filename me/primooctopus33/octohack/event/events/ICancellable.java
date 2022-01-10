package me.primooctopus33.octohack.event.events;

public interface ICancellable {
    public void cancel();

    public boolean isCancelled();
}
