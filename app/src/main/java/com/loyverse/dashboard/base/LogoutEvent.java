package com.loyverse.dashboard.base;

public class LogoutEvent {
    boolean force = false;

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }
}
