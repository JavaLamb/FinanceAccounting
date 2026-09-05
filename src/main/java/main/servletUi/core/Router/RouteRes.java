package main.servletUi.core.Router;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import main.servletUi.core.WebController;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RouteRes {
    Map<String, String> parameters;
    WebController webController;
}
