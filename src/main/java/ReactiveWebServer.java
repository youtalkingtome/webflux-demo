import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServer;

public class ReactiveWebServer {

    public static void main(String[] args) {
        // Define a router function to handle GET requests to the root path
        RouterFunction<ServerResponse> route = RouterFunctions.route(
                RequestPredicates.GET("/"),
                request -> ServerResponse.ok().contentType(MediaType.TEXT_HTML)
                        .body(BodyInserters.fromValue("<html><body>Hello World From Vikas!</body></html>"))
        );

        // Start the server
        ReactorHttpHandlerAdapter httpHandler = new ReactorHttpHandlerAdapter(RouterFunctions.toHttpHandler(route));
        HttpServer.create().host("localhost").port(8080).handle(httpHandler).bindNow().onDispose().block();
    }
}
