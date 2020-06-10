package com.github.one2story.grpc_start.calculator.client;

import com.proto.calculator.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CalculatorClient {

    static Logger logger;

    public static void main(String[] args) {
        logger = Logger.getLogger(CalculatorClient.class.getName());
        new CalculatorClient().run();
    }

    private void run()
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("127.0.0.1", 50052)
                .usePlaintext()
                .build();
        logger.info("Channel created");
        // doSumCall(channel);
        // doNumberDecomposition(channel, new Integer(125));
        doClientStreamingCall(channel);
        channel.shutdown();
    }

    private Integer doSumCall(ManagedChannel channel)
    {
        Integer sumResult = 0;

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        logger.info("Connected to grpc server");

        SumRequest sumRequest = SumRequest.newBuilder()
                .setFirstNumber(10)
                .setSecondNumber(12)
                .build();

        SumResponse sumResponse = stub.sum(sumRequest);
        logger.info(sumRequest.getFirstNumber() + " + " + sumRequest.getSecondNumber() + " = " + sumResponse.getSumResult());
        sumResult = sumResponse.getSumResult();

        return sumResult;
    }

    private ArrayList<Integer> doNumberDecomposition(ManagedChannel channel, Integer number)
    {
        ArrayList<Integer> decomposedNumbers = new ArrayList<>();

        CalculatorServiceGrpc.CalculatorServiceBlockingStub stub = CalculatorServiceGrpc.newBlockingStub(channel);
        logger.info("Connected to GRPC");

        stub.primeNumberDecomposition(PrimeNumberDecompositionRequest.newBuilder()
                .setNumber(number)
                .build())
                .forEachRemaining(primeNumberDecompositionResponse -> {
                    decomposedNumbers.add(primeNumberDecompositionResponse.getPrimeFactor());
                    System.out.println(primeNumberDecompositionResponse.getPrimeFactor());
                });

        return decomposedNumbers;

    }

    private void doClientStreamingCall(ManagedChannel channel)
    {
        CalculatorServiceGrpc.CalculatorServiceStub asyncClient = CalculatorServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        StreamObserver<ComputeAverageRequest> requestObserver = asyncClient.computeAverage(new StreamObserver<ComputeAverageResponse>() {
            @Override
            public void onNext(ComputeAverageResponse value) {
                logger.info("Received a response from server");
                logger.info(String.valueOf(value.getAverageValue()));
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                logger.info("Server has completed sending us data");
                latch.countDown();
            }
        });

        int numbers[] = {1, 2, 2, 3};
        for(int number: numbers)
        {
            requestObserver.onNext(ComputeAverageRequest.newBuilder()
                    .setNumber(number)
                    .build());
        }

        // we expect the average
        requestObserver.onCompleted();
        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
