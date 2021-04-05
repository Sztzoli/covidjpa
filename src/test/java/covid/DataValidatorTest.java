package covid;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DataValidatorTest {

    private DataValidator validator = new DataValidator();

    @Test
    void validateName() {
        assertTrue(validator.validateName("John Doe"));
        assertFalse(validator.validateName(null));
        assertFalse(validator.validateName("   "));
    }

    @Test
    void validateZip() {
        assertTrue(validator.validateZip("6000"));
        assertFalse(validator.validateZip(null));
        assertFalse(validator.validateZip("   "));
    }

    @Test
    void validateAge() {
        assertTrue(validator.validateAge(11));
        assertFalse(validator.validateAge(10));
        assertFalse(validator.validateAge(150));
    }

    @Test
    void validateEmail() {
        assertTrue(validator.validateEmail("bv@"));
        assertFalse(validator.validateEmail("b@"));
        assertFalse(validator.validateEmail("bvgh"));
    }

    @Test
    void validateTaj() {
        assertTrue(validator.validateTaj("111111110"));
        assertFalse(validator.validateTaj("11111111"));
        assertFalse(validator.validateTaj("111111111"));
    }
}