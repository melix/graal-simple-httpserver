import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import groovy.transform.CompileStatic

@CompileStatic
abstract class HttpServerGroovy {

    // VERY dirty trick to avoid the creation of a groovy.lang.Reference
    static File baseDir

    static void main(String[] args) {
        def port = args.length > 0 ? args[0].toInteger() : 8080
        baseDir = args.length > 1 ? new File(args[1]).canonicalFile : new File(".").canonicalFile

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
                    String listing = linkify(base, file.list()).join("\n")
                    sendResponse(exchange, 200, String.format("<html><body>%s</body></html>", listing))

                } else if (!file.file) {
                    sendResponse(exchange, 404, "404 (Not Found)\n")
                } else {
                    sendResponse(exchange, 200, new FileInputStream(file))
                }
            }
        })
        server.executor = null
        System.out.println(String.format("Listening at http://localhost:%s/", port))
        server.start()
    }

    private static List<String> linkify(String base, String[] files) {
        def out = new ArrayList<String>(files.length)
        for (int i = 0; i < files.length; i++) {
            String file = files[i]
            out << String.format("<ul><a href=\"%s/%s\">%s</a></ul>", base, file, file)
        }
        out
    }

    private static void sendResponse(HttpExchange ex, int code, InputStream ins) {
        ex.sendResponseHeaders(code, 0)
        ex.responseBody << ins
        ex.responseBody.flush()
        ex.responseBody.close()
    }

    private static void sendResponse(HttpExchange ex, int code, byte[] answer) {
        ex.sendResponseHeaders(code, answer.length)
        ex.responseBody.write(answer)
        ex.responseBody.close()
    }

    private static void sendResponse(HttpExchange ex, int code, String answer) {
        sendResponse(ex, code, answer.bytes)
    }

}
