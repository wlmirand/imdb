package william.miranda.recomendacao;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.model.FilmeRating;
import william.miranda.imdb.parser.UserParser;
import william.miranda.lucene.LuceneDatabase;
import william.miranda.lucene.LuceneSearch;
import william.miranda.xml.XMLParser;

/** Esta classe implementa o algoritmo de Recomendação baseada em Itens
 * @author william.miranda
 */
public class Recomendacao
{
	//estrutura para armazenar as entradas do u.data
	private Map<Integer, List<FilmeRating>> mapRatings;
	
	//engine do Lucene
	LuceneDatabase luceneDB;
	
	//inicializamos os pre requisitos
	public Recomendacao()
	{
		//faz o parsing dos ratings dos usuarios guardando no objeto mapRatings
		Path p = Paths.get("data/ml-100k/u.data");
		mapRatings = new UserParser(p).getUserRatings();
		
		//iniciamos a engine do Lucene com os diretorios de entrada e saida
		Path localXml = Paths.get("out/");
		Path localIndex = Paths.get("index/");
		
		luceneDB = new LuceneDatabase(localXml, localIndex, false);
	}
	
	//obtem as triplas do arquivo u.data
	public void percorrerAvaliacoes()
	{
		//para cada user, pega a Lista de Reviews
		for (int userId : mapRatings.keySet())
		{
			List<FilmeRating> lista = mapRatings.get(userId);
			
			//varremos a lista para obter as triplas originais
			for (FilmeRating fr : lista)
			{
				int filmeId = fr.getFilmeId();
				int rating = fr.getRating();
				
				//tendo a tripla original do arquivo, jogamos no algoritmo
				blah(filmeId);
			}
		}
	}
	
	public void blah(int filmeId)
	{
		//obtemos o XML do filme que foi passado
		Filme f = XMLParser.parseXML(Paths.get("out/" + filmeId + ".xml"));
		
		//calculamos as similaridades para o filme passado
		LuceneSearch luceneSearch = new LuceneSearch(f, luceneDB);
	}
}
