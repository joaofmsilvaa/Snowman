package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.PositionsFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PositionsFileModelTest {

    private File tempFile;
    private PositionsFile data;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("test_positions", ".txt");
        tempFile.delete();
        data = new PositionsFile(tempFile.getAbsolutePath());
    }

    @AfterEach
    void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    @DisplayName("Create file to store the position")
    void testCreateFile() {
        assertTrue(data.isFileCreated());
        assertTrue(new File(data.getPositionsFileName()).exists());
    }

    @Test
    @DisplayName("Store position in file")
    void testStorePosition() throws IOException {
        data.storePosition(1, 1, 1,2);

        List<String> lines = Files.readAllLines(new File(data.getPositionsFileName()).toPath());
        assertEquals(1, lines.size());
        assertEquals("(1,A) -> (1,B)", lines.get(0).trim());
    }

    @Test
    @DisplayName("Store multiple positions in file")
    void testStoreMultiplePositions() throws IOException {
        data.storePosition(1, 1, 1, 2);

        List<String> lines = Files.readAllLines(new File(data.getPositionsFileName()).toPath());
        assertEquals("(1,A) -> (1,B)", lines.get(0).trim());
        System.out.println(lines.get(0).trim());

        data.storePosition(1, 2, 2, 2);

        lines = Files.readAllLines(new File(data.getPositionsFileName()).toPath());
        assertEquals(2, lines.size());
        assertEquals("(1,A) -> (1,B)", lines.get(0).trim());
        assertEquals("(1,B) -> (2,B)", lines.get(1).trim());
        System.out.println(lines.get(1).trim());
    }

    @Test
    @DisplayName("Convert position to letter")
    void testConvertPositionToLetter() {
        String letter = data.convertToLetter(1);

        assertEquals("A", letter);
    }

}
