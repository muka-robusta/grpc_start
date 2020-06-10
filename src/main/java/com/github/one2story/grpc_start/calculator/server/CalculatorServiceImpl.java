package com.github.one2story.grpc_start.calculator.server;

import com.proto.calculator.*;
import io.grpc.stub.StreamObserver;

public class CalculatorServiceImpl extends CalculatorServiceGrpc.CalculatorServiceImplBase {
    @Override
    public void sum(SumRequest request, StreamObserver<SumResponse> responseObserver) {
        SumResponse sumResponse = SumResponse.newBuilder()
                .setSumResult(request.getFirstNumber() + request.getSecondNumber())
                .build();
        responseObserver.onNext(sumResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void primeNumberDecomposition(PrimeNumberDecompositionRequest request, StreamObserver<PrimeNumberDecompositionResponse> responseObserver) {
        Integer number = request.getNumber();
        Integer divisor = 2;

        while(number > 1) {
            if(number % divisor == 0){
                number = number / divisor;
                responseObserver.onNext(PrimeNumberDecompositionResponse.newBuilder()
                        .setPrimeFactor(divisor)
                        .build());
            } else {
                divisor++;
            }

        }

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<ComputeAverageRequest> computeAverage(StreamObserver<ComputeAverageResponse> responseObserver) {
        StreamObserver<ComputeAverageRequest> streamObserver = new StreamObserver<ComputeAverageRequest>() {

            int number = 0;
            int quantity = 0;

            @Override
            public void onNext(ComputeAverageRequest value) {
                // getting number from the stream
                number += value.getNumber();
                quantity++;
            }

            @Override
            public void onError(Throwable t) {
                // do smth on error
            }

            @Override
            public void onCompleted() {
                int average = number / quantity;
                responseObserver.onNext(ComputeAverageResponse.newBuilder()
                        .setAverageValue(average)
                        .build());
                responseObserver.onCompleted();
            }
        };
        return streamObserver;

    }
}
