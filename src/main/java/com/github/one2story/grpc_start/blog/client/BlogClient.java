package com.github.one2story.grpc_start.blog.client;

import com.github.one2story.grpc_start.calculator.client.CalculatorClient;
import com.proto.blog.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.logging.Logger;

public class BlogClient {

    public static void main(String[] args) {
        System.out.println("gRPC client start");

        BlogClient blogClient = new BlogClient();
        blogClient.run();
    }

    private void run()
    {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();

        BlogServiceGrpc.BlogServiceBlockingStub blogClient = BlogServiceGrpc.newBlockingStub(channel);

        /*
        Blog blog = Blog.newBuilder()
                .setTitle("Elixir - is my love")
                .setContent("I love you Elixir")
                .setAuthor("Ilya Tsuprun")
                .build();


        CreateBlogResponse createblogResponse = blogClient.createBlog(CreateBlogRequest.newBuilder()
                .setBlog(blog)
                .build());

        System.out.println("Received create blog response");
        System.out.println(createblogResponse.toString());

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        String blogId = createblogResponse.getBlog().getId();
        */
        String blogId = "5ee4eb2c90b14f54452c6e5e";
        System.out.println(blogId);


        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setId(blogId)
                .build());

        System.out.println(readBlogResponse.toString());

        /*ReadBlogResponse readBlogResponseNotFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setId("fake-id")
                .build());*/


    }
}
