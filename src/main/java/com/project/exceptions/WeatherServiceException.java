package com.project.exceptions;

public class WeatherServiceException extends RuntimeException {
  public WeatherServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}