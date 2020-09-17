package quarkus.hackfest.twitteringestor.bean;

import com.google.gson.Gson;
import org.apache.camel.Exchange;
import org.apache.camel.component.infinispan.InfinispanConstants;
import org.apache.camel.component.twitter.TwitterConstants;
import quarkus.hackfest.datamodel.Guess;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.logging.Logger;

@Singleton
@Named("messageTranslatorBean")
public class MessageTranslatorBean {

    static Logger logger = Logger.getLogger(MessageTranslatorBean.class.getName());

    long actualSinceId = 0;

    public void translateTweetToKafka(Exchange exchange){

        Status status = exchange.getIn().getBody(Status.class);

        String gp =  null ;

        for ( HashtagEntity hashtag : status.getHashtagEntities()  ) {
            if( hashtag.getText().contains("GP") ){
                gp = hashtag.getText();
                break;
            }
        }

        Guess guess = new Guess();
        guess.setGp(gp);
        guess.setDriver(removeHashtag(status.getText()).trim());

       exchange.getIn().setBody(new Gson().toJson(guess));

       //records id for infinispan


        if(status.getId() > actualSinceId){
            exchange.getIn().setHeader(InfinispanConstants.VALUE, status.getId());
            exchange.getIn().setHeader("updateInfinispan", "true");
            actualSinceId = status.getId();
        }else{
            exchange.getIn().setHeader("updateInfinispan", "false");
        }

    }



    public void translateInfinispanReturn(Exchange exchange){

        String sinceId = exchange.getIn().getBody(String.class);

        if(sinceId == null){
            sinceId = "1";

        }

        actualSinceId = Long.parseLong(sinceId);
        exchange.getIn().setHeader(TwitterConstants.TWITTER_SINCEID, sinceId);

    }

    public static String removeHashtag(String textWithHashtag){

        String textWithoutHashtag = null;

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


}
