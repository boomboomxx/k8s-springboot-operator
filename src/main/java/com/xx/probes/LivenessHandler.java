package com.xx.probes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import io.javaoperatorsdk.operator.Operator;

import java.io.IOException;

import static com.xx.probes.StartupHandler.sendMessage;

/**
 * @author xx
 * @date 2024-11-11
 */
public class LivenessHandler implements HttpHandler {

    private final Operator operator;

    public LivenessHandler(Operator operator) {
        this.operator = operator;
    }

    // custom logic can be added here based on the health of event sources
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        if (operator.getRuntimeInfo().allEventSourcesAreHealthy()) {
            sendMessage(httpExchange, 200, "healthy");
        } else {
            sendMessage(httpExchange, 400, "an event source is not healthy");
        }
    }
}