import cn.uexpo.md_cloud.MdLog
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class HttpClientUtil {

    companion object {
        fun post(url: String, jsonStr: String): String? {
            return httpRequest(url, "POST", jsonStr)
        }

        fun get(url: String, params: Map<String, String>): String? {
            val fullUrl = buildUrlWithParams(url, params) ?: return null
            return httpRequest(fullUrl.toString(), "GET", null)
        }

        private fun httpRequest(url: String, method: String, requestBody: String?): String? {

            val connection = URL(url).openConnection() as HttpURLConnection

            if (method == "GET") {
                MdLog.debug("GET请求:${url}")
            } else {
                MdLog.debug("POST请求:${url},数据:${requestBody}")
            }

            connection.requestMethod = method
            connection.setRequestProperty("Accept-Charset", "UTF-8")
            connection.setRequestProperty("Content-Type", "application/json; utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.connectTimeout = 5 * 1000 // 连接超时时间为5秒
            connection.readTimeout = 60 * 1000// 读取超时时间为10秒
            connection.doOutput = true

            try {
                if (requestBody != null) {
                    val os: OutputStream = connection.outputStream
                    val input: ByteArray = requestBody.toByteArray(charset("utf-8"))
                    os.write(input, 0, input.size)
                }
            } catch (e: Exception) {
                MdLog.error(e)
                return null
            }

            val responseCode = connection.responseCode
            val response: StringBuilder = StringBuilder()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8"))

                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
            } else {
                return null
            }

            connection.disconnect()

            return response.toString()
        }

        private fun buildUrlWithParams(url: String, params: Map<String, String>): URL? {
            val urlBuilder = StringBuilder(url)
            if (params.isNotEmpty()) {
                urlBuilder.append('?')
                for ((key, value) in params) {
                    try {
                        urlBuilder
                            .append(URLEncoder.encode(key, "UTF-8"))
                            .append('=')
                            .append(URLEncoder.encode(value, "UTF-8"))
                            .append('&')
                    } catch (e: UnsupportedEncodingException) {
                        MdLog.error(e)
                        return null
                    }
                }
                urlBuilder.deleteCharAt(urlBuilder.length - 1) // Remove the last '&'
            }

            return URL(urlBuilder.toString())
        }
    }
}
