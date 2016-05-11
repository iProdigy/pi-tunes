package org.micds;

import java.util.ArrayDeque;

public class RequestQueue extends ArrayDeque<SongRequest> {
    private static final RequestQueue INSTANCE = new RequestQueue();

    public static RequestQueue getQueue() {
        return INSTANCE;
    }
}
