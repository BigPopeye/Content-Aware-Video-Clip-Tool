import java.io.PrintWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String path = "/Users/olive/Documents/GitHub/Projects/Content-Aware-Video-Clip-Tool/res/";
        String clipPath = path+"cut.txt";
        List<ClipProcess.TargetClip> listTargetClip = ClipProcess.getTargetClip(clipPath);

        String videoName = "source.mp4";
        String outputVideoName = "output.mp4";

        new VideoEdit().generateVideo(listTargetClip,path,videoName,outputVideoName);
    }

}
