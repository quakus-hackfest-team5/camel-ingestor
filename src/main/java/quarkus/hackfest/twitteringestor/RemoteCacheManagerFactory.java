package quarkus.hackfest.twitteringestor;

import org.infinispan.client.hotrod.RemoteCacheManager;
import quarkus.hackfest.twitteringestor.bean.CacheQualifier;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

//TODO remove this class. We need a better way to do this
public class RemoteCacheManagerFactory {

    @Inject
    RemoteCacheManager cacheManager;

    @Produces
    @Named("cacheBean")
    @CacheQualifier
    public RemoteCacheManager newRemoteCacheManager() {
        return cacheManager;
    }


}
