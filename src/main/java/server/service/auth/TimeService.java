package server.service.auth;

import java.time.Instant;

interface TimeService {
    Instant now();
}
