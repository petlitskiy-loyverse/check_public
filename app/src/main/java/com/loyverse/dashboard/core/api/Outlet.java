package com.loyverse.dashboard.core.api;

import com.google.gson.annotations.SerializedName;

public class Outlet {
    @SerializedName(value = "id", alternate = {"outletId"})
    public int id;
    @SerializedName(value = "name", alternate = {"outletName"})
    public String name;

    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Outlet)) {
            return false;
        }
        Outlet outlet = (Outlet) o;
        return this.id == outlet.id;
    }

    @Override
    public int hashCode() {
        int result = 17;
//            result = 31 * result + name.hashCode();
        result = 31 * result + id;
        return result;
    }
}
