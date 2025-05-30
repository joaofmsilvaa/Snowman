package pt.ipbeja.estig.po2.snowman.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SnowmanFile extends DetailsFile{

    public SnowmanFile() {
        super();
    }

    public String getCurrentDate(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return currentDateTime.format(formatter);
    }

}
