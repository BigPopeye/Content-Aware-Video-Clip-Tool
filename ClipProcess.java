import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ClipProcess {
    public static class VideoClip{
        String startTime; // eg:00:00:22
        String endTime;
        public VideoClip(String startTime,String endTime){
            this.startTime = startTime;
            this.endTime = endTime;
        }
        public String toString(){
            return String.format("startTime: %s , endTime: %s",startTime,endTime);
        }
    }

    private static class SourceVideoClip{
        int seq;
        String startTime; // eg: 00:00:22
        String endTime;
        String content;

        public SourceVideoClip(int seq, String startTime, String endTime, String content) {
            this.seq = seq;
            this.startTime = startTime;
            this.endTime = endTime;
            this.content = content;
        }
        public String toString(){
            return String.format("seq: %d , startTime: %s , endTime: %s , content: %s",seq,startTime,endTime,content);
        }
    }

    public static List<VideoClip> getTargetClip(String clipFilePath){
        String content = readFile(clipFilePath);
        //System.out.println(content);
        List<SourceVideoClip> listSourceClips = parseSubtitleContent(content);
//        System.out.println(listSourceClips);
//        System.out.println(listSourceClips.size());
        List<VideoClip> listTargetClips = transferSource(listSourceClips);
        System.out.println(listTargetClips);
        return listTargetClips;
    }

    private static String readFile(String filePath){
        try {
            return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("file read failed :"+filePath,e);
        }
    }
    private static List<SourceVideoClip> parseSubtitleContent(String content){
        List<SourceVideoClip> resListClip = new LinkedList<>();
        String[] lists = content.split("\\n\\n");
        for(String clip : lists){
            String[] lines = clip.split("\\n");
            if(lines.length != 3){
                throw new RuntimeException("Invalid clip format"+clip);
            }
            int seq = Integer.valueOf(lines[0]);
            String sourceTimes = lines[1];
            String subtitle = lines[2];
            String[] clipTime = sourceTimes.split("-->");
            String startClipTime = clipTime[0].split(",")[0].trim();
            String endClipTime = clipTime[1].split(",")[0].trim();
            resListClip.add(new SourceVideoClip(seq,startClipTime,endClipTime,subtitle));
        }
        return resListClip;
    }
    private static List<VideoClip> transferSource(List<SourceVideoClip> listClips){
        List<VideoClip> resVideoClip = new LinkedList<>();
        int startSeq = listClips.get(0).seq;
        String startTime = listClips.get(0).startTime;
        for(int i = 1; i < listClips.size();i++){
            int currSeq = listClips.get(i).seq;
            if(startSeq == currSeq-1){
                startSeq = currSeq;
            }else{
                resVideoClip.add(new VideoClip(startTime,listClips.get(i-1).endTime));
                startTime = listClips.get(i).startTime;
                startSeq = listClips.get(i).seq;
            }
        }
        resVideoClip.add(new VideoClip(startTime,listClips.get(listClips.size()-1).endTime));
        return resVideoClip;
    }

    public static void main(String[] args) {
        String clipFilePath = "/Users/olive/Documents/GitHub/Projects/Video/res/cut.txt";
        getTargetClip(clipFilePath);
        List<VideoClip> expectClip= new LinkedList<>();
        expectClip.add(new VideoClip("00:00:22","00:00:36"));
        expectClip.add(new VideoClip("00:00:55","00:01:09"));
    }
}
