import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Blob {
    String fileName, fileContents, hash;

    public Blob (String inputFile) throws IOException {
        fileName = inputFile;
        fileContents = getFileContents(inputFile);
        hash = hashContents();
        copyFileToObjects();
        writeToIndexFile();
    }

    public static String getFileContents(String inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        String fileContents = "";
        while (reader.ready()) {
            fileContents += reader.readLine();
            if (reader.ready()) {
                fileContents += "/n";
            }
        }
        reader.close();
        return fileContents;
    }

    public void copyFileToObjects() throws IOException {
        File file = new File("git" + File.separator + "objects" + File.separator + hash.toString());
        try (Writer writer = new FileWriter(file)) {
            writer.write(fileContents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToIndexFile() throws IOException {
        File index = new File("index");
        try (FileWriter writer = new FileWriter(index, true)) {
            writer.write(hash.toString() + " " + fileName);
            writer.write("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public String hashContents() {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(fileContents.getBytes("UTF-8"));
            sha1 = byteToHex(crypt.digest());
        }
        catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e){
        e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public String getHash() {
        return hash;
    }
}