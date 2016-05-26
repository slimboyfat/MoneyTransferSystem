/*
 * Copyright 2016 slimboyfat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.slimboyfat.mts;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Standalone bootstrap.
 *
 * @author slimboyfat
 */
public class Bootstrap {

    /**
     * Starts the lightweight HTTP server serving the JAX-RS application.
     *
     * @return new instance of the lightweight HTTP server
     * @throws IOException in case of IO problem
     */
    static HttpServer startServer() throws IOException {
        final HttpServer server = HttpServer.create(new InetSocketAddress(getBaseURI().getPort()), 0);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                server.stop(0);
            }
        }));

        HttpHandler handler = RuntimeDelegate.getInstance().createEndpoint(new JaxRsApplication(), HttpHandler.class);
        server.createContext(getBaseURI().getPath(), handler);
        server.start();
        return server;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        startServer();

        Thread.currentThread().join();
    }

    /**
     * Gets base {@link URI}.
     *
     * @return base {@link URI}.
     */
    public static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(8080).build();
    }
}
