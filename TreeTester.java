import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TreeTester {

    public static void main(String[] args) {
        try {
            String root = "testRoot";
            setup(root);
            Tree tree = new Tree();
            tree.addDirectory(root);

        } catch (IOException e) {
            e.printStackTrace();
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
        newFile(subSubDir + "/file5.txt", "This is file 5 in subSubDir");
    }
    
    private static void newFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
    }
}
