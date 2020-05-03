import java.io.*;

public class FileObject {
    int id;
    public String filepath;
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
        bw.close();
    }

    synchronized void commit(String message) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filepath, true));
        bw.write(message + "\n");
        bw.close();
    }

    public synchronized String getFileContents()
    {
        StringBuilder messageBuilder = new StringBuilder();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))){
            while ((line = br.readLine()) != null){
                messageBuilder.append(line).append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return messageBuilder.toString();
    }
}
