package com.aquavitae.application.usecase;

import com.aquavitae.domain.ports.FuenteClimaPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para CheckExternalApisUseCase.
 *
 * Este caso de uso se encarga de ejecutar una revisión manual
 * de las APIs externas utilizadas por el sistema para obtener
 * información climática.
 *
 * Las APIs externas verificadas son:
 * - Open-Meteo
 * - NASA POWER
 * - SMN
 *
 * En estas pruebas se mockean los puertos FuenteClimaPort para evitar
 * llamadas reales a servicios externos. De esta forma se valida únicamente
 * que el caso de uso invoque correctamente a cada fuente y que tolere
 * errores individuales sin detener la revisión completa.
 */
class CheckExternalApisUseCaseTest {

    /**
     * Mock de la fuente climática Open-Meteo.
     * Se utiliza para validar que el caso de uso intente consultar esta API.
     */
    private FuenteClimaPort openMeteo;

    /**
     * Mock de la fuente climática NASA POWER.
     * Se utiliza para validar que el caso de uso intente consultar esta API.
     */
    private FuenteClimaPort nasaPower;

    /**
     * Mock de la fuente climática SMN.
     * Se utiliza para validar que el caso de uso intente consultar esta API.
     */
    private FuenteClimaPort smn;

    /**
     * Caso de uso que será probado.
     */
    private CheckExternalApisUseCase checkExternalApisUseCase;

    /**
     * Configuración inicial antes de cada prueba.
     *
     * Se crean mocks de las tres fuentes climáticas y se inyectan manualmente
     * en el caso de uso, ya que esta prueba es unitaria y no levanta el
     * contexto completo de Quarkus.
     */
    @BeforeEach
    void setUp() {
        openMeteo = mock(FuenteClimaPort.class);
        nasaPower = mock(FuenteClimaPort.class);
        smn = mock(FuenteClimaPort.class);

        checkExternalApisUseCase = new CheckExternalApisUseCase();
        checkExternalApisUseCase.openMeteo = openMeteo;
        checkExternalApisUseCase.nasaPower = nasaPower;
        checkExternalApisUseCase.smn = smn;
    }

    /**
     * Verifica que el caso de uso consulte las tres APIs externas.
     *
     * Escenario:
     * - Se ejecuta la revisión manual de APIs.
     * - Ninguna fuente climática lanza errores.
     *
     * Resultado esperado:
     * - Open-Meteo es consultada una vez.
     * - NASA POWER es consultada una vez.
     * - SMN es consultada una vez.
     */
    @Test
    void execute_shouldCallAllExternalApis() {
        checkExternalApisUseCase.execute();

        verify(openMeteo, times(1)).obtenerDatos(anyList());
        verify(nasaPower, times(1)).obtenerDatos(anyList());
        verify(smn, times(1)).obtenerDatos(anyList());
    }

    /**
     * Verifica que un error en Open-Meteo no detenga la revisión
     * de las demás APIs externas.
     *
     * Escenario:
     * - Open-Meteo lanza una excepción.
     * - NASA POWER y SMN no fallan.
     *
     * Resultado esperado:
     * - La excepción de Open-Meteo es capturada internamente.
     * - NASA POWER se sigue consultando.
     * - SMN se sigue consultando.
     */
    @Test
    void execute_shouldContinueWhenOpenMeteoFails() {
        doThrow(new RuntimeException("Open-Meteo error"))
                .when(openMeteo).obtenerDatos(anyList());

        checkExternalApisUseCase.execute();

        verify(openMeteo, times(1)).obtenerDatos(anyList());
        verify(nasaPower, times(1)).obtenerDatos(anyList());
        verify(smn, times(1)).obtenerDatos(anyList());
    }

    /**
     * Verifica que un error en NASA POWER no detenga la revisión
     * de las demás APIs externas.
     *
     * Escenario:
     * - Open-Meteo responde correctamente.
     * - NASA POWER lanza una excepción.
     * - SMN no falla.
     *
     * Resultado esperado:
     * - La excepción de NASA POWER es capturada internamente.
     * - SMN se sigue consultando.
     */
    @Test
    void execute_shouldContinueWhenNasaPowerFails() {
        doThrow(new RuntimeException("NASA POWER error"))
                .when(nasaPower).obtenerDatos(anyList());

        checkExternalApisUseCase.execute();

        verify(openMeteo, times(1)).obtenerDatos(anyList());
        verify(nasaPower, times(1)).obtenerDatos(anyList());
        verify(smn, times(1)).obtenerDatos(anyList());
    }

    /**
     * Verifica que el caso de uso no propague errores aunque todas
     * las APIs externas fallen.
     *
     * Escenario:
     * - Open-Meteo lanza una excepción.
     * - NASA POWER lanza una excepción.
     * - SMN lanza una excepción.
     *
     * Resultado esperado:
     * - El método execute termina sin lanzar excepción.
     * - Las tres fuentes climáticas fueron llamadas.
     */
    @Test
    void execute_shouldNotThrowExceptionWhenAllApisFail() {
        doThrow(new RuntimeException("Open-Meteo error"))
                .when(openMeteo).obtenerDatos(anyList());

        doThrow(new RuntimeException("NASA POWER error"))
                .when(nasaPower).obtenerDatos(anyList());

        doThrow(new RuntimeException("SMN error"))
                .when(smn).obtenerDatos(anyList());

        checkExternalApisUseCase.execute();

        verify(openMeteo, times(1)).obtenerDatos(anyList());
        verify(nasaPower, times(1)).obtenerDatos(anyList());
        verify(smn, times(1)).obtenerDatos(anyList());
    }
}