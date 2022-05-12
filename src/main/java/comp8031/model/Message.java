package comp8031.model;

public class Message {

    private String content;

    public Message() {}

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return String.format("Content: %s", content);
    }
}
