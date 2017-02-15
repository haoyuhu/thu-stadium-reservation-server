package com.huhaoyu.thu.service;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:50.
 */

public interface AuthorizationService {

    String SESSION_ID = "session_id";

    String OPEN_ID = "open_id";
    String SESSION_KEY = "session_key";

    String SECRET_ID = "secret_id";
    String SECRET_KEY = "secret_key";

    boolean isAuthenticatedUser(String sessionId);

    boolean isVerifiedTaskClient(String secretId);

    String createSessionId();

    void refreshAllCacheBySessionId(String sessionId);

    void deleteAllCacheBySessionId(String sessionId);

    boolean saveSessionKeyBySessionId(String sessionId, String sessionKey);

    boolean saveOpenIdBySessionId(String sessionId, String openId);

    String getSessionKeyBySessionId(String sessionId);

    String getOpenIdBySessionId(String sessionId);

}
