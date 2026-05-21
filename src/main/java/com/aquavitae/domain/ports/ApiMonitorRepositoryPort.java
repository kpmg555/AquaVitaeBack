package com.aquavitae.domain.ports;

import com.aquavitae.domain.models.ApiAlert;
import com.aquavitae.domain.models.ApiMonitorStatus;

import java.util.List;

public interface ApiMonitorRepositoryPort {
    List<ApiMonitorStatus> findStatus();
    List<ApiAlert> findActiveAlerts();
}