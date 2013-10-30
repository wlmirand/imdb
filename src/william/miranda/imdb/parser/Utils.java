package william.miranda.imdb.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import william.miranda.imdb.model.Filme;

public class Utils
{
	public static String preparaURL(String url)
	{
		String tmp = url;
		
		if (url.endsWith("/"))
		{
			tmp = url.substring(0, url.length()-1);
		}
		
		return tmp;		
	}
	
	public static void saveToFile(Path path, String conteudo)
	{
		try
		{
			//se o arquivo nao existe, cria um do zero
			if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			{
				Files.createFile(path);
			}
			
			BufferedWriter br = Files.newBufferedWriter(path, getCharset(), new OpenOption[] {StandardOpenOption.WRITE});
			
			br.write(StringEscapeUtils.unescapeXml(conteudo));
			br.write("\n");
			br.flush();
			br.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveToFile(Path path, List<String> conteudo)
	{
		try
		{
			//se o arquivo nao existe, cria um do zero
			if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			{
				Files.createFile(path);
			}
			
			BufferedWriter br = Files.newBufferedWriter(path, getCharset(), new OpenOption[] {StandardOpenOption.WRITE});
			
			for (String linha : conteudo)
			{
				if (linha != null)
				{
					br.write(linha);
					br.write("\r\n");
				}
			}

			br.flush();
			br.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<String> readFromFile(Path path)
	{
		List<String> lines = new ArrayList<>();
		
		try
		{
			lines = Files.readAllLines(path, getCharset());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return lines;
	}
	
	public static StringBuilder readStringFromFile(Path path)
	{
		try
		{
			BufferedReader reader = Files.newBufferedReader(path, Utils.getCharset());
			String s = reader.readLine();
			StringBuilder sb = new StringBuilder();
			
			while (s != null)
			{
				sb.append(s).append("\r\n");
				s = reader.readLine();
			}
			
			return sb;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		return null;
	}
	
	public static Charset getCharset()
	{
		//return Charset.forName("ISO-8859-1");
		return Charset.forName("UTF-8");
	}
	
	public static void salvarFilme(Filme f)
	{
		Path path = Paths.get("out/" + f.getId() + ".xml");
		
		System.out.println(f);	
		Utils.saveToFile(path, f.toXML().toString());
	}
	
	public static List<Integer> getIDs()
	{
		//abre o arquivo de URLs, para obter os codigos
		Path pathUrls = Paths.get("urls.txt");
		List<String> urls = Utils.readFromFile(pathUrls);
		
		List<Integer> ids = new ArrayList<>();
		
		for (String linha : urls)
		{
			String[] tmp = linha.split("\\|");
				
			int id = Integer.valueOf(tmp[0].trim());
			ids.add(id);
		}
		
		return ids;
	}
	
	public static void generateFinalXML() throws IOException
	{
		List<Integer> ids = getIDs();
		Path res = Paths.get("resultado.xml");
		
		String start = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		
		/*
		BufferedWriter writer = Files.newBufferedWriter(res, Utils.getCharset(), StandardOpenOption.CREATE);
		writer.write(start + "\n");
		writer.write("<filmes>\n");
		*/
		
		for (int id : ids)
		{
			Path p = Paths.get("out/" + id + ".xml");

			//se o filme foi parseado, adiciona
			if (Files.exists(p, LinkOption.NOFOLLOW_LINKS))
			{
				BufferedReader reader = Files.newBufferedReader(p, Utils.getCharset());
				String line = null;

				BufferedWriter writer = Files.newBufferedWriter(res, Utils.getCharset(), StandardOpenOption.APPEND);
				
				while ((line = reader.readLine()) != null)
				{
					writer.write(line + "\n");
				}
				
				writer.close();
			}
		}
		
		//writer.write("</filmes>\n");
	}
}
