package ru.shadam.ferry.util;

/**
 * @author sala
 */
public class MoreObjects {
    public static <T> T requireNonNull(T arg) {
        if(arg == null) {
            throw new NullPointerException();
        }
        return arg;
    }
}
