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
	
	//media global dos itens e o numero de entrada do u.data
	private static float mediaGlobal;
	private static long numeroAvaliacoes;
	
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
		
		//usados para a media global
		long notas = 0;
		numeroAvaliacoes = 0;
		
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
			
			//soma todos os ratings para obter a media depois
			notas += rating;
			numeroAvaliacoes++;
		}
		
		//calcula a media global de todos os filmes
		mediaGlobal = (float) notas / numeroAvaliacoes;
		
		return res;
	}
	
	//calcula a media do rating do Filme, removendo a tripla onde usuario = userID
	/*
	 * (u1, f, n1)
	 * (u2, f, n2)
	 * (u3, f, n3)
	 * (u4, f, n4)
	 * (u5, f, n5)
	 * (u6, f, n6)
	 * 
	 * se chamamos mediaRatingFilme(u2, f), iremos retornar:  media = (n1+n3+n4+n5+n6) / 5 */
	public float mediaRatingFilme(int filmeId, int userId)
	{
		int nota = 0;
		int num = 0;
		
		for (int i : userRatings.keySet())
		{
			//varre a lista de reviews para um userId
			for (FilmeRating fr : userRatings.get(i))
			{
				//se encontrou o filme que procuramos E nao eh o usuario passado, adiciona seu rating no vetor
				if (fr.getFilmeId() == filmeId && i != userId)
				{
					nota += fr.getRating();
					num++;
					break;
				}
			}
		}
		
		//se a unica avaliacao do filme foi feita pelo userId, nao temos como calcular a media, entao retorna o valor global
		if (num == 0)
			return mediaGlobal;
		
		//agora temos a soma de todos os ratings e o numero de reviews, basta fazer a divisao
		return (float) nota / num;
	}
	
	//obtemos uma tripla do arquivo (a avaliacao de um usuario para um determinado filme)
	public FilmeRating getTripla(int userId, int filmeId)
	{
		List<FilmeRating> tmp = userRatings.get(userId);
		
		for (FilmeRating fr : tmp)
		{
			if (fr.getFilmeId() == filmeId)
				return fr;
		}
		
		return null;
	}
	
	public Map<Integer, List<FilmeRating>> getUserRatings()
	{
		return this.userRatings;
	}
	
	public static float getMediaGlobal() {
		return mediaGlobal;
	}
	
	public static long getNumeroAvaliacoes() {
		return numeroAvaliacoes;
	}
}
