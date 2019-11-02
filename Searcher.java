import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.Paths;

public class Searcher {
    public static void search(String indexDir, String q) throws Exception {

        // get index doc path
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        // get all the indexed files
        IndexReader reader = DirectoryReader.open(dir);
        // build index searcher
        IndexSearcher is = new IndexSearcher(reader);
        // instantiate analyzer
        Analyzer analyzer = new StandardAnalyzer();
        // build search parser
        /**
         * first parameter : query key, second parameter : Analyzer
         */
        QueryParser parser = new QueryParser("contents", analyzer);
        // parse user input q
        Query query = parser.parse(q);
        // calculate time
        long start = System.currentTimeMillis();
        // begin query
        /**
         * 第一个参数是通过传过来的参数来查找得到的query； 第二个参数是要出查询的行数
         */
        TopDocs hits = is.search(query, 20);
        // 计算索引结束时间
        long end = System.currentTimeMillis();
        System.out.println("匹配 " + q + " ，总共花费" + (end - start) + "毫秒" + "查询到" + hits.totalHits + "个记录");
        // 遍历hits.scoreDocs，得到scoreDoc
        /**
         * ScoreDoc:得分文档,即得到文档 scoreDocs:代表的是topDocs这个文档数组
         *
         * @throws Exception
         */
        for (ScoreDoc scoreDoc : hits.scoreDocs) {
            Document doc = is.doc(scoreDoc.doc);
            System.out.println(doc.get("fullpath"));
            System.out.println(scoreDoc.score);
        }

        // 关闭reader
        reader.close();
    }

    public static void main(String[] args) {
        String indexDir = "dataindex";
        //target search content
        String q = "Last AND night AND I AND had AND a AND dream";
        try {
            search(indexDir, q);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
