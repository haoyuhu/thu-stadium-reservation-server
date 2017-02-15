package com.huhaoyu.thu.common;

import com.huhaoyu.thu.common.Constants.Response;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 下午12:31.
 */

public class UnauthorizedException extends IllegalArgumentException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public Response createResponse() {
        return Response.Unauthorized;
    }

}
