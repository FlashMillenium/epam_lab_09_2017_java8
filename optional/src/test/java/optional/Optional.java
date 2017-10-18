package optional;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Optional<T> {

    private static final Optional<?> EMPTY = new Optional<>();

    private final T value;

    private Optional() {
        value = null;
    }

    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    public static <T> Optional<T> of(T value) {
        return new Optional<>(value);
    }

    public static <T> Optional<T> empty() {
        return (Optional<T>) EMPTY;
    }

    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    public boolean isPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<T> action) {
        if (isPresent()) {
            action.accept(value);
        }
    }

    public T get() {
        if (!isPresent()) {
            throw new NoSuchElementException("Haven't value");
        }
        return value;
    }

    public T orElse(T defaultValue) {
        return isPresent() ? value : defaultValue;
    }

    public T orElseGet(Supplier<T> supplier) {
        return isPresent() ? value : supplier.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isPresent()) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        if (isPresent()) {
            return predicate.test(value) ? this : empty();
        } else {
            return empty();
        }
    }

    public <R> Optional<R> map(Function<? super T, ? extends R> mapper) {
        if (isPresent()) {
            return ofNullable(mapper.apply(value));
        } else {
            return empty();
        }
    }

    public <R> Optional<R> flatMap(Function<? super T, Optional<R>> mapper) {
        if (isPresent()) {
            return mapper.apply(value);
        } else {
            return empty();
        }
    }
}









