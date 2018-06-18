package model;

public class Status {

    String statusId, statusText, mobileNumer, emailAddress;

    public Status(String statusId, String statusText, String mobileNumer, String emailAddress) {
        this.statusId = statusId;
        this.statusText = statusText;
        this.mobileNumer = mobileNumer;
        this.emailAddress = emailAddress;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getMobileNumer() {
        return mobileNumer;
    }

    public void setMobileNumer(String mobileNumer) {
        this.mobileNumer = mobileNumer;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}