package quarkus.hackfest.twitteringestor.bean;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.twitter.TwitterConstants;
import quarkus.hackfest.datamodel.Guess;
import quarkus.hackfest.twitteringestor.entity.Data;
import quarkus.hackfest.twitteringestor.entity.TweetResult;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Logger;

import static quarkus.hackfest.twitteringestor.bean.LatestTweetBean.CURRENT_GP;

@Singleton
@Named("messageTranslatorBean")
public class MessageTranslatorBean {

    static Logger logger = Logger.getLogger(MessageTranslatorBean.class.getName());

    long actualSinceId = 0;

    private static String DEFAULT_SINCE_ID = "1304415889189240833";

    public void translateTweetToKafka(Exchange exchange){

        Data status = exchange.getIn().getBody(Data.class);

        Guess guess = new Guess();
        guess.setGp( (String) exchange.getIn().getHeader(CURRENT_GP));
        guess.setDriver(removeHandler(removeHashtag(status.getText())).trim());

        exchange.getIn().setBody(new Gson().toJson(guess));

        //records id for infinispan

        if(Long.parseLong(status.getId()) > actualSinceId){
            exchange.getIn().setHeader(InfinispanConstants.VALUE, status.getId());
            exchange.getIn().setHeader("updateInfinispan", "true");
            actualSinceId = Long.parseLong(status.getId());
        }else{
            exchange.getIn().setHeader("updateInfinispan", "false");
        }

    }

    public void replyIdInfinispanReturn(Exchange exchange){

        String replyId = exchange.getIn().getBody(String.class);

        if(replyId == null){
            replyId = "1";
            logger.info("no previous replyId");
        }

        actualSinceId = Long.parseLong(replyId);
        exchange.getIn().setHeader(TwitterConstants.TWITTER_SINCEID, replyId);

    }

    public static String removeHashtag(String textWithHashtag){

        String textWithoutHashtag = textWithHashtag;

        char[] letters = textWithHashtag.toCharArray();

        for ( int it = 0 ; it < letters.length ; it++){

            if (letters[it] == '#' ){
                textWithoutHashtag = textWithHashtag.substring(0,it);
                logger.fine("hashtag removed");
                break;
            }
        }

        return textWithoutHashtag;
    }

    public static String removeHandler(String text){
        return text.replace("@gui_camposo", "");
    }


}
