package org.takes.remote;

import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Skipped;
import org.takes.Request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public final class RqBase implements Request {
    private final CharSequence method;
    private final CharSequence host;
    private final CharSequence href;
    private final Request req;

    public RqBase(CharSequence host, CharSequence href) {
        this("GET", host, href);
    }

    public RqBase(CharSequence method, CharSequence host, CharSequence href) {
        this(method, host, href, new RqEmpty());
    }

    public RqBase(CharSequence method, CharSequence host, CharSequence href, Request req) {
        this.method = method;
        this.host = host;
        this.href = href;
        this.req = req;
    }

    @Override
    public Iterable<String> head() throws IOException {
        return new Joined<>(
            Arrays.asList(
                this.method + " " + this.href + " HTTP/1.1",
                "Host: " + this.host
            ),
            new Skipped<>(1, this.req.head())
        );
    }

    @Override
    public InputStream body() throws IOException {
        return req.body();
    }
}
