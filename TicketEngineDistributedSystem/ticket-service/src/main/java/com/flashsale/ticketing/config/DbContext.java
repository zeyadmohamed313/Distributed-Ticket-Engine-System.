package com.flashsale.ticketing.config;

public class DbContext {
    // Thread-local storage for current database shard (SHARD_1 or SHARD_2)
    private static final ThreadLocal<String> currentDb = new ThreadLocal<>();

    public static void setDbType(String dbType) {
        currentDb.set(dbType);
    }

    public static String getDbType() {
        return currentDb.get();
    }

    // Clear context (important: thread returns to pool and may be reused)
    public static void clear() {
        currentDb.remove();
    }
}
