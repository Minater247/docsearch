import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import java.util.Collections;

class FileHelpers {
    static List<File> getFiles(Path start) throws IOException {
        File f = start.toFile();
        List<File> result = new ArrayList<>();
        if (f.isDirectory()) {
            File[] paths = f.listFiles();
            for (File subFile : paths) {
                result.addAll(getFiles(subFile.toPath()));
            }
        } else {
            result.add(start.toFile());
        }
        return result;
    }

    static String readFile(File f) throws IOException {
        return new String(Files.readAllBytes(f.toPath()));
    }
}

class Handler implements URLHandler {
    Path base;

    Handler(String directory) throws IOException {
        this.base = Paths.get(directory);
    }

    public String handleRequest(URI url) throws IOException {
        List<File> paths = FileHelpers.getFiles(this.base);
        if (url.getPath().equals("/")) {
            return String.format("There are %d total files to search.", paths.size());
        } else if (url.getPath().equals("/search")) {
            if (url.getQuery().contains("&")) {
                String[] parameters = url.getQuery().split("&");
                if (parameters[0].split("=")[0].equals("title")) {
                    String temp = parameters[0];
                    parameters[0] = parameters[1];
                    parameters[1] = temp;
                }
                if (!parameters[0].split("=")[0].equals("q")) {
                    return "Invalid query parameter " + parameters[0].split("=")[0];
                } else if (!parameters[1].split("=")[0].equals("title")) {
                    return "Invalid query parameter " + parameters[1].split("=")[0];
                }
                String result = "";
                List<String> foundPaths = new ArrayList<>();
                for (File f : paths) {
                    if (FileHelpers.readFile(f).contains(parameters[0].split("=")[1])
                            && f.getName().contains(parameters[1].split("=")[1])) {
                        foundPaths.add(f.toString());
                    }
                }
                Collections.sort(foundPaths);
                result = String.join("\n", foundPaths);
                return String.format("Found %d paths:\n%s", foundPaths.size(), result);
            } else {
                String[] parameters = url.getQuery().split("=");
                if (parameters[0].equals("q")) {
                    String result = "";
                    List<String> foundPaths = new ArrayList<>();
                    for (File f : paths) {
                        if (FileHelpers.readFile(f).contains(parameters[1])) {
                            foundPaths.add(f.toString());
                        }
                    }
                    Collections.sort(foundPaths);
                    result = String.join("\n", foundPaths);
                    return String.format("Found %d paths:\n%s", foundPaths.size(), result);
                } else if (parameters[0].equals("title")) {
                    String result = "";
                    List<String> foundPaths = new ArrayList<>();
                    for (File f : paths) {
                        if (f.getName().contains(parameters[1])) {
                            foundPaths.add(f.toString());
                        }
                    }
                    Collections.sort(foundPaths);
                    result = String.join("\n", foundPaths);
                    return String.format("Found %d paths:\n%s", foundPaths.size(), result);
                } else {
                    return "Invalid query parameter " + parameters[0];
                }
            }
        } else {
            return "Don't know how to handle that path!";
        }
    }
}

class DocSearchServer {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println(
                    "Missing port number or directory! The first argument should be the port number (Try any number between 1024 to 49151) and the second argument should be the path of the directory");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler(args[1]));
    }
}