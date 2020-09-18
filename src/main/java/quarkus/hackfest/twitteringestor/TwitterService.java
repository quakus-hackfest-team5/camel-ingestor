package quarkus.hackfest.twitteringestor;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import quarkus.hackfest.twitteringestor.entity.TweetResult;

import javax.ws.rs.*;

@Path("/2")
@RegisterRestClient
public interface TwitterService {


    @GET
    @Path("/tweets/search/recent")
    @Produces("application/json")
    TweetResult getTweets(@QueryParam("query") String query, @HeaderParam("Authorization")  String authorization);

    @GET
    @Path("/tweets/search/recent")
    @Produces("application/json")
    TweetResult getTweets(@QueryParam("query") String query,@QueryParam("since_id") String sinceId, @HeaderParam("Authorization")  String authorization);

}
