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
		
		Process p = null;
		InputStream is = null;
		try {
			for (int i = 0; i < libs.size(); i++) {
				InternalLib lib = libs.get(i);
				p = Runtime.getRuntime().exec(getMavenHome() + " install:install-file -DpomFile=" + lib.getPomFile()
								+ " -Dfile=" + lib.getFile());
				copy(is = p.getInputStream(), System.out);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			
			if (p != null) {
				p.destroy();
			}
		}

	}

	public static void executeFlyway(String targetPath) throws IOException {
		Process p = null;
		InputStream is = null;
		try {
			File dir = new File(targetPath);
//			ProcessBuilder pb = new ProcessBuilder(getMavenHome() + " compile flyway:migrate");
//			pb.directory(dir);
//			pb.redirectError();
//
//			p = pb.start();
			p = Runtime.getRuntime().exec(new String[]{getMavenHome(), "compile", "flyway:migrate"}, null, dir);
			copy(is = p.getInputStream(), System.out);
		} finally {
			if (is != null) {
				is.close();
			}
			if (p != null) {
				p.destroy();
			}
		}
		
	}
	
	private static String getMavenHome() throws IOException {
		String mavenHome = System.getenv("MAVEN_HOME");
		if (null == mavenHome || "".equals(mavenHome)) {
			mavenHome = System.getenv("M2_HOME");
			if (null == mavenHome || "".equals(mavenHome)) {
				throw new IOException("请设置MAVEN_HOME");
			}
		}
		
		String os = System.getenv("OS");
		String extensionName = os.compareToIgnoreCase("Windows") > 0 ? ".bat" : "sh";
		
		return mavenHome + File.separator + "bin" + File.separator +  
				"mvn"+extensionName;
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

	public static void main(String[] args) throws IOException {
//		File file = new File("");
//		Process p = Runtime.getRuntime().exec(new String[]{"pwd"}, System.getenv("PATH").split(File.pathSeparator), file);
		String[] versions = {};
		String s = "5.3.6";
		versions = s.split("[.]");
		System.out.println(versions[0]);
		System.out.println(versions[1]);
//		String s = "/Users/qizhao/Workspace/Zyeeda/cdeio-setup/app/pom.xml";
//		System.out.println(s.substring(0, s.lastIndexOf("/")));
//		copy(p.getInputStream(), System.out);
//		Map<String, String> env = System.getenv();
//        for (String envName : env.keySet()) {
//            System.out.format("%s=%s%n",
//                              envName,
//                              env.get(envName));
//        }
	}
}
