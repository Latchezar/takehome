package com.example.takehome.keycloak;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class OkHttpClientEngine implements ClientHttpEngine {

    private final OkHttpClient client;

    private SSLContext sslContext;

    public OkHttpClientEngine(OkHttpClient client) {
        this.client = client;
    }

    @Override
    public SSLContext getSslContext() {
        return sslContext;
    }

    public void setSslContext(SSLContext sslContext) {
        this.sslContext = sslContext;
    }

    @Override
    public HostnameVerifier getHostnameVerifier() {
        return client.hostnameVerifier();
    }

    @Override
    @SneakyThrows
    public javax.ws.rs.core.Response invoke(Invocation request) {
        Request req = createRequest((ClientInvocation) request);
        Response response;
        try {
            response = client.newCall(req).execute();
        } catch (IOException e) {
            throw new ProcessingException("Unable to invoke request", e);
        }
        return createResponse((ClientInvocation) request, response);
    }

    private Request createRequest(ClientInvocation request) throws IOException {
        Request.Builder builder =
                new Request.Builder()
                        .method(request.getMethod(), createRequestBody(request))
                        .url(request.getUri().toString());
        for (Map.Entry<String, List<String>> header : request.getHeaders().asMap().entrySet()) {
            String headerName = header.getKey();
            for (String headerValue : header.getValue()) {
                builder.addHeader(headerName, headerValue);
            }
        }
        return builder.build();
    }

    private RequestBody createRequestBody(final ClientInvocation request) throws IOException {
        if (request.getEntity() == null) {
            return null;
        }

        // NOTE: this will invoke WriterInterceptors which can possibly change the request,
        // so it must be done first, before reading any header.
        try (final Buffer buffer = new Buffer()){
            request.writeRequestBody(buffer.outputStream());

            javax.ws.rs.core.MediaType mediaType = request.getHeaders().getMediaType();
            final MediaType contentType =
                    (mediaType == null) ? null : MediaType.parse(mediaType.toString());

            return new RequestBody() {
                @Override
                public long contentLength() {
                    return buffer.size();
                }

                @Override
                public MediaType contentType() {
                    return contentType;
                }

                @Override
                public void writeTo(@NotNull BufferedSink sink) {
                    buffer.copyTo(sink.getBuffer(), 0, buffer.size());
                }
            };
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    private ClientResponse createResponse(ClientInvocation request,
                                          final Response response) {
        ClientResponse clientResponse =
                new ClientResponse(request.getClientConfiguration()) {
                    private InputStream stream;

                    @Override
                    protected InputStream getInputStream() {
                        if (stream == null && response.body() != null) {
                            stream = response.body().byteStream();
                        }
                        return stream;
                    }

                    @Override
                    protected void setInputStream(InputStream is) {
                        stream = is;
                    }

                    @Override
                    public void releaseConnection() throws IOException {
                        // Stream might have been entirely replaced, so we need to close it independently from response.body()
                        Throwable primaryExc = null;
                        try {
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Exception t) {
                            primaryExc = t;
                            throw t;
                        } finally {
                            if (primaryExc != null) {
                                try {
                                    response.body().close();
                                } catch (Exception suppressedExc) {
                                    primaryExc.addSuppressed(suppressedExc);
                                }
                            } else {
                                response.body().close();
                            }
                        }
                    }
                };

        clientResponse.setStatus(response.code());
        clientResponse.setHeaders(transformHeaders(response.headers()));

        return clientResponse;
    }

    private MultivaluedMap<String, String> transformHeaders(Headers headers) {
        MultivaluedMap<String, String> ret = new CaseInsensitiveMap<>();
        for (int i = 0, l = headers.size(); i < l; i++) {
            ret.add(headers.name(i), headers.value(i));
        }
        return ret;
    }

    @Override
    public void close() {
        // no-op
    }
}
