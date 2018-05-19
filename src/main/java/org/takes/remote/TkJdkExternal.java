package org.takes.remote;

import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHeaders;
import org.takes.rq.RqPrint;
import org.takes.rq.RqRequestLine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TkJdkExternal implements Take {

    /**
     * HttpURLConnection decorator after its opening and before sending Request
     */
    private final JdkConnConfig config;

    /**
     * Ctor.
     * No HttpURLConnection configuration
     */
    public TkJdkExternal() {
        this(new JdkConnConfig() {
            @Override
            public HttpURLConnection config(HttpURLConnection conn) {
                return conn;
            }
        });
    }

    /**
     * Ctor.
     * @param readTimeout Read timeout millis
     * @param connectTimeout Connection timeout millis
     */
    public TkJdkExternal(final int readTimeout, final int connectTimeout) {
        this(new JdkConnConfig() {
            @Override
            public HttpURLConnection config(HttpURLConnection conn) {
                conn.setReadTimeout(readTimeout);
                conn.setConnectTimeout(connectTimeout);
                return conn;
            }
        });
    }

    /**
     * Ctor.
     * @param config HttpURLConnection decorator after its opening and before sending Request
     */
    public TkJdkExternal(final JdkConnConfig config) {
        this.config = config;
    }

    @Override
    public Response act(Request req) throws IOException {
        final RqRequestLine line = new RqRequestLine.Base(req);
        final RqHeaders headers = new RqHeaders.Base(req);
        final HttpURLConnection initial = HttpURLConnection.class.cast(new URL("http://" + headers.header("Host").iterator().next() + line.uri()).openConnection());
        final HttpURLConnection conn = this.config.config(initial);
        try {
            conn.setRequestMethod(line.method());

            for (String name : headers.names()) {
                for (String val : headers.header(name)) {
                    conn.addRequestProperty(name, val);
                }
            }

            if (Arrays.asList("POST", "PUT", "PATCH").contains(line.method())) {
                conn.setDoOutput(true);
                try (OutputStream out = conn.getOutputStream()) {
                    new RqPrint(req).printBody(out);
                }
            }

            final List<String> head = new ArrayList<>();
            head.add(conn.getHeaderField(0));
            int total = conn.getHeaderFields().size();
            for (int i = 1; i < total; i++) {
                head.add(conn.getHeaderFieldKey(i) + ": " + conn.getHeaderField(i));
            }

            return new Response() {
                @Override
                public Iterable<String> head() throws IOException {
                    return head;
                }

                @Override
                public InputStream body() throws IOException {
                    return conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream();
                }
            };
        } catch (IOException e){
            conn.disconnect();
            throw e;
        }
    }
}
