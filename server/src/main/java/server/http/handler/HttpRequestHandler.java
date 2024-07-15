package server.http.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.connection.ConnectionManager;
import server.connection.OneTimeConnectionManager;
import server.exception.BadGrammarException;
import server.http.model.HttpRequest;
import server.http.model.HttpResponse;
import server.http.model.body.Body;
import server.http.model.header.Headers;
import server.http.model.startline.StatusCode;
import server.http.model.startline.StatusLine;
import server.http.model.startline.Version;
import server.http.parser.HttpRequestParser;
import server.http.parser.HttpRequestParserImpl;
import server.processor.HttpRequestProcessor;
import server.processor.HttpRequestProcessors;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

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
                responseHttp(new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.BAD_REQUEST)));
            } catch (Exception exception) {
                logger.info("exception", exception);
                responseHttp(new HttpResponse(new StatusLine(Version.HTTP_1_1, StatusCode.INTERNAL_SERVER_ERROR)));
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
                outputStream.write(body.getMessage());
            }
            connectionManager.write(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest getHttpRequest() throws BadGrammarException {
        HttpRequest httpRequest = httpRequestParser.parseRequestLine(connectionManager.readLine());
        httpRequest = httpRequestParser.parseHeader(httpRequest, connectionManager.readUntilCrlf());
        if (httpRequest.hasBody()) {
            logger.debug("reading body {} bytes", httpRequest.getContentLength());
            byte[] read = connectionManager.readNBytes(httpRequest.getContentLength());
            httpRequest = httpRequestParser.parseBody(httpRequest, read);
        }
        return httpRequest;
    }
}
