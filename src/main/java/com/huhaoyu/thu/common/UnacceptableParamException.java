package com.huhaoyu.thu.common;

import com.huhaoyu.thu.common.Constants.Response;

/**
 * Created by huhaoyu
 * Created On 2017/2/7 下午8:43.
 */
public class UnacceptableParamException extends IllegalArgumentException {

    private Response response;

    public UnacceptableParamException() {
        this.response = Response.IncorrectRequestParameterOrMethod;
    }

    public UnacceptableParamException(Response response) {
        this.response = response;
    }

    public UnacceptableParamException(String s) {
        super(s);
        this.response = Response.IncorrectRequestParameterOrMethod;
    }

    public UnacceptableParamException(Response response, String s) {
        super(s);
        this.response = response;
    }

    public Response createResponse() {
        return this.response;
    }
}
