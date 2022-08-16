package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import models.Dashboard;
import models.User;
import mongo.IMongoDB;
import mongo.InMemoryMongoDB;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

import javax.inject.Inject;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.mvc.Results.ok;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

//import static play.test.Helpers.route;

public class DashboardTest extends WithApplication {
    @Inject
    IMongoDB mongoDB;

    String token;
    Dashboard dash1;
    String user;

    @Before
    public void setup() {
//        super.startPlay();
//
//        mongoDB = app.injector().instanceOf(InMemoryMongoDB.class);

        //MongoCollection userMongo = mongoDB.getMongoDatabase()
        //        .getCollection("users", User.class);


        User user = new User("ermira", "password", singletonList("62e90a69e08d17cd567438dd"));
        user.setId(new ObjectId("62e257f6dbff353f56ef3025"));
//        userMongo.insertOne(new User("ermira", "password", Collections.singletonList("Admin")));
//        user.setId(new ObjectId());
        //userMongo.insertOne(user);
//        System.out.println("User is: " + user);

        final Http.RequestBuilder userRequest = new Http.RequestBuilder()
                .method("POST")
                .bodyJson(Json.toJson(user))
                .uri("/api/users");
        final Result userRes = route(app, userRequest);
        ObjectNode node = Json.newObject();
        node.put("username", user.getUsername());
        node.put("password", user.getPassword());

        final Http.RequestBuilder homeRequest = new Http.RequestBuilder()
                .method("POST")
                .bodyJson(Json.toJson(user))
                .uri("/api/authenticate");

        System.out.println("HR" + homeRequest);

        final Result home = route(app, homeRequest);

        //token from the user
        token = contentAsString(home).replace("\"", "");

        dash1 = new Dashboard("Kids", "utilize cross-platform action-items",
                new ObjectId());
        System.out.println("Dashboard test " + dash1);
//                singletonList("62e257f6dbff353f56ef3024"), singletonList("62ea2bb98f2b3cbbd9d853de"), emptyList());
//                emptyList(),emptyList(),emptyList());
//        dash1.setId(new ObjectId());
//        dashboards.insertOne(dash1);

//        Result res = getResult(POST, dashboard, "/api/dashboard/", "Bearer ", +token);
        final Http.RequestBuilder builder = new Http.RequestBuilder();
        builder.method("POST")
                .uri("/api/dashboard")
                .bodyJson(Json.toJson(dash1))
                .header("Authorization", "Bearer " + token);

        final Result dashRes = route(app, builder);
        JsonNode dashNode = Json.parse(contentAsString(dashRes));
        System.out.println("Contentas string " + contentAsString(dashRes));


//        token = getToken(new User("ermira"))
    }


    @Test
    public void save() {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Test");
        dashboard.setDescription("Test dashboard");
        dashboard.setId(new ObjectId());

        Http.RequestBuilder homeRequest = new Http.RequestBuilder();
        homeRequest.method("POST")
                .uri("/api/dashboard")
                .bodyJson(Json.toJson(dashboard))
                .header("Authorization", "Bearer " + token);
//                .build();
        Result dashRes = route(app, homeRequest);
//        JsonNode dashNode = Json.parse(contentAsString(dashRes));
        assertEquals(OK, dashRes.status());
    }

    @Test
    public void all() {
        final Http.RequestBuilder homeRequest = new Http.RequestBuilder();
        homeRequest.method("GET")
                .uri("/api/dashboard")
                .bodyJson(Json.toJson(""))
                .header("Authorization", "Bearer " + token);
//                .build();
        Result dashRes = route(app, homeRequest);
        JsonNode dashNode = Json.parse(contentAsString(dashRes));
        assertEquals(OK, dashRes.status());
    }


    @Test
    public void updateTest() {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Test");
        dashboard.setDescription("Test dashboard");
        dashboard.setId(new ObjectId());

        final Http.RequestBuilder homeRequest = new Http.RequestBuilder();
        homeRequest.method("PUT")
                .uri("/api/dashboard/62e257f6dbff353f56ef3025")
                .bodyJson(Json.toJson(dashboard))
                .header("Authorization", "Bearer " + token);
//                .build();
        final Result dashRes = route(app, homeRequest);
//        JsonNode dashNode = Json.parse(contentAsString(dashRes));
        assertEquals(OK, dashRes.status());
        JsonNode body = Json.parse(contentAsString(dashRes));
        Dashboard returnedDashboard = Json.fromJson(body, Dashboard.class);
        assertEquals(returnedDashboard, dashboard);

    }

    @Test
    public void deleteTest() {
        Dashboard dashboard = new Dashboard();
        dashboard.setName("Test");
        dashboard.setDescription("Test dashboard");
        dashboard.setId(new ObjectId());

        dashboard.setId(new ObjectId("5e5fc74ff5511f8590f4c3a5"));

        final Http.RequestBuilder homeRequest = new Http.RequestBuilder();
        homeRequest.method("PUT")
                .uri("/api/dashboard/5e5fc74ff5511f8590f4c3a5")
                .bodyJson(Json.toJson(dashboard))
                .header("Authorization", "Bearer " + token);
//                .build();
        final Result result = route(app, homeRequest);
        assertEquals(OK, result.status());
    }


//    @Test
//    public void allTest() {
//        final Http.RequestBuilder request = new Http.RequestBuilder()
//                .method("GET").uri("/api/dashboard")
//                .header("Authorization","Bearer " + token);
//        final Result result = Helpers.route(app,request);
//        assertEquals(ok().status(),result.status());
//    }


//    @After
//    @Override
//    public void stopPlay() {
//        super.stopPlay();
//        mongoDB.getMongoDatabase().drop();
//
//    }
}




























