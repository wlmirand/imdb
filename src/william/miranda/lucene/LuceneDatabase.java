package william.miranda.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import william.miranda.imdb.model.Filme;

public class LuceneDatabase
{
	//variaveis internas
	private StandardAnalyzer analyzer;
	private Directory index;
	private IndexWriterConfig config;
	private IndexWriter writer;
	
	public LuceneDatabase() throws IOException
	{
		analyzer = new StandardAnalyzer(Version.LUCENE_45);
		index = new RAMDirectory();
		config = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		writer = new IndexWriter(index, config);
	}
	
	public void addDoc(Filme f) throws IOException
	{
		Document doc = new Document();
		doc.add(new StringField("titulo", f.getTitulo(), Field.Store.YES));
		doc.add(new IntField("ano", f.getAno(), Field.Store.YES));
		writer.addDocument(doc);
		
		//org.apache.lucene.document.
	}
}
