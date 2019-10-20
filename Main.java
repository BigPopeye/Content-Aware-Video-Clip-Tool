import java.io.PrintWriter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String path = "/Users/olive/Documents/GitHub/Projects/Content-Aware-Video-Clip-Tool/res/";
        String clipPath = path+"cut.txt";
        String infoPath = path+"info.txt";

        List<ClipProcess.VideoClip> listVideoClip = ClipProcess.getTargetClip(clipPath);
        String videoName = "source.mp4";
        String outputVideoName = "output.mp4";
        writeInfoFile(infoPath,listVideoClip,videoName);
        new VideoEdit().generateVideo(infoPath,path,outputVideoName);
    }

    private static void writeInfoFile(String infoPath, List<ClipProcess.VideoClip> listVideoClip, String videoName){

//        2
//        input.mp4 00:00:05 00:00:15
//        input.mp4 00:01:00 00:01:10

        try{
            PrintWriter out = new PrintWriter(infoPath);
            out.println(listVideoClip.size());
            for(ClipProcess.VideoClip videoClip:listVideoClip){
                out.println(videoName+" "+videoClip.startTime+" "+videoClip.endTime);
            }
            out.close();
        }catch (Exception e){
            throw new RuntimeException("Write File error!"+infoPath,e);
        }

    }
}
