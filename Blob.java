import java.io.*;

public class Blob {
    String fileContents;
    public Blob (String inputFile) throws IOException {
        fileContents = getFileContents(inputFile);
    }

    public String getFileContents(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String fileContents = "";
        while (reader.ready()) {
            fileContents += reader.readLine();
            if (reader.ready()) {
                fileContents += "/n";
            }
        }
        reader.close();
        return fileContents;
    }

    //TODO figure this out, stackoverflow has one but not sure if thats allowed...
    public String hashContents() {
        return "";
    }
}