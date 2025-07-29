package com.creditos.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de resposta para health check da API
 * Separado do controller seguindo o princípio Single Responsibility
 *
 * @author Ednilton Curt Rauh
 * @version 1.0.0
 */
public class HealthResponseDTO {

    @JsonProperty("status")
    private String status;

    @JsonProperty("service")
    private String service;

    @JsonProperty("version")
    private String version;

    @JsonProperty("timestamp")
    private long timestamp;

    /**
     * Construtor padrão
     */
    public HealthResponseDTO() {}

    /**
     * Construtor completo
     */
    public HealthResponseDTO(String status, String service, String version, long timestamp) {
        this.status = status;
        this.service = service;
        this.version = version;
        this.timestamp = timestamp;
    }

    /**
     * Factory method para criar resposta de sucesso
     */
    public static HealthResponseDTO createHealthyResponse(String serviceName, String version) {
        return new HealthResponseDTO("UP", serviceName, version, System.currentTimeMillis());
    }

    /**
     * Factory method para criar resposta de erro
     */
    public static HealthResponseDTO createUnhealthyResponse(String serviceName, String version) {
        return new HealthResponseDTO("DOWN", serviceName, version, System.currentTimeMillis());
    }

    // Getters e Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "HealthResponseDTO{" +
                "status='" + status + '\'' +
                ", service='" + service + '\'' +
                ", version='" + version + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}