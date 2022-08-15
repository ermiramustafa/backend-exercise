package controllers;

import play.test.WithApplication;

//import static play.test.Helpers.route;

public class DashboardTest extends WithApplication {
//    @Inject
//    IMongoDB mongoDB;
//
//    String token;
//    Dashboard dash1;
//    String user;
//
//    @Before
//    @Override
//    public void startPlay() {
//        super.startPlay();
//
//        mongoDB = app.injector().instanceOf(InMemoryMongoDB.class);
//
//        MongoCollection userMongo = mongoDB.getMongoDatabase()
//                .getCollection("users", User.class);
//
//        User user = new User("ermira", "password", singletonList("62e90a69e08d17cd567438dd"));
////        userMongo.insertOne(new User("ermira", "password", Collections.singletonList("Admin")));
//        user.setId(new ObjectId());
//        userMongo.insertOne(user);
//
//        ObjectNode node = Json.newObject();
//        node.put("username", user.getUsername());
//        node.put("password", user.getPassword());
//
//        Http.RequestBuilder homeRequest = new Http.RequestBuilder()
//                .method("POST")
//                .bodyJson(node)
//                .uri("api/authenticate/");
//
//        Result userRes = route(app, homeRequest);
//        JsonNode tokenNode = Json.parse(contentAsString(userRes));
//        //token from the user
//        token = tokenNode.get("token").asText();
//
//
//        //setting up dashboards
//        MongoCollection<Dashboard> dashboards = mongoDB.getMongoDatabase()
//                .getCollection("dashboard", Dashboard.class);
//
//        dash1 = new Dashboard("Kids","utilize cross-platform action-items",
//                new ObjectId(), 0, emptyList(),
//                singletonList("62e257f6dbff353f56ef3024"), singletonList("62ea2bb98f2b3cbbd9d853de"), emptyList());
//        dash1.setId(new ObjectId());
//
////        Result res = getResult(POST, dashboard, "/api/dashboard/", "Bearer ", +token);
//        Http.RequestBuilder builder = new Http.RequestBuilder();
//        builder.method("POST")
//                .uri("/api/dashboard/")
//                .bodyJson(Json.toJson(dash1))
//                .header("Authorization", "Bearer " + token)
//                .build();
//        final Result dashRes = route(app, builder);
////        JsonNode dashNode = Json.parse(contentAsString(dashRes));
//
//
////        token = getToken(new User("ermira"))
//    }
//
//
////    @Test
////    public void save() {
////        Dashboard dashboard = new Dashboard();
////        dashboard.setName("Test");
////        dashboard.setDescription("Test dashboard");
////        dashboard.setId(new ObjectId());
////
////        Http.RequestBuilder builder = new Http.RequestBuilder();
////        builder.method("POST")
////                .uri("/api/dashboard/")
////                .bodyJson(Json.toJson(dashboard))
////                .header("Authorization", "Bearer " + token)
////                .build();
////        Result dashRes = route(app, builder);
//////        JsonNode dashNode = Json.parse(contentAsString(dashRes));
////        assertEquals(OK, dashRes.status());
////    }
//
//
//    @Test
//    public void allTest() {
//        final Http.RequestBuilder request = new Http.RequestBuilder()
//                .method("GET").uri("/api/dashboard")
//                .header("Authorization","Bearer " + token);
//        final Result result = Helpers.route(app,request);
//        assertEquals(ok().status(),result.status());
//    }
//
//
//
//    @After
//    @Override
//    public void stopPlay() {
//        super.stopPlay();
//        mongoDB.getMongoDatabase().drop();
//
//    }
}




























