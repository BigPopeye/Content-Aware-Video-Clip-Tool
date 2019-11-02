import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;


public class SrtFileIndexer {
    private IndexWriter indexWriter;
    public SrtFileIndexer(String indexDir) throws IOException{
        // get index dir
        Directory directory = FSDirectory.open(Paths.get(indexDir));
        // stander
        Analyzer analyzer = new StandardAnalyzer();
        // save configs
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
        // instantiation
        indexWriter = new IndexWriter(directory,iwConfig);

    }

    public void close() throws IOException{
        indexWriter.close();
    }

    public int index(String dataDir) throws Exception{
        File[] files = new File(dataDir).listFiles();
        for(File file : files){
            indexFile(file);
        }
        return indexWriter.numRamDocs();
    }

    //获取文档，文档里再设置每个字段
    private void indexFile(File file) throws Exception{
        //out put index file dir
        System.out.println("索引文件："+ file.getCanonicalPath());
        // get doc, and set filed
        Document doc = getDocument(file);
        indexWriter.addDocument(doc);
    }

    private Document getDocument(File file) throws Exception{
        Document doc = new Document();
        doc.add(new TextField("contents",new FileReader(file)));
        //Yes 表示将文件名存入索引文件
        doc.add(new TextField("filename", file.getName(),Field.Store.YES));
        //add full path into indexfile
        doc.add(new TextField("fullpath", file.getCanonicalPath(),Field.Store.YES));
        return doc;
    }

    public static void main(String[] args){
        String indexDir = "dataIndex";
        String dataDir = "mid_data";
        SrtFileIndexer indexer = null;
        int numIndexed = 0;
        long start = System.currentTimeMillis();
        try{
            indexer = new SrtFileIndexer(indexDir);
            numIndexed = indexer.index(dataDir);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                indexer.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("索引："+ numIndexed+" 个文件，花费了"+(end - start) + " 毫秒");
    }


}
