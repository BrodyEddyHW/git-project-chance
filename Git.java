import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Git {
    public static void initRepo () throws IOException {
        if (new File("git/objects").exists() && new File("git", "index").exists()) {
            System.out.println("Git Repository already exists");
        }
        else {
            new File("git/objects").mkdirs();
            File directory = new File ("git");
            File index = new File(directory, "index");
            FileWriter writer = new FileWriter(index);
        }
    }
    private String deleteRepo() {
        if ((new File("git/objects").exists() && new File("git", "index").exists())) {
            File index = new File("git", "index");
            File objects = new File("git/objects");
            File gitDirectory = new File("git");
            index.delete(); objects.delete(); gitDirectory.delete();
            return "Git Repository deleted";
        }
        return "Git Repository does not exist";
    }
}