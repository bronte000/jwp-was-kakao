package webserver;

import webserver.Dataclass.Session;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static Session createSession() {
        Session session = new Session();
        SESSIONS.put(session.getId(), session);
        return session;
    }

    public static Session findSession(final String sessionId) {
        return SESSIONS.get(sessionId);
    }

    public void removeSession(final String sessionId) {
        SESSIONS.get(sessionId).invalidate();
        SESSIONS.remove(sessionId);
    }

    private SessionManager() {
    }
}
