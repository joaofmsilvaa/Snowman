package pt.ipbeja.estig.po2.snowman.app.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SnowmanFile extends DetailsFile{

    public SnowmanFile() {
        super();
    }

    public String getCurrentDate(){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        return currentDate.format(myFormatObj);
    }

}
