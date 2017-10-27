package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.ArrayList;
import java.util.List;

public class Chat {
    private String name;
    private List<Message> messages;

    public Chat(String name) {
        this.name = name;
        messages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Message getLastMessage() {
        return this.messages.get(this.messages.size() -1);
    }
}
