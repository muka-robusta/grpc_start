package com.github.one2story.grpc_start.greeting.server;

import com.proto.greet.*;
import io.grpc.stub.StreamObserver;

public class GreetServiceImpl extends GreetServiceGrpc.GreetServiceImplBase {
    @Override
    public void greet(GreetRequest request, StreamObserver<GreetResponse> responseObserver) {
        // extract the field we need
        Greeting greeting = request.getGreeting();
        String firstName = greeting.getFirstName();

        //create the response
        String firstNameRequestResult = "Hello, " + firstName;
        GreetResponse response = GreetResponse.newBuilder()
                .setResult(firstNameRequestResult)
                .build();

        // set the response
        responseObserver.onNext(response);

        // complete the RPC call
        responseObserver.onCompleted();
    }

    @Override
    public void greetManyTimes(GreetManyTimesRequest request, StreamObserver<GreetManyTimesResponse> responseObserver) {
        String firstName = request.getGreeting().getFirstName();

        for (int i = 0; i < 10; i++) {
            String result = "Hello, " + firstName + ", response number: " + i;
            GreetManyTimesResponse response = GreetManyTimesResponse.newBuilder()
                    .setResult(result)
                    .build();

            responseObserver.onNext(response);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }
}
