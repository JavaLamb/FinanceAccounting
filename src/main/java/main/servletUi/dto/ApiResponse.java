package main.servletUi.dto;

public record ApiResponse<Resp>(int status, Resp response) {

}
