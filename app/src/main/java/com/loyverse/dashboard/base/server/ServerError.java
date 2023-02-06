package com.loyverse.dashboard.base.server;

import java.io.IOException;

public class ServerError extends IOException {

    private String result;

    public ServerError(String result) {
        this.result = result;
    }

    public int getErrorResource() {
        for (ServerResult message : ServerResult.values())
            if (result.equals(message.result))
                return message.resource;
        return ServerResult.UNKNOWN_ERROR.resource;
    }

    public String getResult() {
        return result;
    }
}
