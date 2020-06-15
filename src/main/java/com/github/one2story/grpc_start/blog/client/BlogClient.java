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


//        String blogId = "5ee4eb2c90b14f54452c6e5e";
        System.out.println(blogId);


        ReadBlogResponse readBlogResponse = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setId(blogId)
                .build());

        System.out.println(readBlogResponse.toString());

        /*ReadBlogResponse readBlogResponseNotFound = blogClient.readBlog(ReadBlogRequest.newBuilder()
                .setId("fake-id")
                .build());*/

        // updateBlog(blogClient);
        // deleteBlog(blogClient);
        listBlogs(blogClient);


    }

    private void updateBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient)
    {
        String replacementId = "5ee4d6c490b14f406075ba56";

        Blog blogToUpdate = Blog.newBuilder()
                .setAuthor("Ilya")
                .setContent("This content is replaced")
                .setTitle("This title is replaced")
                .setId(replacementId)
                .build();

        UpdateBlogResponse updateResponse = blogClient.updateBlog(UpdateBlogRequest.newBuilder()
                .setBlog(blogToUpdate)
                .build());

        System.out.println("Updated blog");
        System.out.println(updateResponse.toString());
    }

    private void deleteBlog(BlogServiceGrpc.BlogServiceBlockingStub blogClient)
    {
        String blogId = "5ee77a757525445a5decb4de";

        System.out.println("Deleting blog by ID");
        DeleteBlogResponse deleteBlogResponse = blogClient.deleteBlog(DeleteBlogRequest.newBuilder()
                .setBlogId(blogId)
                .build());
    }

    private void listBlogs(BlogServiceGrpc.BlogServiceBlockingStub blogClient)
    {
        // here we list blogs in our database
        blogClient.listBlog(ListBlogRequest.newBuilder()
                .build()).forEachRemaining(
                listBlogResponse -> {
                    System.out.println(listBlogResponse.getBlog().toString());
                }
        );
    }
}
