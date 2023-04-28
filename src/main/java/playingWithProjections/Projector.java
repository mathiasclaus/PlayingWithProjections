package playingWithProjections;

public interface Projector<T>{
    T getResult();

    void projection(Event event);
}
