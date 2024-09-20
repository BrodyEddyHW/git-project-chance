import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
    }
    public static void initRepo () throws IOException {
        if (new File("git/objects").exists() && new File("git", "index").exists()) {
            System.out.println("Git Repository already exists");
        }
        else {
            new File("git/objects").mkdirs();
            File directory = new File ("git");
            File index = new File(directory, "index");
            index.createNewFile();
        }
    }
    private static void deleteRepo() {
        if ((new File("git/objects").exists() && new File("git", "index").exists())) {
            File index = new File("git", "index");
            File objects = new File("git/objects");
            File gitDirectory = new File("git");
            index.delete(); objects.delete(); gitDirectory.delete();
            System.out.println("Git Repository deleted");
        }
        System.out.println("Git Repository does not exist");
    }
}