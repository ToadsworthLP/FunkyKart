package toadsworthlp.funkykart.util;

public interface IState<T> {
    void enter(T target, IState<T> previous);
    void tick(T target);
    void exit(T target, IState<T> next);
}
