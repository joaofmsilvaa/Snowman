package pt.ipbeja.estig.po2.snowman.app.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * SnowmanFile extends DetailsFile and provides a method for generating
 * a timestamp string in the format "yyyyMMddHHmmss". This timestamp is
 * used to name the output file uniquely when a snowman is completed.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class SnowmanFile extends DetailsFile {

    public SnowmanFile() {
        super();
    }

    /**
     * Returns the current date and time formatted as "yyyyMMddHHmmss".
     * Example: 20250531143025 corresponds to 31 May 2025, 14:30:25.
     *
     * @return a timestamp string in "yyyyMMddHHmmss" format
     */
    public String getCurrentDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return currentDateTime.format(formatter);
    }

}
