import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer.create
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream
import java.net.InetSocketAddress

fun main(args: Array<String>) {
    val port = if (args.size > 0) args[0].toInt() else 8080
    val baseDir = if (args.size > 1) File(args[1]).canonicalFile else File(".").canonicalFile

    create(InetSocketAddress(port), 0).run {
        createContext("/") { exchange ->
            exchange.run {
                val file = File(baseDir, requestURI.path).canonicalFile
                if (!file.path.startsWith(baseDir.path)) {
                    sendResponse(403, "403 (Forbidden)\n")
                } else if (file.isDirectory) {
                    val base = if (file == baseDir) "" else requestURI.path
                    sendResponse(200, "<html><body>" +
                            file.list().map { "<ul><a href=\"$base/${it}\">${it}</a></ul>" }.joinToString("\n") + "</body></html>")

                } else if (!file.isFile) {
                    sendResponse(404, "404 (Not Found)\n")
                } else {
                    sendResponse(200) {
                        FileInputStream(file).use {
                            it.copyTo(this)
                        }
                    }
                }
            }
        }
        executor = null
        println("Listening at http://localhost:$port/")
        start()
    }
}

private inline fun HttpExchange.sendResponse(code: Int, answer: OutputStream.() -> Unit) = run {
    sendResponseHeaders(code, 0)
    responseBody.use(answer)
}

private fun HttpExchange.sendResponse(code: Int, answer: ByteArray) = run {
    sendResponseHeaders(code, answer.size.toLong())
    responseBody.use { it.write(answer) }
}

private fun HttpExchange.sendResponse(code: Int, answer: String) = sendResponse(code, answer.toByteArray())