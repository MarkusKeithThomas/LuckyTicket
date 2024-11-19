package ticket.luckyticket.saler.model;
import java.util.Observable;

public class User extends Observable {
    private String email;
    private String password;
    private String re_password;
    private String name;
    private String phone;
    private String idCard;
    private String rating;
    private String image_Url_Avatar;
    private String userId;
    private String moneyAccount;
    public User(){}

    public String getRe_password() {
        return re_password;
    }

    public void setRe_password(String re_password) {
        this.re_password = re_password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
       return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdCard() {
        return idCard;
    }
    public String getHideCCCD(){
        if (idCard != null && idCard.length() > 4) {
            return "******" + idCard.substring(idCard.length() - 4);
        } else {
            return "";
        }    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getRating() {
        return getRatingNull();
    }
    private String getRatingNull(){
        if (rating == null){
            return rating = "10";
        } else {
            return rating;
        }
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getImage_Url_Avatar() {
        return image_Url_Avatar;
    }

    public void setImage_Url_Avatar(String image_Url_Avatar) {
        this.image_Url_Avatar = image_Url_Avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMoneyAccount() {
        return getMoneyAccountNull();
    }
    private String getMoneyAccountNull(){
        if (moneyAccount == null){
            return moneyAccount = "0";
        } else {
            return moneyAccount;
        }
    }

    public void setMoneyAccount(String moneyAccount) {
        this.moneyAccount = moneyAccount;
    }

}
