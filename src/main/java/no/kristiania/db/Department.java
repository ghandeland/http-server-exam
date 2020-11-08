package no.kristiania.db;

public class Department implements SetId {
    private final String name;
    private long id;

    public Department(String name) {
        this.name = name;
    }

    public Department(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
}
