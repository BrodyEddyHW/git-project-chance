import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FinalTester {
    public static void main(String[] args) throws IOException {
        testInitRepo();
        testCreateBlob();
        new File("test.txt").delete();

        String root = "testRoot";
        setup(root);
        Tree tree = new Tree();
        tree.addDirectory(root);

        System.out.println("\n");
        String commitHash = tree.commit("Chance", "My first commit. Yipee.");
        System.out.println("Commit created with hash: " + commitHash);

        String commitHash2 = tree.commit("Chance", "Second commit");
        System.out.println("Commit created with hash: " + commitHash2);
    }

    public static void testInitRepo() throws IOException {
        Git.deleteRepo();
        Git.initRepo();
        File gitFolder = new File("git");
        gitFolder.mkdir();
        File objectsFolder = new File("git" + File.separator + "objects");
        objectsFolder.mkdir();
        File index = new File("git" + File.separator + "index");
        index.createNewFile();
        System.out.println("\nTest initRepo():");
        if (gitFolder.exists() && objectsFolder.exists() && index.exists()) {
            System.out.println("\t-initRepo() works");
        } 
        else {
            System.out.println("\t-initRepo() doesn't work");
        }
    }

    public static void testCreateBlob() throws IOException {
        FileWriter writer = new FileWriter("test.txt");
        writer.append("hello world");
        writer.close();
        Blob blob = new Blob("test.txt");
        File blobFile = new File("git" + File.separator + "objects" + File.separator + blob.getHash());
        System.out.println("\nTest blob generation:");
        if (blobFile.exists()) {
            System.out.println("\t-Blob works");
        } 
        else {
            System.out.println("\t-Blob doesn't work");
        }
        String content = Blob.getFileContents("test.txt");
        System.out.println("\nTest blob content:");
        if (content.equals("hello world")) {
            System.out.println("\t-Blob content works");
        } 
        else {
            System.out.println("\t-Blob content doesn't work");
        }
    }

    private static void setup(String rootPath) throws IOException {
        File root = new File(rootPath);
        if (!root.exists()) {
            root.mkdirs();
        }
        newFile(rootPath + "/file1.txt", "file1");
        newFile(rootPath + "/file2.txt", "file2");
        String insideDirectory = rootPath + "/subDir";
        File subDirectory = new File(insideDirectory);
        if (!subDirectory.exists()) {
            subDirectory.mkdirs();
        }
        newFile(insideDirectory + "/file3.txt", "file3");
        newFile(insideDirectory + "/file4.txt", "file4");
        String subSubDir = insideDirectory + "/subSubDir";
        File subSubDirectory = new File(subSubDir);
        if (!subSubDirectory.exists()) {
            subSubDirectory.mkdirs();
        }
        newFile(subSubDir + "/file5.txt", "file 5");
    }

    private static void newFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
