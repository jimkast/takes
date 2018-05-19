package org.takes.remote;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;

import java.io.IOException;

public final class TkRsGreedy implements Take {
    private final Take tk;

    public TkRsGreedy(Take tk) {
        this.tk = tk;
    }

    @Override
    public Response act(Request req) throws IOException {
        return new RsGreedy(tk.act(req));
    }
}
