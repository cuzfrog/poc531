package server.service.auth;

import server.domain.Role;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

final class Token {
    private final String id;
    private final Instant expirationTime;
    private final Set<Role> roles;

    Token(String id, Instant expirationTime, Set<Role> roles) {
        this.id = requireNonNull(id);
        this.expirationTime = requireNonNull(expirationTime);
        this.roles = new HashSet<>(roles);
    }

    String getId() {
        return id;
    }

    Instant getExpirationTime() {
        return expirationTime;
    }

    Set<Role> getRoles() {
        return roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Token token = (Token) o;
        return Objects.equals(id, token.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
