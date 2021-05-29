package server.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public final class User {
    // private Long id; // for simplicity's sake
    private String name;
    private Set<Role> roles;
    private byte[] pw;
    private SaltStrategy pwSaltStrategy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles == null ? Collections.emptySet() : roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void addRole(Role role) {
        if (this.roles == null) {
            this.roles = new HashSet<>(1); // assume in most cases user has only 1 role
        }
        this.roles.add(role);
    }

    public byte[] getPw() {
        return pw;
    }

    public void setPw(byte[] pw) {
        this.pw = pw;
    }

    public SaltStrategy getPwSaltStrategy() {
        return pwSaltStrategy;
    }

    public void setPwSaltStrategy(SaltStrategy pwSaltStrategy) {
        this.pwSaltStrategy = pwSaltStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
