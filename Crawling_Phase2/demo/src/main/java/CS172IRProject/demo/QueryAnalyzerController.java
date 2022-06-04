package CS172IRProject.demo;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/api")
@CrossOrigin("+")

public class QueryAnalyzerController {
    static List<QueryAnalyzer> tweets;
    static {
        tweets = new ArrayList<>();
    }

    //Call this through url localhost:8080/api/query?query="enter query here"
    //Use for title or text fields
    @GetMapping("/query")
    public List<QueryAnalyzer> queryAnalyzer(
            @RequestParam(required = false, defaultValue = "Ukraine") String query) throws ParseException, IOException {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(new String[]{"titles", "text"}, new StandardAnalyzer());
        Query q = parser.parse(query);
        Directory dir = FSDirectory.open(Paths.get("index"));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, 100);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");

        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            System.out.println(docId);
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("username") + "\n" + d.get("text") + "\n" + d.get("timestamp") + "\n" + d.get("location") + "\n" + d.get("titles"));
            tweets.add(new QueryAnalyzer(query,d));
        }
        return tweets;
    }
    //Call this through url localhost:8080/api/query?query="enter query here"
    //Use for title or text fields
    @GetMapping("/usernamequery")
    public List<QueryAnalyzer> usernamequeryAnalyzer(
            @RequestParam(required = false, defaultValue = "Ukraine") String query) throws ParseException, IOException {
        QueryParser parser = new QueryParser("username", new StandardAnalyzer());
        String query_with_wildcard = new String(query);
        query_with_wildcard = query.concat("*");
        Query q = parser.parse(query_with_wildcard);
        Directory dir = FSDirectory.open(Paths.get("index"));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, 100);
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("Found " + hits.length + " hits.");

        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            System.out.println(docId);
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get("username") + "\n" + d.get("text") + "\n" + d.get("timestamp") + "\n" + d.get("location") + "\n" + d.get("titles"));
            tweets.add(new QueryAnalyzer(query,d));
        }
        return tweets;
    }

}