package quarkus.hackfest.twitteringestor;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.infinispan.InfinispanOperation;

import javax.enterprise.context.Dependent;

@Dependent
public class TwitterIngestorRoute extends RouteBuilder {


    @Override
    public void configure() throws Exception {

      from("timer:twitter-timer?period={{timer.period}}")
         .routeId("tweet-ingest")
         .log("starting twitter polling")
         .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.GET)
         .setHeader(InfinispanConstants.KEY).constant("last-since-id")
         .to("{{infinispan.url}}")
         .to("bean:messageTranslatorBean?method=translateInfinispanReturn")
         .log("starting from id: ${body}")
         .to("twitter-search:{{twitter.query}}?{{twitter.search.parameters}}" )
         .split().body()
              .to("bean:messageTranslatorBean?method=translateTweetToKafka")
              .log("received body: ${body}")
              .to(ExchangePattern.InOnly,"{{kafka.url}}")
              .log("message sent to kafka")
              .setHeader(InfinispanConstants.OPERATION).constant(InfinispanOperation.PUT)
              .setHeader(InfinispanConstants.KEY).constant("last-since-id")
              .choice().when(simple(" ${headers.updateInfinispan} == 'true' "))
                  .to("{{infinispan.url}}")
                  .log("infinispan updated: ${headers."+ InfinispanConstants.VALUE+"}")
              .endChoice();

    }
}