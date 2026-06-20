package ru.cinemaabyss.proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Enumeration;
import java.util.Set;

@Service
public class ProxyService {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailer",
            "transfer-encoding",
            "upgrade",
            "host",
            "content-length"
    );

    private final RestClient restClient;
    private final TargetSelector targetSelector;

    public ProxyService(RestClient.Builder restClientBuilder, TargetSelector targetSelector) {
        this.restClient = restClientBuilder.build();
        this.targetSelector = targetSelector;
    }

    public ResponseEntity<byte[]> forward(HttpServletRequest request, byte[] body) {
        TargetSelector.Target target = targetSelector.select(request.getRequestURI());
        URI targetUri = buildTargetUri(target.baseUrl(), request);
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        RestClient.RequestBodySpec requestSpec = restClient
                .method(method)
                .uri(targetUri)
                .headers(headers -> headers.addAll(copyRequestHeaders(request)));

        ResponseEntity<byte[]> response = hasBody(method, body)
                ? execute(requestSpec.body(body))
                : execute(requestSpec);

        HttpHeaders responseHeaders = copyResponseHeaders(response.getHeaders());
        responseHeaders.set("X-Proxy-Target", target.name());

        return new ResponseEntity<>(
                response.getBody(),
                responseHeaders,
                response.getStatusCode()
        );
    }

    private ResponseEntity<byte[]> execute(RestClient.RequestHeadersSpec<?> requestSpec) {
        return requestSpec.exchange((backendRequest, backendResponse) -> ResponseEntity
                .status(backendResponse.getStatusCode())
                .headers(copyResponseHeaders(backendResponse.getHeaders()))
                .body(backendResponse.getBody().readAllBytes()));
    }

    private boolean hasBody(HttpMethod method, byte[] body) {
        return body != null
               && body.length > 0
               && method != HttpMethod.GET
               && method != HttpMethod.DELETE;
    }

    private URI buildTargetUri(String baseUrl, HttpServletRequest request) {
        String normalizedBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        String queryString = request.getQueryString();
        String targetUrl = normalizedBaseUrl + request.getRequestURI()
                           + (queryString == null ? "" : "?" + queryString);

        return URI.create(targetUrl);
    }

    private HttpHeaders copyRequestHeaders(HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();

            if (isAllowedHeader(name)) {
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    headers.add(name, values.nextElement());
                }
            }
        }

        return headers;
    }

    private HttpHeaders copyResponseHeaders(HttpHeaders sourceHeaders) {
        HttpHeaders headers = new HttpHeaders();

        sourceHeaders.forEach((name, values) -> {
            if (isAllowedHeader(name)) {
                headers.addAll(name, values);
            }
        });

        return headers;
    }

    private boolean isAllowedHeader(String name) {
        return name != null && !HOP_BY_HOP_HEADERS.contains(name.toLowerCase());
    }
}