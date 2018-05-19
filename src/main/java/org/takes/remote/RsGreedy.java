package org.takes.remote;

import org.cactoos.Input;
import org.cactoos.io.StickyInput;
import org.takes.Response;

import java.io.IOException;
import java.io.InputStream;

public final class RsGreedy implements Response {
    private final Response rs;
    private final Input input;

    public RsGreedy(final Response rs) {
        this.rs = rs;
        this.input = new StickyInput(new Input() {
            @Override
            public InputStream stream() throws IOException {
                return rs.body();
            }
        });
    }

    @Override
    public Iterable<String> head() throws IOException {
        return rs.head();
    }

    @Override
    public InputStream body() throws IOException {
        return input.stream();
    }
}
