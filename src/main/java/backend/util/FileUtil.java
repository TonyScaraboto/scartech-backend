package backend.util;

import java.io.File;

public class FileUtil {
    
    public static File getPythonDirectory() {
        String basePath = System.getProperty("user.dir");
        File pythonDir = new File(basePath, "python");
        
        if (!pythonDir.exists()) {
            pythonDir = new File(basePath, "backend/python");
        }
        
        if (!pythonDir.exists()) {
            throw new RuntimeException("Diretório Python não encontrado em: " + basePath);
        }
        
        return pythonDir;
    }
    
    public static String getPythonScriptPath(String scriptName) {
        File pythonDir = getPythonDirectory();
        File scriptFile = new File(pythonDir, scriptName);
        
        if (!scriptFile.exists()) {
            throw new RuntimeException("Script Python não encontrado: " + scriptName);
        }
        
        return scriptFile.getAbsolutePath();
    }
}
