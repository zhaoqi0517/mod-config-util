package com.zyeeda.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.zyeeda.helpers.Item.InternalLib;

public class MavenHelper {

	public static void installPackage(String configFile) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();

		List<InternalLib> libs = mapper.readValue(
				new File(configFile),
				TypeFactory.defaultInstance().constructCollectionType(
						List.class, InternalLib.class));

		for (int i = 0; i < libs.size(); i++) {
			InternalLib lib = libs.get(i);
			Process p = Runtime.getRuntime().exec(
					"mvn install:install-file -DpomFile=" + lib.getPomFile()
							+ " -Dfile=" + lib.getFile());
			copy(p.getInputStream(), System.out);
		}

	}

	public static void copy(InputStream in, OutputStream out)
			throws IOException {
		while (true) {
			int c = in.read();
			if (c == -1)
				break;
			out.write((char) c);
		}
	}

}
