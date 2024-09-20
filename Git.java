import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
    }
    public static void initRepo () throws IOException {
        if (new File("git/objects").exists()) {
            System.out.println("Repository already exists.");
        }
        else {
            new File("git/objects").mkdirs();
            File directory = new File ("git");
            File index = new File(directory, "index");
            Writer writer = new FileWriter(index);
            
        }
    }
}