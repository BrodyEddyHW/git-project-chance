import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GitTester {
    public static void main(String[] args) throws IOException {
        testInitRepo();
        testCreateBlob();
        Git.deleteRepo();
        new File("test.txt").delete();
    }

    public static void testInitRepo() throws IOException {
        Git.deleteRepo();
        Git.initRepo();
        File gitFolder = new File("git");
        File objectsFolder = new File("git" + File.separator + "objects");
        File index = new File("git" + File.separator + "index");
        if (gitFolder.exists() && objectsFolder.exists() && index.exists()) {
            System.out.println("initRepo() works");
        } 
        else {
            System.out.println("initRepo() doesn't work");
        }
    }

    public static void testCreateBlob() throws IOException {
        Git.initRepo();
        FileWriter writer = new FileWriter("test.txt");
        writer.append("hello world");
        writer.close();
        Blob blob = new Blob("test.txt");
        File blobFile = new File("git" + File.separator + "objects" + File.separator + blob.getHash());
        if (blobFile.exists()) {
            System.out.println("Blob works");
        } 
        else {
            System.out.println("Blob doesn't work");
        }
        String content = Blob.getFileContents("test.txt");
        if (content.equals("hello world")) {
            System.out.println("Blob content works");
        } 
        else {
            System.out.println("Blob content doesn't work");
        }
        new File("test.txt").delete();
    }
}
