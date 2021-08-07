package toadsworthlp.funkykart.util;

public class StateMachine<T> {
    private IState<T> currentState;
    private T target;
    private int stateChangeTimer = 0;

    private final StateChangeObserver<T> observer;

    public interface StateChangeObserver<T> {
        void stateChanged(IState<T> previousState, IState<T> newState);
    }

    public StateMachine(T target, IState<T> initialState, StateChangeObserver<T> stateChangeObserver) {
        currentState = initialState;
        this.target = target;
        this.observer = stateChangeObserver;
    }

    public void tick() {
        currentState.tick(target);
        stateChangeTimer++;
    }

    public IState<T> getState() {
        return currentState;
    }

    public void setState(IState<T> newState) {
        IState<T> previousState = currentState;

        currentState.exit(target, newState);
        stateChangeTimer = 0;
        observer.stateChanged(previousState, newState);
        newState.enter(target, currentState);
        currentState = newState;

        tick();
    }

    public int getStateChangeTime() {
        return stateChangeTimer;
    }
}
