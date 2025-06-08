package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.Position;
import pt.ipbeja.estig.po2.snowman.app.model.PositionsFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PositionsFile class, ensuring correct file creation,
 * writing of positions, and column-index-to-letter conversion.
 *
 * Tests covered:
 * 1. File creation and existence.
 * 2. Writing a single position entry.
 * 3. Writing multiple position entries.
 * 4. Converting a numeric column index to its corresponding letter.
 *
 * @author João Silva
 * @author Paulo Neves
 */
public class PositionsFileModelTest {

    private File tempFile;
    private PositionsFile data;
    private Position previous;
    private Position current;

    /**
     * Sets up a new temporary file and PositionsFile instance before each test.
     * Initializes two sample positions for writing.
     */
    @BeforeEach
    public void setUp() throws IOException {
        // Create and immediately delete a temp file so we can test file creation logic
        tempFile = File.createTempFile("test_positions", ".txt");
        tempFile.delete();

        // Initialize PositionsFile with the temp file path and create the file
        data = new PositionsFile();
        data.setFilename(tempFile.getAbsolutePath());
        data.createFile();

        // Prepare two Position instances for testing
        previous = new Position(1, 1);
        current  = new Position(1, 2);
    }

    /**
     * Cleans up the temporary file after each test to avoid leftover files.
     */
    @AfterEach
    public void tearDown() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    /**
     * Verifies that createFile() successfully creates the file on disk.
     */
    @Test
    @DisplayName("Create file to store the position")
    public void testCreateFile() {
        assertTrue(data.isFileCreated(), "Expected fileCreated to be true after createFile()");
        assertTrue(new File(data.getFileName()).exists(), "Expected the file to exist on disk");
    }

    /**
     * Verifies writing a single position pair to the file in the format "(row,Col) -> (row,Col)".
     */
    @Test
    @DisplayName("Store position in file")
    public void testStorePosition() throws IOException {
        // Use different coordinates for this test
        previous = new Position(1, 0);
        current  = new Position(1, 1);

        // Write the single position entry
        data.storePosition(previous, current);

        // Read back the file contents and verify the format
        List<String> lines = Files.readAllLines(new File(data.getFileName()).toPath());
        assertEquals(1, lines.size(), "Expected exactly one line in the file");
        assertEquals("(1,A) -> (1,B)", lines.get(0).trim(), "Line content mismatch");
    }

    /**
     * Verifies that multiple calls to storePosition() append new entries correctly.
     */
    @Test
    @DisplayName("Store multiple positions in file")
    public void testStoreMultiplePositions() throws IOException {
        // First write
        data.storePosition(previous, current);
        List<String> lines = Files.readAllLines(new File(data.getFileName()).toPath());
        assertEquals("(1,B) -> (1,C)", lines.get(0).trim(), "First entry mismatch");

        // Then write a second entry
        Position newPosition = new Position(2, 2);
        data.storePosition(current, newPosition);

        lines = Files.readAllLines(new File(data.getFileName()).toPath());
        assertEquals(2, lines.size(), "Expected two lines after two writes");
        assertEquals("(1,B) -> (1,C)", lines.get(0).trim(), "First line unchanged after second write");
        assertEquals("(1,C) -> (2,C)", lines.get(1).trim(), "Second entry mismatch");
    }

    /**
     * Verifies that convertToLetter() returns the correct column letter for a given index.
     */
    @Test
    @DisplayName("Convert position to letter")
    public void testConvertPositionToLetter() {
        String letter = current.convertToLetter(1);
        assertEquals("A", letter, "Column index 1 should convert to 'A'");
    }
}
