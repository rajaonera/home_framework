package mg.framework.exception;

public class Error {
    public static String getError(String message) {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"fr\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Erreur</title>\n" +
               "    <style>\n" +
               "        body {\n" +
               "            font-family: Arial, sans-serif;\n" +
               "            background-color: #f8f8f8;\n" +
               "            margin: 0;\n" +
               "            padding: 20px;\n" +
               "            display: flex;\n" +
               "            justify-content: center;\n" +
               "            align-items: center;\n" +
               "            min-height: 100vh;\n" +
               "            color: #333;\n" +
               "        }\n" +
               "        .error-container {\n" +
               "            background: white;\n" +
               "            border-left: 5px solid #d9534f;\n" +
               "            border-radius: 5px;\n" +
               "            box-shadow: 0 2px 10px rgba(0,0,0,0.1);\n" +
               "            width: 100%;\n" +
               "            max-width: 500px;\n" +
               "            padding: 25px;\n" +
               "            text-align: center;\n" +
               "        }\n" +
               "        h1 {\n" +
               "            color: #d9534f;\n" +
               "            margin-top: 0;\n" +
               "        }\n" +
               "        .error-actions {\n" +
               "            display: flex;\n" +
               "            gap: 10px;\n" +
               "            margin-top: 20px;\n" +
               "        }\n" +
               "        button {\n" +
               "            flex: 1;\n" +
               "            padding: 10px;\n" +
               "            border: none;\n" +
               "            border-radius: 4px;\n" +
               "            cursor: pointer;\n" +
               "            font-size: 14px;\n" +
               "        }\n" +
               "        .back-btn {\n" +
               "            background: #e0e0e0;\n" +
               "        }\n" +
               "        .retry-btn {\n" +
               "            background: #d9534f;\n" +
               "            color: white;\n" +
               "        }\n" +
               "        .error-details {\n" +
               "            margin-top: 20px;\n" +
               "            padding: 10px;\n" +
               "            background: #f5f5f5;\n" +
               "            border-radius: 4px;\n" +
               "            font-family: monospace;\n" +
               "            font-size: 12px;\n" +
               "            text-align: left;\n" +
               "            display: none;\n" +
               "        }\n" +
               "        .toggle-details {\n" +
               "            color: #666;\n" +
               "            font-size: 12px;\n" +
               "            cursor: pointer;\n" +
               "            margin-top: 15px;\n" +
               "            display: inline-block;\n" +
               "        }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div class=\"error-container\">\n" +
               "        <h1>ERREUR</h1>\n" +
               "        <p>Une erreur s'est produite lors du traitement de votre requête.</p>\n" +
               "        \n" +
               "        <div class=\"error-actions\">\n" +
               "            <button class=\"back-btn\" onclick=\"window.history.back()\">Retour</button>\n" +
               "            <button class=\"retry-btn\" onclick=\"window.location.reload()\">Réessayer</button>\n" +
               "        </div>\n" +
               "        \n" +
               "        <span class=\"toggle-details\" onclick=\"toggleDetails()\">Détails techniques ▼</span>\n" +
               "        <div class=\"error-details\" id=\"errorDetails\">\n" +
               "            <p><b>Message :</b> " + escapeHtml(message) + "</p>\n" +
               "            <p><b>Heure :</b> <span id=\"timestamp\"></span></p>\n" +
               "        </div>\n" +
               "    </div>\n" +
               "\n" +
               "    <script>\n" +
               "        // Affiche l'horodatage actuel\n" +
               "        document.getElementById('timestamp').textContent = new Date().toLocaleString();\n" +
               "        \n" +
               "        // Basculer l'affichage des détails\n" +
               "        function toggleDetails() {\n" +
               "            const details = document.getElementById('errorDetails');\n" +
               "            const isHidden = details.style.display === 'none';\n" +
               "            details.style.display = isHidden ? 'block' : 'none';\n" +
               "            document.querySelector('.toggle-details').textContent = \n" +
               "                isHidden ? 'Détails techniques ▲' : 'Détails techniques ▼';\n" +
               "        }\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}