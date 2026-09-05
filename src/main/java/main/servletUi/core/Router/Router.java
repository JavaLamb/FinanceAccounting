package main.servletUi.core.Router;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Router {
    private final List<Route> routeList;

    public Router(List<Route> routeList) {
        this.routeList = routeList;
    }

    public RouteRes find(String method, String url) {
        for (Route route : routeList) {
            Map<String, String> parameters = new HashMap<>();
            boolean stillNice = true;
            if (Objects.equals(method, route.getMethod())) {
                String[] urlPart = url.split("/");
                String[] routePart = route.getUrl().split("/");
                if (urlPart.length == routePart.length) {
                    for (int i = 0; i < urlPart.length; i++) {
                        if (routePart[i].startsWith("{")) {
                            parameters.put(routePart[i].substring(1, routePart[i].length() - 1), urlPart[i]);
                        } else if (!routePart[i].equals(urlPart[i])) {
                            stillNice = false;
                            break;
                        }
                    }
                    if (stillNice) {
                        return new RouteRes(parameters, route.getWebController());
                    }
                }
            }
        }
        return null;
    }
}