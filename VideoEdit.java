/*Easiest demo to show how to use java and ffmpeg to merge videos
TBD :
1. different format videos sources
2. clip operation improvement(this version just write down)
3. can user set output file name (this version is set by default)
* */

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class VideoEdit {

    public static final String ffmpegPath = "/usr/local/Cellar/ffmpeg/4.1.3_1/bin/ffmpeg";
//    public static String videoPath = "videos";

    /**read video slices that will be merged from file */
    public static VideoSlice[] readVideoSlice(String infoPath){
        In in = new In(infoPath);
        int numOfSlices = in.readInt();
        VideoSlice[] slices = new VideoSlice[numOfSlices];
        for(int i = 0;i < numOfSlices; i++){
            slices[i] = new VideoSlice(in.readString(),in.readString(),in.readString());
        }
        return slices;
    }

    /**convert java code into ffmpeg command
     * $ ffmpeg -i input.mp4 -i out_merged.mp4 -i input.mp4 -filter_complex \
     * "[0:v]trim=0:5,setpts=PTS-STARTPTS[0v];[0:a]atrim=0:5,asetpts=PTS-STARTPTS[0a];\
     * [1:v]trim=9:15,setpts=PTS-STARTPTS[1v];[1:a]atrim=9:15,asetpts=PTS-STARTPTS[1a];\
     * [2:v]trim=30:40,setpts=PTS-STARTPTS[2v];[2:a]atrim=30:40,asetpts=PTS-STARTPTS[2a];\
     * [0v][0a][1v][1a][2v][2a]concat=n=3:v=1:a=1[outv][outa]" -map "[outv]" -map "[outa]" outtest0.mp4 */
    public static List<String> convertor(VideoSlice[] videoSlices, String outVideoName){
        List<String> command = new ArrayList<>();
        int len = videoSlices.length;
        command.add(ffmpegPath);
        for(int i = 0; i < len; i++){
            command.add("-i");
            command.add(videoSlices[i].getFilename());
        }
        command.add("-filter_complex");

        // build main string
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < len; i++){
            sb.append("[");
            sb.append(i);
            sb.append(":v]trim=");
            sb.append(videoSlices[i].getStarttime());
            sb.append(":");
            sb.append(videoSlices[i].getEndtime());
            sb.append(",setpts=PTS-STARTPTS[");
            sb.append(i);
            sb.append("v];[");
            sb.append(i);
            sb.append(":a]atrim=");
            sb.append(videoSlices[i].getStarttime());
            sb.append(":");
            sb.append(videoSlices[i].getEndtime());
            sb.append(",asetpts=PTS-STARTPTS[");
            sb.append(i);
            sb.append("a];");
        }
        for(int i = 0; i< len; i++){
            sb.append("[");
            sb.append(i);
            sb.append("v][");
            sb.append(i);
            sb.append("a]");
        }
        sb.append("concat=n=");
        sb.append(len);
        sb.append(":v=1:a=1[outv][outa]");

        command.add(sb.toString());
        command.add("-map");
        command.add("[outv]");
        command.add("-map");
        command.add("[outa]");

        command.add(outVideoName);

        return command;
    }


    public void generateVideo(String infoPath, String inputFolderPath,String outVideoName){

        VideoEdit videoEdit = new VideoEdit();
        VideoSlice[] vs = videoEdit.readVideoSlice(infoPath);
        List<String> comm = videoEdit.convertor(vs,outVideoName);
        ProcessBuilder processBuilder = new ProcessBuilder();
        //processBuilder.directory(new File("/usr/local/Cellar/ffmpeg/4.1.3_1/bin"));
        //processBuilder.command("ffplay", "../../../../../../Users/olive/Documents/Coding/VideoSlice/ffmpeg/input.mp4");
        processBuilder.directory(new File(inputFolderPath));

        processBuilder.command(comm);

        try{
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (IOException e){
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }




}
