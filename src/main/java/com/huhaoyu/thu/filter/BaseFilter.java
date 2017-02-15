package com.huhaoyu.thu.filter;

import javax.servlet.Filter;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 下午11:46.
 */

public interface BaseFilter extends Filter {

    String getFilterName();

    Integer getFilterOrder();

    String[] getFilterUrlPatterns();

}
