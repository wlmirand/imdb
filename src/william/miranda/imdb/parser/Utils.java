package william.miranda.imdb.parser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

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
			
			BufferedWriter br = Files.newBufferedWriter(path, Charset.forName("UTF-8"), new OpenOption[] {StandardOpenOption.WRITE});
			br.write(conteudo);
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
			
			BufferedWriter br = Files.newBufferedWriter(path, Charset.forName("UTF-8"), new OpenOption[] {StandardOpenOption.WRITE});
			
			for (String linha : conteudo)
			{
				if (linha != null)
				{
					br.write(linha);
					br.write("\n");
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
}
