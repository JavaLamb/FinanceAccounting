package main.servletUi.core.Router;

import lombok.AllArgsConstructor;
import lombok.Getter;
import main.servletUi.core.WebController;


@AllArgsConstructor
@Getter
public class Route {
    private final String method;
    private final String url;
    private final WebController webController;
}
