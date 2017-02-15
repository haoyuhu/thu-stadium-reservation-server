package com.huhaoyu.thu.widget;

import java.lang.annotation.*;

/**
 * Created by huhaoyu
 * Created On 2017/2/6 上午7:15.
 */

@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VisibleEntity {

    boolean visible() default true;

}
