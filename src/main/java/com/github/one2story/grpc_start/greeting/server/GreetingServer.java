package com.github.one2story.grpc_start.greeting.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class GreetingServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getLogger(GreetingServer.class.getName());
        logger.info("Hello gRPC");

        // plain text server
        /*
        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl()) // added class contains rpc method
                .build();
        server.start();

         */

        Server server = ServerBuilder.forPort(50051)
                .addService(new GreetServiceImpl())
                .useTransportSecurity(
                        new File("ssl/server.crt"),
                        new File("ssl/server.pem")
                )
                .build();


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Received shutdown request");
            server.shutdown();
            logger.info("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
