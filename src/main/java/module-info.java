module my.project.digital_library {
    requires javafx.controls;
    requires javafx.fxml;


    opens my.project.digital_library to javafx.fxml;
    exports my.project.digital_library;
}