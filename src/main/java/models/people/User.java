package models.people;

public class User {
    private String userName;
    private String userRole; //Author or Reader

    //No-arg constructor
    public User(){}

    //Parametrized constructor to initialize the user
    public User(String userName, String userRole){
        this.userName = userName;
        this.userRole = userRole;
    }


    //Set methods

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    //Get methods

    public String getUserName() {
        return userName;
    }

    public String getUserRole() {
        return userRole;
    }



}
