package com.bankingsystem.model;

import java.util.UUID;

/**
 * Simple ID generator utility.
 */
public final class IdUtil {
    private IdUtil() {}

    public static String nextId(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
