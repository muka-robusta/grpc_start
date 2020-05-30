package com.github.one2story.grpc_start.calculator.client;

import com.proto.calculator.CalculatorServiceGrpc;
import com.proto.calculator.PrimeNumberDecompositionRequest;
import com.proto.calculator.SumRequest;
import com.proto.calculator.SumResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.logging.Logger;

public class CalculatorClient {
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(CalculatorClient.class.getName());

        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50052)
                .usePlaintext()
                .build();
        logger.info("Channel created");

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        logger.info("Connected to grpc server");
//
//        SumRequest request = SumRequest.newBuilder()
//                .setFirstNumber(10)
//                .setSecondNumber(20)
//                .build();
//
//        SumResponse response = stub.sum(request);
//        logger.info(request.getFirstNumber() + " + " + request.getSecondNumber() + " = " + response.getSumResult());

        // Streaming server
        Integer number = 1235;
        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

        channel.shutdown();
    }
}
