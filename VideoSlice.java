/**obtain videoslice from txt
 * one line contains a slice
 * format is below:
 * eg : filename starttime endtime
* */

public class VideoSlice {
    private String filename;
    private int starttime;
    private int endtime;

    public String getFilename(){
        return filename;
    }

    public int getStarttime(){
        return starttime;
    }

    public int getEndtime(){
        return endtime;
    }


    public VideoSlice(String file, String stime, String etime){
        filename = file;
        starttime = stringToSeconds(stime);
        endtime = stringToSeconds(etime);
    }

    public int stringToSeconds(String s){
        String[] time = s.split(":");
        int seconds = Integer.parseInt(time[0]) * 3600 + Integer.parseInt(time[1]) * 60 + Integer.parseInt(time[2]);
        return seconds;
    }


    public static void main(String[] args) {
        VideoSlice video = new VideoSlice("movie.mp4","00:00:01","00:11:11");

        System.out.println(video.filename);
        System.out.println(video.starttime);
        System.out.println(video.endtime);
    }

}
