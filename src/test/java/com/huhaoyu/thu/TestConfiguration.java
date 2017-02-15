package com.huhaoyu.thu;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by huhaoyu
 * Created On 2017/2/9 下午12:15.
 */

@Getter
@Configuration
@PropertySource(value = "classpath:test.properties")
public class TestConfiguration {

    @Value(value = "${tsinghua.username}")
    private String username;
    @Value(value = "${tsinghua.studentId}")
    private String studentId;
    @Value(value = "${tsinghua.password}")
    private String password;

}
