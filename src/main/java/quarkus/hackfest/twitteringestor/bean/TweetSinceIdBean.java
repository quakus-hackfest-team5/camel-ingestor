package quarkus.hackfest.twitteringestor.bean;

import org.apache.camel.Exchange;
import org.apache.camel.component.twitter.TwitterConstants;
import twitter4j.Status;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@Named("tweetSinceIdBean")
public class TweetSinceIdBean {

    Logger logger = Logger.getLogger(TweetSinceIdBean.class.getName());

    private long latestId = 1;

    public void updateLatestId(Exchange exchange) throws Exception {

        Status status = exchange.getIn().getBody(Status.class);

        if(latestId <  status.getId()) {
            latestId = status.getId();
            logger.info("id updated: " + latestId);
        }

    }

    public void addLatestIdToHeader(Exchange exchange){
        exchange.getIn().setHeader(TwitterConstants.TWITTER_SINCEID, getLatestId());
        logger.info("using id = " + getLatestId());
    }

    public String getLatestId() {
        return String.valueOf(latestId);
    }
}
