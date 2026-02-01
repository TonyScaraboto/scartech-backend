package backend;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PythonRunner {
    public static String runPythonScript(String scriptPath, Map<String, Object> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(data);
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, json);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String runPythonScript(String scriptPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String runPythonScript(String scriptPath, String workingDirectory) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath);
            pb.directory(new File(workingDirectory));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
            process.waitFor();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
