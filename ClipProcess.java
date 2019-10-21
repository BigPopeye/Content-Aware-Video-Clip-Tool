import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class ClipProcess {
    public static class TargetClip {
        private String startTime; // eg:00:00:22
        private String endTime;
        public int getStartTimeBySecond(){
            return stringToSeconds(startTime);
        }
        public int getEndTimeBySecond(){
            return stringToSeconds(endTime);
        }
        public TargetClip(String startTime, String endTime){
            this.startTime = startTime;
            this.endTime = endTime;
        }
        private int stringToSeconds(String s){
            String[] time = s.split(":");
            return Integer.parseInt(time[0]) * 3600 + Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);
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
    }

    public static List<TargetClip> getTargetClip(String clipFilePath){
        String content = readFile(clipFilePath);
        List<SourceVideoClip> listSourceClips = parseSubtitleContent(content);
        return transferSource(listSourceClips);
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
        String[] lists = content.split("\\n\\n"); // select each subtitle by new line
        for(String clip : lists){
            String[] lines = clip.split("\\n");
            if(lines.length != 3){
                throw new RuntimeException("Invalid clip format"+clip);
            }
            int seq = Integer.valueOf(lines[0]);
            String sourceTimes = lines[1];
            String subtitle = lines[2];
            String[] clipTime = sourceTimes.split("-->");  // parse time : 00:00:22,957 --> 00:00:26,308
            String startClipTime = clipTime[0].split(",")[0].trim();
            String endClipTime = clipTime[1].split(",")[0].trim();
            resListClip.add(new SourceVideoClip(seq,startClipTime,endClipTime,subtitle));
        }
        return resListClip;
    }

    private static List<TargetClip> transferSource(List<SourceVideoClip> listClips){
        List<TargetClip> resTargetClip = new LinkedList<>();
        int startSeq = listClips.get(0).seq;
        String startTime = listClips.get(0).startTime;
        for(int i = 1; i < listClips.size();i++){
            int currSeq = listClips.get(i).seq;
            if(startSeq == currSeq-1){
                startSeq = currSeq;
            }else{
                resTargetClip.add(new TargetClip(startTime,listClips.get(i-1).endTime));
                startTime = listClips.get(i).startTime;
                startSeq = listClips.get(i).seq;
            }
        }
        // don't forget the last clip
        resTargetClip.add(new TargetClip(startTime,listClips.get(listClips.size()-1).endTime));
        return resTargetClip;
    }

}
