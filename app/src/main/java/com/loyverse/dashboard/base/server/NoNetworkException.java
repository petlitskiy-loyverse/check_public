package com.loyverse.dashboard.base.server;

import java.io.IOException;

public class NoNetworkException extends IOException {
    public NoNetworkException(String message) {
        super(message);
    }

}
