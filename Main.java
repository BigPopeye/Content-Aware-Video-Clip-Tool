import java.io.PrintWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String path = "/Users/olive/Documents/GitHub/Projects/Content-Aware-Video-Clip-Tool/res/";
        String clipPath = path+"cut.srt";
        List<ClipProcess.TargetClip> listTargetClip = ClipProcess.getTargetClip(clipPath);

        String videoName = "Friends.S08E01.rmvb";
        String outputVideoName = "outFriends.S08E01.mp4";

        new VideoEdit().generateVideo(listTargetClip,path,videoName,outputVideoName);
    }

}
