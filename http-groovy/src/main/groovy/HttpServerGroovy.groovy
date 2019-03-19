import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic

@CompileStatic
abstract class HttpServerGroovy {

    static void main(String[] args) {
        def port = args.length > 0 ? args[0].toInteger() : 8080
        def baseDir = args.length > 1 ? new File(args[1]).canonicalFile : new File(".").canonicalFile

        def server = HttpServer.create(new InetSocketAddress(port), 0)
        server.createContext("/", new HttpHandler() {
            @Override
            void handle(HttpExchange exchange) throws IOException {
                def uri = exchange.requestURI
                def file = new File(baseDir, uri.path).canonicalFile
                if (!file.path.startsWith(baseDir.path)) {
                    sendResponse(exchange, 403, "403 (Forbidden)\n")
                } else if (file.directory) {
                    String base = file == baseDir ? '': uri.path
                    String listing = file.list()
                            .collect { "<li><a href=\"${base}/${it}\">${it}</a></li>" }
                            .join("\n")
                    sendResponse(exchange, 200, "<html><body><ul>${listing}</ul></body></html>")

                } else if (!file.file) {
                    sendResponse(exchange, 404, "404 (Not Found)\n")
                } else {
                    sendResponse(exchange, 200, new FileInputStream(file))
                }
            }
        })
        server.executor = null
        println "Listening at http://localhost:${port}/"
        server.start()
    }

    static void sendResponse(HttpExchange ex, int code, InputStream ins) {
        ex.sendResponseHeaders(code, 0)
        ex.responseBody << ins
        ex.responseBody.flush()
        ex.responseBody.close()
    }

    static void sendResponse(HttpExchange ex, int code, byte[] answer) {
        ex.sendResponseHeaders(code, answer.length)
        ex.responseBody.write(answer)
        ex.responseBody.close()
    }

    static void sendResponse(HttpExchange ex, int code, String answer) {
        sendResponse(ex, code, answer.bytes)
    }

}
