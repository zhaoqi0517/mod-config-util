package com.zyeeda.helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertyHelper {

	public static boolean updateProperty(String appId, String appConfigPath)
			throws IOException {
		Properties property = new Properties();
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(new File(appConfigPath));
			//is = PropertyHelper.class.getResourceAsStream(appConfigPath);
			property.load(is);
			property.setProperty("coala.application.name", appId);

			// should be set appName?
			os = new FileOutputStream(new File(appConfigPath));

			property.store(os, "already updated");

			return true;
		} finally {
			if (os != null) {
				os.close();
			}

			if (is != null) {
				is.close();
			}
		}
	}
}
