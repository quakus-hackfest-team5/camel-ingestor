package quarkus.hackfest.twitteringestor;

import org.apache.camel.builder.RouteBuilder;

public class TwitterIngestorRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

      from("timer:twitter-timer?period={{timer.period}}")
         .routeId("tweet-ingest")
         .log("starting twitter polling")
         .to("bean:tweetSinceIdBean?method=addLatestIdToHeader")
         .to("twitter-search:{{twitter.query}}?{{twitter.search.parameters}}" )
         .split().body()
              .to("bean:tweetSinceIdBean?method=updateLatestId")
              .log("received body: ${body}");


    }
}