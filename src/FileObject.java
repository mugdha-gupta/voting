import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileObject {
    int id;
    String filepath;
    int serverId;

    public FileObject(int id, int serverId) throws IOException {
        this.id = id;
        this.serverId = serverId;
        setFileWriter();
    }

    synchronized private void setFileWriter() throws IOException {
        //each server will write to its own copy of the file
        filepath = "/home/012/m/mx/mxg167030/voting/server" + serverId + "/" + "f" + id + ".txt";
        BufferedWriter bw = new BufferedWriter(new FileWriter(filepath));
        System.out.println("file created");
        bw.close();
    }

    synchronized void commit(String message) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true));
        bw.write(message + "\n");
        System.out.println("file object wrote to file");
        bw.close();
    }
}
