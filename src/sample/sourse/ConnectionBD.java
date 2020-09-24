package sample.sourse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBD {

    final String DRIVER = "com.mysql.cj.jdbc.Driver";
    final String URL = "jdbc:mysql://ittechpu.beget.tech/ittechpu_taskmgr?" +
            "useUnicode=true&serverTimezone=UTC&useSSL=true&verifyServerCertificate=false";
    final String USER_BD = "ittechpu_taskmgr";
    final String PASSWORD_BD = "Qq159753";

    public String getDRIVER() {
        return DRIVER;
    }

    public String getURL() {
        return URL;
    }

    public String getUSER_BD() {
        return USER_BD;
    }

    public String getPASSWORD_BD() {
        return PASSWORD_BD;
    }


}
