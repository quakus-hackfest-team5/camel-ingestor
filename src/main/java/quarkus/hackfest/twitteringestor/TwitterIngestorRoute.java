package quarkus.hackfest.twitteringestor;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;
import javax.enterprise.context.Dependent;

@Dependent
public class TwitterIngestorRoute extends RouteBuilder {

    private static final String CACHE_KEY = "cacheKey";

    @Override
    public void configure() throws Exception {

      from("timer:twitter-timer?period={{timer.period}}")
         .routeId("tweet-ingest")
         .log("starting twitter polling")
         .to("twitter-search:{{twitter.query}}?{{twitter.search.parameters}}")
         .to("bean:latestTweetBean")
         .log("Main Tweet: ${headers.CamelTwitterKeywords} ")
         //get sinceId
          .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
          .setHeader(InfinispanConstants.KEY).simple("replySinceId")
          .to("{{infinispan.url}}")
          .to("bean:messageTranslatorBean?method=replyIdInfinispanReturn")
          //get replies
          .to("bean:twitterBean")
          .split().body()
              .to("bean:messageTranslatorBean?method=translateTweetToKafka")
              .log("Tweet body: ${body}")
              //.to(ExchangePattern.InOnly,"{{kafka.url}}")
              .log("message sent to kafka")
              .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
              .setHeader(InfinispanConstants.KEY).simple("replySinceId")
              .choice().when(simple(" ${headers.updateInfinispan} == 'true' "))
              .to("{{infinispan.url}}")
              .log("infinispan  ${headers.cacheKey}  updated: ${headers."+ InfinispanConstants.VALUE+"}")
              .endChoice();

    }
}