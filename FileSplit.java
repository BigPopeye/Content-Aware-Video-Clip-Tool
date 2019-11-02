import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;

public class FileSplit {

    public static class FileSlice {
        private String sliceSeq; //1,2,..
        private String content;
        public FileSlice(String sliceSeq, String content){
            this.sliceSeq = sliceSeq;
            this.content = content;
        }
    }
    public class AllFileSlice{
        public String fileName;
        public List<FileSlice> fileSliceList;
        public AllFileSlice(String fileName, List<FileSlice> fileSlices){
            this.fileName = fileName;
            this.fileSliceList = fileSlices;
        }
    }

    //将指定目录下的所有文件读入
    //依次将文件按照段落分割，并将分割后的文件写入新的文件并重命名
    public List<AllFileSlice> fileSplit(String dataDir) throws Exception{
        File[] files = new File(dataDir).listFiles();
        // TBD : 隐藏文件的处理 如 .DS_Store
        List<AllFileSlice> allFileSlices = new LinkedList<>();
        for(File file : files){
            String fileString = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            String[] rawSlices = fileString.split("\\n\\n"); // select each paragragh by new line
            List<FileSlice> fileSlices = new LinkedList<>();
            for(String slice : rawSlices){
                String seq =new BufferedReader(new StringReader(slice)).readLine();
                String content = slice;
                fileSlices.add(new FileSlice(seq,content));
            }
            allFileSlices.add(new AllFileSlice(file.getName(),fileSlices));

        }
        return allFileSlices;
    }
    public void midGenerateFiles(List<AllFileSlice> fileSlices, String midPath) throws IOException {
        if (!Files.exists(Paths.get(midPath)))
            Files.createDirectories(Paths.get(midPath));
        for(AllFileSlice files : fileSlices){
            for(FileSlice f : files.fileSliceList){
                String outFilePath = midPath+"//"+files.fileName;
                outFilePath = outFilePath+"_"+ f.sliceSeq + ".srt";
                //File file = new File(outFilePath);
                Path out = Paths.get(outFilePath);
                Files.write(out,f.content.getBytes(),CREATE);
            }
        }
    }


    public static void main(String[] args) throws Exception {
        FileSplit fs = new FileSplit();
        List<AllFileSlice> fileSlices = fs.fileSplit("data");
        String midPath = "mid_data";
        fs.midGenerateFiles(fileSlices,midPath);
    }
}
