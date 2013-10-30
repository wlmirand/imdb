package william.miranda.imdb.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import william.miranda.imdb.model.FilmeRating;

/** Classe para fazer o parser do arquivo u.data, que contem os ratings que os usuarios
 * deram para os filmes.
 * 
 * O arquivo a ser parseado eh da forma:   (onde | representa o TAB).
 * user id | item id | rating | timestamp.
 * 
 * Apos parsear, guardamos o resultado em uma estrutura do tipo Map,
 * onde a chave eh o userId e o value eh List<FilmeRating>
 * Esta implementação melhora a velocidade na busca, uma vez que as estruturas
 * estão otimizadas.
 * @author william.miranda
 *
 */
public class UserParser
{
	//local do arquivo u.data
	private Path path;
	
	//guarda as linhas do arquivo
	private List<String> ratings;
	
	//guarda o conteudo ja parseado, mapeando os usuarios para uma lista dos reviws que este usuario fez
	private Map<Integer, List<FilmeRating>> userRatings;
	
	public UserParser(Path path)
	{
		this.path = path;
		ratings = Utils.readFromFile(path);
		userRatings = parseRatings();
	}
	
	//parseia o arquivo e guarda em um Hash com a chave sendo o id do usuario
	private Map<Integer, List<FilmeRating>> parseRatings()
	{
		Map<Integer, List<FilmeRating>> res = new HashMap<>();
		
		//para cada linha faz o parse
		for (String linha : ratings)
		{
			String[] tmp = linha.split("\t");
			
			//separamos os dados
			int userId = Integer.valueOf(tmp[0]);
			int filmeId = Integer.valueOf(tmp[1]);
			int rating = Integer.valueOf(tmp[2]);
			
			List<FilmeRating> listaTmp = res.get(userId); 

			if (listaTmp == null)//se a chave nao existe, criamos a lista e adicionamos
				listaTmp = new ArrayList<>();
			
			listaTmp.add(new FilmeRating(filmeId, rating));
			res.put(userId, listaTmp);
		}
		
		return res;
	}
	
	public Map<Integer, List<FilmeRating>> getUserRatings()
	{
		return this.userRatings;
	}
}
