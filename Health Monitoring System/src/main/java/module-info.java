module project.healthmonitoringsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens project.healthmonitoringsystem to javafx.fxml;
    exports project.healthmonitoringsystem;
}