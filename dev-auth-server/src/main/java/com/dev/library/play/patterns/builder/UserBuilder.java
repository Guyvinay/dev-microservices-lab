package com.dev.library.play.patterns.builder;

public class UserBuilder {
    private final String name;
    private final int age;
    private final String role;

    // Private constructor - only builder can create
    private UserBuilder(Builder builder) {
        this.name = builder.name;
        this.age = builder.age;
        this.role = builder.role;
    }

    public static class Builder {
        private String name;
        private int age;
        private String role;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder role(String role) {
            this.role = role;
            return this;
        }

        public UserBuilder build() {
            return new UserBuilder(this);
        }
    }
}
