package quarkus.hackfest.twitteringestor.entity;

import javax.json.bind.annotation.JsonbProperty;

public  class Data {

    @JsonbProperty("id")
    private String id;

    @JsonbProperty("text")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
