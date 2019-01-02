package com.example.nazrulasraf.basicactivity.other;

public class ClubData {

    String clubName, clubFaculty, clubDetails, clubAdminName;

    public ClubData(){
        //Empty constructor
    }

    public ClubData(String clubName, String clubFaculty, String clubDetails, String clubAdminName){
        this.clubName = clubName;
        this.clubFaculty = clubFaculty;
        this.clubDetails = clubDetails;
        this.clubAdminName = clubAdminName;
    }

    public String getClubName(){
        return clubName;
    }

    public String getClubFaculty(){
        return clubFaculty;
    }

    public String getClubDetails(){
        return clubDetails;
    }

    public String getClubAdminName(){
        return clubAdminName;
    }

    public void setClubName(String clubName){
        this.clubName = clubName;
    }

    public void setClubAdminName(String clubAdminName) {
        this.clubAdminName = clubAdminName;
    }

    public void setClubDetails(String clubDetails) {
        this.clubDetails = clubDetails;
    }

    public void setClubFaculty(String clubFaculty) {
        this.clubFaculty = clubFaculty;
    }
}
