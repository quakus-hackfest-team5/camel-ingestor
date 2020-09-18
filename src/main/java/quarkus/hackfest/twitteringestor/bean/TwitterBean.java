package quarkus.hackfest.twitteringestor.bean;

import org.apache.camel.Exchange;
import org.apache.camel.component.twitter.TwitterConstants;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import quarkus.hackfest.twitteringestor.TwitterService;
import quarkus.hackfest.twitteringestor.entity.TweetResult;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

@Singleton
@Named("twitterBean")
public class TwitterBean {

    @ConfigProperty(name = "twitter.token.bearer")
    String tokenBearer;

    @ConfigProperty(name = "twitter.url")
    String twitterUrl;

    static Logger logger = Logger.getLogger(TwitterBean.class.getName());

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    private final HttpClient httpClient = HttpClient.newBuilder()
            .executor(executorService)
            .version(HttpClient.Version.HTTP_2)
            .build();

    @Inject
    @RestClient
    TwitterService  twitter;

    public void sendTweetV2(Exchange exchange){

        String query = (String) exchange.getIn().getHeader(TwitterConstants.TWITTER_KEYWORDS) ;
        String sinceId = (String) exchange.getIn().getHeader(TwitterConstants.TWITTER_SINCEID);
        TweetResult result;

        if(sinceId.equals("1")){
            result= twitter.getTweets(query, tokenBearer);
        }else{
            result= twitter.getTweets(query, sinceId, tokenBearer);
        }

        if(result != null && result.getData() != null){
            logger.info(result.getData().size() + " results found");
            exchange.getIn().setBody(result.getData());
        }else{
            logger.info("zero results found");
            exchange.getIn().setBody(new ArrayList());
        }

    }


    private void javaHttpClient(Exchange exchange) throws IOException, InterruptedException{
        String url;

        String query = "query=" + exchange.getIn().getHeader(TwitterConstants.TWITTER_KEYWORDS);

        if(!exchange.getIn().getHeader(TwitterConstants.TWITTER_SINCEID).equals("1")){
            String sinceId =  "since_id=" + exchange.getIn().getHeader(TwitterConstants.TWITTER_SINCEID);
            url = twitterUrl + "?" + query + "&" + sinceId;
        }  else{
            url =twitterUrl + "?" + query;
        }

        logger.info(url);

        HttpResponse<String> response = httpClient.send( HttpRequest.newBuilder()
                        .GET()
                        .uri(URI.create(url))
                        .header( "content-type", "application/json")
                        .header("Accept", "application/json")
                        .header("Authorization", tokenBearer)
                        .build()
                , HttpResponse.BodyHandlers.ofString());

        logger.info(response.body());
    }

}
