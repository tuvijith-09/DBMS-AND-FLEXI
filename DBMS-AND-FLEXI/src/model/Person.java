package model;

public class Person {
    protected int personId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNo;
    protected String city;

    // Constructor to build the object
    public Person(int personId, String firstName, String lastName,
                  String email, String phoneNo, String city) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.city = city;
    }

    // --- GETTERS (This fixes the compile errors!) ---
    public int getPersonId() { return personId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNo() { return phoneNo; }
    public String getCity() { return city; }
}