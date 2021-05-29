package server.domain;

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
}
