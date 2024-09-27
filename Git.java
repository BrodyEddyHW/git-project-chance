import java.io.File;
import java.io.IOException;

public class Git {
    public static void main(String[] args) throws IOException {
        initRepo();
    }
    public static void initRepo () throws IOException {
        if (new File("git" + File.separator + "objects").exists() && new File("git", "index").exists()) {
            System.out.println("Git Repository already exists");
        }
        else {
            new File("git" + File.separator + "objects").mkdirs();
            File directory = new File ("git");
            File index = new File(directory, "index");
            index.createNewFile();
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

    public static void deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    deleteDirectory(files[i]);
                }
            }
        }
        directory.delete();
    }
}