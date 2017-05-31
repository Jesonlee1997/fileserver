package me.jesonlee.jjfsserver.httpserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by JesonLee
 * on 2017/5/29.
 */
public class PathUtil {
    public static String getRelativePath(String dirPath) {
        int index = dirPath.lastIndexOf(HttpStaticFileServer.fileRoot) + HttpStaticFileServer.fileRoot.length();
        String path = dirPath.substring(index);
        if (path.length() == 0) {
            return "/";
        }
        path = path.replace('\\', '/');
        return path;
    }

    static String sanitizeUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new Error(e);
        }

        if (uri.isEmpty() || uri.charAt(0) != '/') {
            return null;
        }

        // Convert file separators.
        uri = uri.replace('/', File.separatorChar);

        // Simplistic dumb security check.
        // You will have to do something serious in the production environment.
        if (uri.contains(File.separator + '.') ||
                uri.contains('.' + File.separator) ||
                uri.charAt(0) == '.' || uri.charAt(uri.length() - 1) == '.' ||
                HttpStaticFileServerHandler.INSECURE_URI.matcher(uri).matches()) {
            return null;
        }

        // Convert to absolute path.
        return HttpStaticFileServer.fileRoot + File.separator + uri;
    }
}
