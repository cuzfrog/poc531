package server.service.auth;

interface TokenRepository {
    void save(Token token);
    void remove(String id);
    Token findById(String id);
}
