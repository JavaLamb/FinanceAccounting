package main.servletUi.core;

import lombok.AllArgsConstructor;
import lombok.Getter;



@AllArgsConstructor
@Getter
public class Route {
    private final String method;
    private final String url;
    private final WebController webController;
}
