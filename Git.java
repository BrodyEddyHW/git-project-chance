import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
        deleteRepo();
    }
    public static void initRepo () throws IOException {
        if (new File("git" + File.separator + "objects").exists() && new File("git", "index").exists()) {
            System.out.println("Git Repository already exists");
        }
        else {
            new File("git" + File.separator + "objects").mkdirs();
            File index = new File("git" + File.separator + "index");
            index.createNewFile();  // Create the index file
            System.out.println("Git Repository initialized");
        }
    }

    public static void deleteRepo() {
        File objects = new File("git" + File.separator + "objects");
        File index = new File("git", "index");
        if (objects.exists() && index.exists()) {
            deleteDirectory(objects);
            index.delete();
            System.out.println("Git Repository deleted");
        } 
        else {
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
                }
            }
        }
        directory.delete();
    }
}