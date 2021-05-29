package server.domain;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Role {
    //private Long id; // for simplicity's sake
    private final String name;

    public Role(String name) {
        this.name = requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
