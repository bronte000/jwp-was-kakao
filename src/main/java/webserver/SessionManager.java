package webserver;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {

    private static final Map<String, Session> SESSIONS = new HashMap<>();

    public static void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    public static Session findSession(final String sessionId) {
        if (!SESSIONS.containsKey(sessionId)) {
            add(new Session(sessionId));
        }
        return SESSIONS.get(sessionId);
    }

    public void remove(final String sessionId) {
        SESSIONS.get(sessionId).invalidate();
        SESSIONS.remove(sessionId);
    }

    private SessionManager() {}
}
