package server.domain;

import server.service.crypto.SaltStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class User {
    public static final User ANONYMOUS_USER = new User("anonymous", null, null, null);
    // private Long id; // for simplicity's sake
    private final String name;
    private final Set<Role> roles;
    private final byte[] pw;
    private final SaltStrategy pwSaltStrategy;
    // for simplicity's sake: pw and pwSaltStrategy directly stored with User;
    // user has dependency on SaltStrategy, which can be prevented by storing credential in another entity.

    private User(String name, Set<Role> roles, byte[] pw, SaltStrategy pwSaltStrategy) {
        this.name = name;
        this.roles = roles;
        this.pw = pw;
        this.pwSaltStrategy = pwSaltStrategy;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public Set<Role> getRoles() {
        return roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
    }

    public byte[] getPw() {
        return Arrays.copyOf(pw, pw.length);
    }

    public SaltStrategy getPwSaltStrategy() {
        return pwSaltStrategy;
    }

    public Builder update() {
        return new Builder().withName(name).withPw(pw).withPwSaltStrategy(pwSaltStrategy).withRoles(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static final class Builder {
        private String name;
        private Set<Role> roles = Collections.emptySet();
        private byte[] pw;
        private SaltStrategy pwSaltStrategy;

        private Builder() {}

        public Builder withName(String name) {
            if (ANONYMOUS_USER.getName().equals(name)) {
                throw new IllegalArgumentException();
            }
            this.name = name;
            return this;
        }

        public Builder withRoles(Set<Role> roles) {
            this.roles = new HashSet<>(roles);
            return this;
        }

        public Builder addRole(Role role) {
            if (this.roles == null || Collections.emptySet().equals(this.roles)) {
                this.roles = new HashSet<>(1); // assume in most cases user has only 1 role
            }
            this.roles.add(role);
            return this;
        }

        public Builder withPw(byte[] pw) {
            this.pw = pw;
            return this;
        }

        public Builder withPwSaltStrategy(SaltStrategy pwSaltStrategy) {
            this.pwSaltStrategy = pwSaltStrategy;
            return this;
        }

        public User build() {
            return new User(name, roles, pw, pwSaltStrategy);
        }
    }
}
