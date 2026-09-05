package main.servletUi.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RouteRes {
    Map<String, String> parameters;
    WebController webController;
}
