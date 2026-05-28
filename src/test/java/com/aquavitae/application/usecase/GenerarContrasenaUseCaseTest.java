package com.aquavitae.application.usecase;

import com.aquavitae.application.dto.GenerarContrasenaResponseDto;
import com.aquavitae.domain.ports.FirebaseAuthPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerarContrasenaUseCaseTest {

    @Mock
    FirebaseAuthPort firebaseAuthPort;

    @InjectMocks
    GenerarContrasenaUseCase useCase;

    @Test
    void execute_returnsGeneratedPassword() {
        String generated = "Abc123!@#";
        when(firebaseAuthPort.generarContrasenaAleatoria()).thenReturn(generated);

        GenerarContrasenaResponseDto response = useCase.execute();

        assertNotNull(response);
        assertEquals(generated, response.getContrasena());
        verify(firebaseAuthPort).generarContrasenaAleatoria();
    }
}