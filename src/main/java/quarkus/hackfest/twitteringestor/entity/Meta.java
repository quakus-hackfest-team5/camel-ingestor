package quarkus.hackfest.twitteringestor.entity;

import javax.json.bind.annotation.JsonbProperty;

public class Meta{

    @JsonbProperty("newest_id")
    private String newestId;

    @JsonbProperty("oldest_id")
    private String oldestId;

    @JsonbProperty("result_count")
    private int resultCount;

    public String getNewestId() {
        return newestId;
    }

    public void setNewestId(String newestId) {
        this.newestId = newestId;
    }

    public String getOldestId() {
        return oldestId;
    }

    public void setOldestId(String oldestId) {
        this.oldestId = oldestId;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}