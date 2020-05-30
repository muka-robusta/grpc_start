package com.github.one2story.grpc_start.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.logging.Logger;

public class GreetingClient {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(GreetingClient.class.getName());
        logger.info("gRPC client");

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        logger.info("Creating stub");
        // DummyServiceGrpc.DummyServiceBlockingStub syncClient = DummyServiceGrpc.newBlockingStub(channel);
        // DummyServiceGrpc.DummyServiceFutureStub asyncClient = DummyServiceGrpc.newFutureStub(channel);

        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        //------------
        // create a protobuf greeting message
//        Greeting greeting = Greeting.newBuilder()
//                .setFirstName("Ilya")
//                .setLastName("Sav")
//                .build();
//
//        // creating greet request
//        GreetRequest greetRequest = GreetRequest.newBuilder()
//                .setGreeting(greeting)
//                .build();
//
//        // call the RPC and get back greet response (protobuf)
//        GreetResponse greetResponce = greetClient.greet(greetRequest);
//
//        logger.info(greetResponce.getResult());

        // Server streaming
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ilya"))
                .build();

        // streaming the responses in a blocking manner
        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });

        //do smth
        logger.info("Shutting down channel");
        channel.shutdown();

    }
}
