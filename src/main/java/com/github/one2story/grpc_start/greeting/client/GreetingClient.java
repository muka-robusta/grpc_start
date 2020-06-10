package com.github.one2story.grpc_start.greeting.client;

import com.proto.dummy.DummyServiceGrpc;
import com.proto.greet.*;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GreetingClient {

    ManagedChannel channel;
    static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger(GreetingClient.class.getName());
        logger.info("gRPC client");

        GreetingClient main = new GreetingClient();
        main.run();

    }

    public void run()
    {
        channel = ManagedChannelBuilder
                .forAddress("localhost", 50051)
                .usePlaintext()
                .build();
//        doUnaryCall(channel);
//        doServerStreamingCall(channel);
//        doClientStreamingCall(channel);

        doBiDirectionalCall(channel);

        logger.info("Shutting down channel");
        channel.shutdown();
    }

    private void doBiDirectionalCall(ManagedChannel channel)
    {

        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);
        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<GreetEveryoneRequest> requestObserver = asyncClient.greetEveryone(new StreamObserver<GreetEveryoneResponse>() {
            @Override
            public void onNext(GreetEveryoneResponse value) {
                System.out.println("Response from server: " + value.getResponse());
            }

            @Override
            public void onError(Throwable t) {
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server is done sending data");
                latch.countDown();
            }
        });

        Arrays.asList("Ilya", "Max", "Ivan", "Vlad").forEach(
                name -> {
                    System.out.println("Sending name: " + name);
                    requestObserver.onNext(GreetEveryoneRequest.newBuilder()
                            .setGreeting(Greeting.newBuilder()
                                    .setFirstName(name))
                            .build());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );

        requestObserver.onCompleted();

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void doClientStreamingCall(ManagedChannel channel)
    {
        // create a client
//        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // create an async client
        GreetServiceGrpc.GreetServiceStub asyncClient = GreetServiceGrpc.newStub(channel);

        /*
            To deal with async programming, we need to implement a latch
         */

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<LongGreetRequest> requestObserver = asyncClient.longGreet(new StreamObserver<LongGreetResponse>() {
            @Override
            public void onNext(LongGreetResponse value) {
                // we get a response from a server
                logger.info("Received a response from a server");
                logger.info(value.getResult());
                // this will be called only once
            }

            @Override
            public void onError(Throwable t) {
                // we get an error from the server
            }

            @Override
            public void onCompleted() {
                // the server is done sending us data
                logger.info("Received a response from a server");
                latch.countDown();
                // onCompleted will be called after onNext()
            }
        });

        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Ilya")
                        .build())
                .build());

        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Julia")
                        .build())
                .build());

        // streaming msg
        requestObserver.onNext(LongGreetRequest.newBuilder()
                .setGreeting(Greeting.newBuilder()
                        .setFirstName("Ivan")
                        .build())
                .build());

        // we tell the server that the client is done
        requestObserver.onCompleted();
        /*
            without a latch, we wont have enough time
            to get messages back and we wont see becoming msgs
         */
        try {
            latch.await(3L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doUnaryCall(ManagedChannel channel)
    {
        // create a greet-service client
        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Unary
        // create a protobuf greeting message
        Greeting greeting = Greeting.newBuilder()
                .setFirstName("Ilya")
                .setLastName("Sav")
                .build();

        // creating greet request
        GreetRequest greetRequest = GreetRequest.newBuilder()
                .setGreeting(greeting)
                .build();

        // call the RPC and get back greet response (protobuf)
        GreetResponse greetResponce = greetClient.greet(greetRequest);

        logger.info(greetResponce.getResult());
    }

    private void doServerStreamingCall(ManagedChannel channel)
    {
        logger.info("Creating stub");

        GreetServiceGrpc.GreetServiceBlockingStub greetClient = GreetServiceGrpc.newBlockingStub(channel);

        // Server streaming
        GreetManyTimesRequest greetManyTimesRequest = GreetManyTimesRequest.newBuilder()
                .setGreeting(Greeting.newBuilder().setFirstName("Ilya"))
                .build();

        // streaming the responses in a blocking manner
        greetClient.greetManyTimes(greetManyTimesRequest)
                .forEachRemaining(greetManyTimesResponse -> {
                    System.out.println(greetManyTimesResponse.getResult());
                });


    }
}
