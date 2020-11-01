package no.kristiania.db;

public class Member implements SetId {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private Long departmentId;

    public Member(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        departmentId = null;
    }

    public Member(int id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Member() {
    }

    public Member(String memberFirstName, String memberLastName, String memberEmail, Long departmentId) {
        this.firstName = memberFirstName;
        this.lastName = memberLastName;
        this.email = memberEmail;
        this.departmentId = departmentId;
    }

    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDepartmentId(Long departmentId) {
        if(departmentId == 0L) {
            this.departmentId = null;
        } else {
            this.departmentId = departmentId;
        }
    }

    public Long getDepartmentId() { return departmentId; }
}
