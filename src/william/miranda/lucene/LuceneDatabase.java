package william.miranda.lucene;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import william.miranda.imdb.model.Filme;
import william.miranda.xml.XMLParser;

/**
 * Nesta classe, criamos os indices e fazemos a busca atraves da Lucene Query
 * @author william.miranda
 *
 */
public class LuceneDatabase
{
	//constantes
	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";
	public static final String FIELD_TITULO = "titulo";
	public static final String FIELD_ANO = "ano";
	public static final String FIELD_ID = "id";
	public static final Version versao = Version.LUCENE_45;
	
	//variaveis da classe
	private Path xmlDir;
	private Path indexDir;	
	
	/* Construtor
	 * in: diretorio de entrada dos arquivos xml
	 * out: diretorio de saida para os indices
	 */
	public LuceneDatabase(Path xmlDir, Path indexDir, boolean forcarRecriarIndex)
	{
		this.xmlDir = xmlDir;
		this.indexDir = indexDir;
		
		//cria o indice somente se for necessario
		if (!Files.exists(indexDir, LinkOption.NOFOLLOW_LINKS))
		{
			try
			{
				Files.createDirectory(indexDir);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		File file = indexDir.toFile();
		
		if(file.isDirectory() && file.list().length == 0 || forcarRecriarIndex)
		{
			try
			{
				createIndex();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void createIndex() throws IOException
	{
		Analyzer analyzer = new StandardAnalyzer(versao);

		//Directory directory = new SimpleFSDirectory(indexDir.toFile());
		Directory directory = FSDirectory.open(indexDir.toFile());
		final IndexWriter indexWriter = new IndexWriter(directory, new IndexWriterConfig(versao, analyzer));
		
		indexWriter.deleteAll();
		
		Files.walkFileTree(xmlDir, new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException
					{
						Document doc = new Document();
						
						//adicionamos o path
						String path = file.toAbsolutePath().toString();
						doc.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.NO));
						
						//adicionamos o id, o nome e o ano do filme
						Filme f = XMLParser.parseXML(file);
						String id = String.valueOf(f.getId());
						String ano = String.valueOf(f.getAno());
						doc.add(new Field(FIELD_TITULO, f.getTitulo(), StringField.TYPE_STORED));
						doc.add(new Field(FIELD_ANO, ano, StringField.TYPE_STORED));
						doc.add(new Field(FIELD_ID, id, StringField.TYPE_STORED));
						
						//adicionamos o conteudo do arquivo para ser buscado
						Reader reader = new FileReader(file.toFile());
						doc.add(new Field(FIELD_CONTENTS, reader));

						indexWriter.addDocument(doc);
						
						return FileVisitResult.CONTINUE;
					}
				});
		
		indexWriter.close();
	}
	
	public List<LuceneResult> searchIndex(String searchString)
	{
		//cria o objeto que vamos retornar
		List<LuceneResult> res = new ArrayList<>();
		
		try
		{
			Directory directory = FSDirectory.open(indexDir.toFile());
			IndexReader indexReader = IndexReader.open(directory);
			IndexSearcher indexSearcher = new IndexSearcher(indexReader);
	
			Analyzer analyzer = new StandardAnalyzer(versao);
			QueryParser queryParser = new QueryParser(versao, FIELD_CONTENTS, analyzer);
			Query query = queryParser.parse(searchString);
			TopDocs hits = indexSearcher.search(query, 150);
			
			//para cada registro encontrado, obtem o documento e os seus campos
			for (ScoreDoc sd : hits.scoreDocs)
			{
				Document doc = indexSearcher.doc(sd.doc);
				
				int id = Integer.valueOf(doc.get(FIELD_ID));
				String titulo = doc.get(FIELD_TITULO);
				
				//geramos alguma coisa para retornar na busca
				res.add(new LuceneResult(id, titulo, sd.score));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return res;
	}

}
