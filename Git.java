import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
        deleteRepo();
    }

    public static void initRepo() throws IOException {
        if (new File("git/objects").exists() && new File("git", "index").exists()) {
            System.out.println("Git Repository already exists");
        } else {
            new File("git/objects").mkdirs();  // Create the git/objects directory
            File directory = new File("git");
            File index = new File(directory, "index");
            index.createNewFile();  // Create the index file
            System.out.println("Git Repository initialized");
        }
    }

    public static void deleteRepo() {
        File objects = new File("git/objects");
        File index = new File("git", "index");
        File gitDirectory = new File("git");

        if (objects.exists() && index.exists()) {
            index.delete();  // Delete the index file
            deleteDirectory(objects);
            gitDirectory.delete();  
            System.out.println("Git Repository deleted");
        } else {
            System.out.println("Git Repository does not exist");
        }
    }

    // deletes a directory and the contents inside using recursion
    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) { 
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); 
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}