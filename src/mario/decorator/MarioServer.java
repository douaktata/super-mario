package mario.decorator;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Serveur HTTP léger embarqué pour l'interface web 3D de Mario.
 * Fournit une API REST pour l'état du pattern Décorateur et un serveur de fichiers statiques.
 */
public class MarioServer {

    /**
     * Port d'écoute par défaut du serveur.
     */
    private static final int DEFAULT_PORT = 8080;

    /**
     * Personnage courant décoré (Thread-safe).
     */
    private static Character current = new Mario();

    /**
     * Historique des décorateurs appliqués dans l'ordre (Thread-safe).
     */
    private static final List<String> stack = new ArrayList<>();

    /**
     * Constructeur par défaut.
     */
    public MarioServer() {
        // Constructeur pour éviter tout warning javadoc
    }

    /**
     * Démarre le serveur HTTP Mario.
     * Tente le port 8080, et bascule dynamiquement si le port est déjà occupé.
     *
     * @param args Arguments de ligne de commande (optionnel: port personnalisé).
     * @throws IOException Si une erreur d'E/S survient lors du démarrage du serveur.
     */
    public static void main(String[] args) throws IOException {
        int initialPort = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                initialPort = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                // Port non valide, utilisation du port par défaut
            }
        }

        HttpServer server = null;
        int activePort = initialPort;

        for (int p = activePort; p < activePort + 10; p++) {
            try {
                server = HttpServer.create(new InetSocketAddress(p), 0);
                activePort = p;
                break;
            } catch (BindException e) {
                System.out.println("Note: Port " + p + " deja utilise, tentative sur le port " + (p + 1) + "...");
            }
        }

        if (server == null) {
            throw new IOException("Impossible de trouver un port libre entre " + initialPort + " et " + (initialPort + 10));
        }

        // Enregistrement des Handlers API et Statiques
        server.createContext("/api/state", new StateHandler());
        server.createContext("/api/add", new AddHandler());
        server.createContext("/api/undo", new UndoHandler());
        server.createContext("/api/reset", new ResetHandler());
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // Exécuteur par défaut
        server.start();

        System.out.println("==================================================");
        System.out.println("  Serveur Mario Decorator demarre sur HTTP " + activePort);
        System.out.println("  Accedez a : http://localhost:" + activePort);
        System.out.println("==================================================");

        openBrowser("http://localhost:" + activePort);
    }

    /**
     * Ajoute un power-up de manière thread-safe.
     *
     * @param powerup Le nom du power-up (mushroom, fire, cape, star).
     */
    private static synchronized void addPowerUp(String powerup) {
        if (powerup == null) {
            return;
        }
        switch (powerup.toLowerCase()) {
            case "mushroom":
                current = new SuperMushroomDecorator(current);
                stack.add("mushroom");
                break;
            case "fire":
                current = new FireFlowerDecorator(current);
                stack.add("fire");
                break;
            case "cape":
                current = new CapeFeatherDecorator(current);
                stack.add("cape");
                break;
            case "star":
                current = new StarDecorator(current);
                stack.add("star");
                break;
            default:
                // Type inconnu ignoré
                break;
        }
    }

    /**
     * Annule et retire le dernier power-up appliqué.
     */
    private static synchronized void undoPowerUp() {
        if (!stack.isEmpty()) {
            stack.remove(stack.size() - 1);
            rebuildCharacterFromStack();
        }
    }

    /**
     * Réinitialise l'état du personnage au Mario de base.
     */
    private static synchronized void resetCharacter() {
        current = new Mario();
        stack.clear();
    }

    /**
     * Reconstruit la pile des décorateurs à partir de la liste `stack`.
     */
    private static void rebuildCharacterFromStack() {
        current = new Mario();
        for (String p : stack) {
            switch (p) {
                case "mushroom":
                    current = new SuperMushroomDecorator(current);
                    break;
                case "fire":
                    current = new FireFlowerDecorator(current);
                    break;
                case "cape":
                    current = new CapeFeatherDecorator(current);
                    break;
                case "star":
                    current = new StarDecorator(current);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Génère la représentation JSON complète de l'état actuel de manière thread-safe.
     *
     * @return Chaîne JSON.
     */
    private static synchronized String getStateJson() {
        StringBuilder json = new StringBuilder("{");
        json.append("\"description\":\"").append(escapeJson(current.getDescription())).append("\",");
        json.append("\"health\":").append(current.getHealth()).append(",");
        json.append("\"power\":").append(current.getPower()).append(",");
        json.append("\"speed\":").append(current.getSpeed()).append(",");
        json.append("\"stack\":[");
        for (int i = 0; i < stack.size(); i++) {
            json.append("\"").append(escapeJson(stack.get(i))).append("\"");
            if (i < stack.size() - 1) {
                json.append(",");
            }
        }
        json.append("],");
        json.append("\"javaExpression\":\"").append(escapeJson(buildJavaExpression())).append("\"");
        json.append("}");
        return json.toString();
    }

    /**
     * Construit l'expression d'instanciation Java correspondant au stack actuel.
     *
     * @return La chaîne représentative du code Java.
     */
    private static synchronized String buildJavaExpression() {
        String expr = "new Mario()";
        for (String p : stack) {
            switch (p) {
                case "mushroom":
                    expr = "new SuperMushroomDecorator(" + expr + ")";
                    break;
                case "fire":
                    expr = "new FireFlowerDecorator(" + expr + ")";
                    break;
                case "cape":
                    expr = "new CapeFeatherDecorator(" + expr + ")";
                    break;
                case "star":
                    expr = "new StarDecorator(" + expr + ")";
                    break;
                default:
                    break;
            }
        }
        return expr;
    }

    /**
     * Échappe les caractères spéciaux pour la conformité JSON.
     *
     * @param input Chaîne d'entrée.
     * @return Chaîne nettoyée.
     */
    private static String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Tente d'ouvrir automatiquement le navigateur web vers l'URL indiquée.
     *
     * @param url URL à ouvrir.
     */
    private static void openBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
                } else if (os.contains("mac")) {
                    Runtime.getRuntime().exec("open " + url);
                } else if (os.contains("nix") || os.contains("nux")) {
                    Runtime.getRuntime().exec("xdg-open " + url);
                }
            }
        } catch (Exception e) {
            System.out.println("Note: Impossible d'ouvrir automatiquement le navigateur: " + e.getMessage());
        }
    }

    /**
     * Handler API GET /api/state
     */
    static class StateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            byte[] response = getStateJson().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(method) ? -1 : response.length);
            if ("GET".equalsIgnoreCase(method)) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }
    }

    /**
     * Handler API GET /api/add?p=mushroom|fire|cape|star
     */
    static class AddHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            String query = exchange.getRequestURI().getQuery();
            String powerup = null;
            if (query != null) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && "p".equalsIgnoreCase(pair[0])) {
                        powerup = pair[1];
                        break;
                    }
                }
            }
            if (powerup != null) {
                addPowerUp(powerup);
            }
            byte[] response = getStateJson().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(method) ? -1 : response.length);
            if ("GET".equalsIgnoreCase(method)) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }
    }

    /**
     * Handler API GET /api/undo
     */
    static class UndoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            undoPowerUp();
            byte[] response = getStateJson().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(method) ? -1 : response.length);
            if ("GET".equalsIgnoreCase(method)) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }
    }

    /**
     * Handler API GET /api/reset
     */
    static class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }
            resetCharacter();
            byte[] response = getStateJson().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(method) ? -1 : response.length);
            if ("GET".equalsIgnoreCase(method)) {
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
            }
        }
    }

    /**
     * Handler de fichiers statiques servant le contenu du dossier `web/`.
     * Intègre une protection contre les attaques de traversée de répertoire (Path Traversal).
     */
    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
                exchange.sendResponseHeaders(405, -1);
                return;
            }

            String uriPath = exchange.getRequestURI().getPath();
            if (uriPath == null || uriPath.equals("/") || uriPath.isEmpty()) {
                uriPath = "/index.html";
            }

            Path webDir = Paths.get("web").toAbsolutePath().normalize();
            Path requestedFile = webDir.resolve(uriPath.substring(1)).toAbsolutePath().normalize();

            // Securité: Vérifier que le fichier demandé se trouve bien sous le dossier web/
            if (!requestedFile.startsWith(webDir)) {
                String error = "403 Forbidden: Acces non autorise";
                exchange.sendResponseHeaders(403, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            File file = requestedFile.toFile();
            if (!file.exists() || file.isDirectory()) {
                String error = "404 Not Found: Fichier introuvable";
                exchange.sendResponseHeaders(404, error.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(error.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String contentType = getMimeType(file.getName());
            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, "HEAD".equalsIgnoreCase(method) ? -1 : file.length());

            if ("GET".equalsIgnoreCase(method)) {
                try (FileInputStream fis = new FileInputStream(file);
                     OutputStream os = exchange.getResponseBody()) {
                    byte[] buffer = new byte[8192];
                    int count;
                    while ((count = fis.read(buffer)) != -1) {
                        os.write(buffer, 0, count);
                    }
                    os.flush();
                }
            }
        }

        /**
         * Détermine le type MIME en fonction de l'extension du fichier.
         *
         * @param filename Nom du fichier.
         * @return Type MIME approprié.
         */
        private String getMimeType(String filename) {
            String lower = filename.toLowerCase();
            if (lower.endsWith(".html") || lower.endsWith(".htm")) {
                return "text/html; charset=utf-8";
            } else if (lower.endsWith(".css")) {
                return "text/css; charset=utf-8";
            } else if (lower.endsWith(".js")) {
                return "application/javascript; charset=utf-8";
            } else if (lower.endsWith(".json")) {
                return "application/json; charset=utf-8";
            } else if (lower.endsWith(".gltf")) {
                return "model/gltf+json";
            } else if (lower.endsWith(".bin")) {
                return "application/octet-stream";
            } else if (lower.endsWith(".png")) {
                return "image/png";
            } else if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (lower.endsWith(".svg")) {
                return "image/svg+xml";
            }
            return "application/octet-stream";
        }
    }
}
