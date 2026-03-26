package model;

public class Person {
    protected int personId;
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String phoneNo;
    protected String city;

    public Person(int personId, String firstName, String lastName,
                  String email, String phoneNo, String city) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.city = city;
    }
}