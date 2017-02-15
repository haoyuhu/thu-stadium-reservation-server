package com.huhaoyu.thu.config;

import com.huhaoyu.thu.filter.AuthorizationFilter;
import com.huhaoyu.thu.filter.BaseFilter;
import com.huhaoyu.thu.filter.ScheduledTaskVerificationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 下午11:12.
 */

@Configuration
public class FilterConfiguration {

    @Autowired
    @Bean
    FilterRegistrationBean authorizationFilterRegistration(AuthorizationFilter filter) {
        return createFilterRegistration(filter);
    }

    @Autowired
    @Bean
    FilterRegistrationBean taskVerificationRegistration(ScheduledTaskVerificationFilter filter) {
        return createFilterRegistration(filter);
    }

    private FilterRegistrationBean createFilterRegistration(BaseFilter filter) {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(filter.getFilterUrlPatterns());
        registrationBean.setName(filter.getFilterName());
        registrationBean.setOrder(filter.getFilterOrder());
        return registrationBean;
    }

}
