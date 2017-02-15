package com.huhaoyu.thu.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huhaoyu.thu.service.AuthorizationService;
import lombok.Getter;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.huhaoyu.thu.common.Constants.*;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 下午11:03.
 */

@Component
public class ScheduledTaskVerificationFilter implements BaseFilter {

    @Getter
    public String filterName = ScheduledTaskVerificationFilter.class.getCanonicalName();
    @Getter
    public Integer filterOrder = 5;
    @Getter
    public String[] filterUrlPatterns = {"/task/*", "/stadium"};
    @Autowired
    private AuthorizationService authService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String secretId = request.getParameter(AuthorizationService.SECRET_ID);
        if (authService.isVerifiedTaskClient(secretId)) {
            chain.doFilter(request, response);
            return;
        }

        String entity = new ObjectMapper().writeValueAsString(Response.Unverified.createResponseMap());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        response.getWriter().write(entity);
    }

    @Override
    public void destroy() {
    }

}
