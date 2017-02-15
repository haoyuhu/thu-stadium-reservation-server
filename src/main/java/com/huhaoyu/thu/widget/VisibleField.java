package com.huhaoyu.thu.widget;

import java.lang.annotation.*;

/**
 * Created by huhaoyu
 * Created On 2017/2/6 上午12:28.
 */

@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface VisibleField {

    boolean visible() default true;

    String name() default "";

}
