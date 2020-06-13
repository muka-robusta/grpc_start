package com.github.one2story.grpc_start.blog.server;

import com.github.one2story.grpc_start.calculator.server.CalculatorServer;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

public class BlogServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        Logger logger = Logger.getLogger(CalculatorServer.class.getName());

        Server server = ServerBuilder.forPort(50051)
                .addService(new BlogServiceImpl()) // handling all calls
                .build();

        server.start();
        logger.info("Server started");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            logger.info("Received shutdown request");
            server.shutdown();
            logger.info("Successfully stopped the server");
        }));

        server.awaitTermination();
    }
}
