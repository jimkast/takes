package org.takes.remote;

import org.takes.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

public final class RqEmpty implements Request {
    @Override
    public Iterable<String> head() throws IOException {
        return Collections.singletonList("GET / HTTP/1.1");
    }

    @Override
    public InputStream body() throws IOException {
        return new ByteArrayInputStream(new byte[0]);
    }
}
