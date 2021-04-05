package covid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitizenServiceTest {

    private Citizen citizen = new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110");

    @Mock
    private CitizenDao citizenDao;

    @Mock
    private CityDao cityDao;

    @InjectMocks
    private CitizenService service;


    @Test
    void saveCitizen() {
        assertTrue(service.saveToDatabase(citizen));
        verify(citizenDao).saveCitizen(argThat(c -> c.getTaj().equals("111111110")));
    }

    @Test
    void saveCitizenAlreadyExists() {
        when(citizenDao.findByTaj(any())).thenReturn(citizen);

        boolean result = service.saveToDatabase(new Citizen("John Doe", "6000", 30, "jd@gmail.com", "111111110"));
        verify(citizenDao, never()).saveCitizen(any());
        assertFalse(result);
    }

}