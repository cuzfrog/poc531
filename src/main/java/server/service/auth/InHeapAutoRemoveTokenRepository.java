package server.service.auth;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class InHeapAutoRemoveTokenRepository implements TokenRepository {
    private final ConcurrentMap<String, Token> tokens = new ConcurrentHashMap<>(); // TODO, better impl

    @Override
    public void save(Token token) {
        tokens.put(token.getId(), token);
    }

    @Override
    public void remove(String id) {
        tokens.remove(id);
    }

    @Override
    public Token findById(String id) {
        return tokens.get(id);
    }
}
