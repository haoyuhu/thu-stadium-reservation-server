package com.huhaoyu.thu.common;

import okhttp3.*;
import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/2/8 下午9:30.
 */
public class HttpUtil {

    private static final Logger logger = LoggerFactory.getLogger(HttpUrl.class);

    public interface Scheme {
        String HTTP = "http";
        String HTTPS = "https";
    }

    private static OkHttpClient client = new OkHttpClient();

    public static OkHttpClient getClient() {
        return client;
    }

    public static String get(String scheme, String host, Integer port, String[] segments, Map<String, Object> query) {
        HttpUrl url = createHttpUrl(scheme, host, port, segments, query);
        Request request = new Request.Builder().url(url).get().build();
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("HttpUtil make GET request error: " + url.toString(), e);
            return null;
        }
    }

    public static String post(String scheme, String host, Integer port, String[] segments, Map<String, Object> query, Map<String, Object> data) {
        HttpUrl url = createHttpUrl(scheme, host, port, segments, query);
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : data.keySet()) {
            builder.add(key, String.valueOf(data.get(key)));
        }
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        try {
            Response response = client.newCall(request).execute();
            return response.isSuccessful() ? response.body().string() : null;
        } catch (IOException e) {
            logger.error("Http make POST request error: " + url.toString(), e);
            return null;
        }
    }

    private static HttpUrl createHttpUrl(String scheme, String host, Integer port, String[] segments, Map<String, Object> query) {
        HttpUrl.Builder builder = new HttpUrl.Builder();
        builder.scheme(scheme).host(host);
        if (port != null) {
            builder.port(port);
        }
        if (segments != null) {
            for (String segment : segments) {
                builder.addPathSegment(segment);
            }
        }
        if (query != null) {
            for (String key : query.keySet()) {
                builder.addQueryParameter(key, String.valueOf(query.get(key)));
            }
        }
        return builder.build();
    }

    public static String convertMapToQueryStrings(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s", key, map.get(key).toString()));
        }
        String raw = sb.toString();
        try {
            return UriUtils.encodeQuery(raw, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            logger.error("cannot encode query strings");
        }
        return null;
    }

}
