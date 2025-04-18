package pt.ipbeja.estig.po2.snowman.po2.app.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.ipbeja.estig.po2.snowman.app.model.Model;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    @DisplayName("Say hello to Maria")
    void sayHello() {
        // arrange
        Model model = new Model("Maria");

        // act
        String actualGreeting = model.sayHello();

        // assert
        String expectedGreeting = "Hello, Maria!";
        assertEquals(expectedGreeting, actualGreeting);
    }
}