package com.huhaoyu.thu.common;

import com.huhaoyu.thu.common.Constants.Response;

/**
 * Created by huhaoyu
 * Created On 2017/2/12 上午10:56.
 */
public class ServerErrorException extends RuntimeException {

    private Response response;

    public ServerErrorException() {
        this.response = Response.ServerError;
    }

    public ServerErrorException(Response response) {
        this.response = response;
    }

    public ServerErrorException(String s) {
        super(s);
        this.response = Response.ServerError;
    }

    public ServerErrorException(Response response, String s) {
        super(s);
        this.response = response;
    }

    public Response createResponse() {
        return this.response;
    }

}
