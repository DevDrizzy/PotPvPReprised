package net.frozenorb.potpvp.util;

import java.io.Serializable;

public interface Callback<T> extends Serializable {

    /**
     * Called when the request is successfully completed
     *
     * @param data the data received from the call
     */
    void callback(T data);
}
