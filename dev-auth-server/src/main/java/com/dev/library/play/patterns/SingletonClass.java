package com.dev.library.play.patterns;

public class SingletonClass {

    /**
     * Ensures visibility of changes across threads.
     * This is critical because without volatile,
     * a thread might see a half-constructed object due to JVM optimizations.
     */
    private static volatile SingletonClass singletonInstance;

    // Guard against reflection; ideally this should be private
    private SingletonClass() {
        if(singletonInstance != null) {
            throw new RuntimeException("Cannot create class, instance already created");
        }
    }

    public static SingletonClass getInstance() {
        // First check (no locking) for performance
        if(singletonInstance == null) {

            // Synchronize only when instance is not created
            synchronized (SingletonClass.class) {

                // Second check to ensure only one thread initializes it
                if (singletonInstance == null) {
                    singletonInstance = new SingletonClass();
                }
            }
        }
        return singletonInstance; // Return the single instance
    }
}
