package quarkus.hackfest.twitteringestor.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

public class TweetResult {

    @JsonbProperty("data")
    private List<Data> data;

    @JsonbProperty("meta")
    private Meta meta;


    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
