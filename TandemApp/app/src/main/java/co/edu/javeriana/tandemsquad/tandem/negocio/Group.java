package co.edu.javeriana.tandemsquad.tandem.negocio;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private String name;
    private List<User> members;

    public Group(String name) {
        this.name = name;
        members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<User> getMembers() {
        return members;
    }
}
