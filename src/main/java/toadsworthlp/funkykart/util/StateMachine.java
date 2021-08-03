package toadsworthlp.funkykart.util;

public class StateMachine<T> {
    private IState<T> currentState;
    private T target;
    private int stateDuration;

    public StateMachine(T target, IState<T> initialState) {
        currentState = initialState;
        this.target = target;
    }

    public void tick() {
        currentState.tick(target);
        stateDuration++;
    }

    public IState<T> getState() {
        return currentState;
    }

    public void setState(IState<T> newState) {
        currentState.exit(target, newState);
        stateDuration = 0;
        newState.enter(target, currentState);
        currentState = newState;
        tick();
    }

    public int getStateDuration() {
        return stateDuration;
    }
}
