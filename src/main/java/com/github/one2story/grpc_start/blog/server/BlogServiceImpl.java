package com.github.one2story.grpc_start.blog.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.proto.blog.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

/*

    There was an error
    In the MongoDB author field is called "author_id"
    but on the grpc pipe it is called "author"

 */

import static com.mongodb.client.model.Filters.eq;

public class BlogServiceImpl extends BlogServiceGrpc.BlogServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("mydb");
    private MongoCollection<Document> collection = database.getCollection("blog");

    @Override
    public void createBlog(CreateBlogRequest request, StreamObserver<CreateBlogResponse> responseObserver) {

        System.out.println("Received Create Blog request");

        Blog blog = request.getBlog();

        Document doc = new Document("author_id", blog.getAuthor())
                .append("title", blog.getTitle())
                .append("content", blog.getContent());

        // we insert(create) the document into mongoDB
        collection.insertOne(doc);

        // we retrieve the MongoDB generated ID
        String id = doc.getObjectId("_id").toString();

        CreateBlogResponse response = CreateBlogResponse.newBuilder()
                .setBlog(blog.toBuilder().setId(id).build())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void readBlog(ReadBlogRequest request, StreamObserver<ReadBlogResponse> responseObserver) {

        System.out.println("Receiving Read blog request");
        String blogId = request.getId();

        // searching for a blog
        System.out.println("Searching for a blog");
        Document result = null;
        try {
            result = collection.find(eq("_id", new ObjectId(blogId)))
                    .first();
        }catch(Exception ex)
        {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("The blog with corresponding id is not found")
                            .augmentDescription(ex.getLocalizedMessage())
                            .asRuntimeException()
            );
        }

        if(result == null)
        {
            // we dont have match
            System.out.println("Blog not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("The blog with corresponding id is not found")
                        .asRuntimeException()
            );

        } else
        {
            System.out.println("blog found - sending response");

            Blog blog = Blog.newBuilder()
                    .setTitle(result.getString("title"))
                    .setContent(result.getString("content"))
                    .setAuthor(result.getString("author_id"))
                    .setId(blogId)
                    .build();

            System.out.println("Sending response");
            responseObserver.onNext(ReadBlogResponse.newBuilder().setBlog(blog).build());
            responseObserver.onCompleted();

        }

    }
}
