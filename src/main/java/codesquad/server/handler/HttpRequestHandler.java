package codesquad.server.handler;

import codesquad.exception.BadGrammarException;
import codesquad.http.model.HttpRequest;
import codesquad.http.model.HttpResponse;
import codesquad.http.model.body.Body;
import codesquad.http.model.header.Headers;
import codesquad.http.model.startline.StatusLine;
import codesquad.http.parser.HttpRequestParser;
import codesquad.http.parser.HttpRequestParserImpl;
import codesquad.server.connection.ConnectionManager;
import codesquad.server.connection.OneTimeConnectionManager;
import codesquad.server.processor.HttpRequestProcessor;
import codesquad.server.processor.HttpRequestProcessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpRequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final HttpRequestParser httpRequestParser;
    private final HttpRequestProcessor processor;
    private final ConnectionManager connectionManager;

    public HttpRequestHandler(Socket connectedSocket) {
        this.processor = new HttpRequestProcessors();
        this.httpRequestParser = new HttpRequestParserImpl();
        this.connectionManager = new OneTimeConnectionManager(connectedSocket);
    }

    @Override
    public void run() {
        logger.debug("Client connected");

        while (connectionManager.isAlive()) {
            try {
                HttpRequest httpRequest = getHttpRequest();
                logger.debug("message received = {}", httpRequest);
                HttpResponse response = processor.process(httpRequest);
                logger.debug("processed = {}", response);
                responseHttp(response);
            } catch (BadGrammarException e) {
                logger.info("exception", e);
                responseHttp(HttpResponse.BAD_REQUEST);
            } catch (Exception exception) {
                logger.info("exception", exception);
            } finally {
                connectionManager.incrementRequestCounter();
            }
        }
        logger.debug("Client disconnected");
        close();
    }

    private void close() {
        connectionManager.close();
    }

    private void responseHttp(HttpResponse response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            StatusLine statusLine = response.getStatusLine();
            outputStream.write(statusLine.toString().getBytes("US-ASCII"));
            Headers headers = response.getHeader();
            outputStream.write(headers.toString().getBytes("US-ASCII"));
            outputStream.write('\r');
            outputStream.write('\n');
            if (response.hasBody()) {
                Body body = response.getBody();
                outputStream.write(body.getBody());
            }
            connectionManager.write(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest getHttpRequest() throws BadGrammarException {
        HttpRequest httpRequest = httpRequestParser.parseRequestLine(connectionManager.readLine());
        httpRequest = httpRequestParser.parseHeader(httpRequest, connectionManager.readLines());
        if (httpRequest.hasBody()) {
            byte[] read = connectionManager.readNBytes(httpRequest.getContentLength() * 2);
            httpRequest = httpRequestParser.parseBody(httpRequest, new String(read, StandardCharsets.UTF_8));
        }
        return httpRequest;
    }
}
