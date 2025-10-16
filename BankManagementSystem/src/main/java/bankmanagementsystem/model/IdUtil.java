package bankmanagementsystem.model;

import java.util.UUID;

public final class IdUtil {
    private IdUtil() {}
    public static String nextId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
