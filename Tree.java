import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tree implements GitInterface {
    //Stages a file for the next commit.
    public void stage(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new IllegalArgumentException("File does not exist: " + filePath);
            }
            String blobHash = createBlob(file);
            writeToIndexFile("blob", file.getPath(), blobHash);
            File directory = file.getParentFile();
            while (directory != null) {
                String treeHash = addDirectory(directory.getPath());
                writeToIndexFile("tree", directory.getPath(), treeHash);
                directory = directory.getParentFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Method to create a commit with the given author and message
    public String commit(String author, String message) throws IOException {
        File directory = new File("git/objects/root");
        directory.mkdirs();
        String rootTreeHash = addDirectory("git/objects/root");
        String parentHash = readHead();
        String commitContent = createCommitContent(author, message, rootTreeHash, parentHash);
        String commitHash = hashContents(commitContent);
        File commitFile = new File("git/objects/" + commitHash);
        commitFile.getParentFile().mkdirs();
        try (Writer writer = new FileWriter(commitFile)) {
            writer.write(commitContent);
        }
        File headFile = new File("git/HEAD");
        try (Writer writer = new FileWriter(headFile)) {
            writer.write(commitHash);
        }
        directory.delete();
        return commitHash;
    }
    
    private String createCommitContent(String author, String message, String treeHash, String parentHash) {
        String commitContent = "";
        commitContent += "tree: " + treeHash + "\n";
        if (parentHash != null)
          commitContent += "parent: " + parentHash + "\n";
        else
        commitContent += "parent: \n";
        commitContent += "author: " + author + "\n";
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy");
        commitContent += "date: " + sdf.format(new Date()) + "\n";
        commitContent += "message: " + message + "\n";
        return commitContent;
    }

    //reads current head commit hash
    private String readHead() throws IOException {
        File headFile = new File("git/HEAD");
        if (!headFile.exists())
            return null;
        try (BufferedReader reader = new BufferedReader(new FileReader(headFile))) {
            return reader.readLine().trim();
        }
    }

    // Method to add a directory and recursively create blobs and trees
    public String addDirectory(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        // Ensure the directory exists
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory path: " + directoryPath);
        }

        // StringBuilder to accumulate the contents of the tree file
        StringBuilder treeContents = new StringBuilder();

        // Traverse the directory
        for (File file : directory.listFiles()) {
            if (file.isFile()) {
                // Create a blob for the file
                String fileHash = createBlob(file);
                treeContents.append("blob ").append(fileHash).append(" ").append(file.getName()).append("\n");
                // Add the blob entry to the index
                writeToIndexFile("blob", directoryPath + "/" + file.getName(), fileHash);
            } else if (file.isDirectory()) {
                // Recursively create a tree for the subdirectory
                String dirHash = addDirectory(file.getPath());
                treeContents.append("tree ").append(dirHash).append(" ").append(file.getName()).append("\n");
                // Add the tree entry to the index
                writeToIndexFile("tree", directoryPath + "/" + file.getName(), dirHash);
            }
        }

        // Hash the contents of the tree and create a tree object
        String treeHash = hashTreeContents(treeContents.toString());
        writeTreeToFile(treeHash, treeContents.toString());

        return treeHash;
    }

    // Method to create a Blob from a file
    private String createBlob(File file) throws IOException {
        // Read the file contents and generate the SHA1 hash
        String fileContents = getFileContents(file.getPath());
        String fileHash = hashContents(fileContents);

        // Write the file contents to the objects directory
        copyFileToObjects(fileHash, fileContents);

        return fileHash;
    }

    // Helper method to read file contents
    private String getFileContents(String inputFile) throws IOException {
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        }
        return fileContents.toString().trim();
    }

    // Part 1 requirement
    private void writeToIndexFile(String type, String path, String hash) throws IOException {
        File index = new File("git/index");
        try (FileWriter writer = new FileWriter(index, true)) {
            writer.write(type + " " + hash + " " + path + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to hash the tree contents using SHA1
    private String hashTreeContents(String treeContents) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(treeContents.getBytes(StandardCharsets.UTF_8));
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to write the tree contents to the objects directory
    private void writeTreeToFile(String treeHash, String treeContents) throws IOException {
        File treeFile = new File("git/objects/" + treeHash);
        treeFile.getParentFile().mkdirs(); // Ensure the directory exists
        try (Writer writer = new FileWriter(treeFile)) {
            writer.write(treeContents);
        }
    }

    // Helper method to write blob contents to the objects directory
    private void copyFileToObjects(String hash, String fileContents) throws IOException {
        File file = new File("git/objects/" + hash);
        file.getParentFile().mkdirs();  // Ensure the directory exists
        try (Writer writer = new FileWriter(file)) {
            writer.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to hash file contents using SHA1
    private String hashContents(String content) {
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(content.getBytes(StandardCharsets.UTF_8));
            return byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper method to convert bytes to hexadecimal (for SHA1)
    private static String byteToHex(final byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void checkout(String commitHash) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkout'");
    }
}