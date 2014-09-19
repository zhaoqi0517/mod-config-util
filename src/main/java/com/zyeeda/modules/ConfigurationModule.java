package com.zyeeda.modules;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.xml.sax.SAXException;

import com.zyeeda.helpers.JDBCHelper;
import com.zyeeda.helpers.PropertyHelper;
import com.zyeeda.helpers.XMLHelper;

/*
 This is a simple Java module which edit configuration file on the event bus.
 */
public class ConfigurationModule extends BusModBase implements
		Handler<Message<JsonObject>> {

	private String address;
	private String appConfigPath;
	private String dbConfigPath;
	private String serverConfigPath;

	public void start() {
		super.start();

		EventBus eb = vertx.eventBus();
		address = getOptionalStringConfig("address", "com.zyeeda.property.util");
		appConfigPath = getOptionalStringConfig("appConfigPath", "");
		dbConfigPath = getOptionalStringConfig("dbConfigPath", "");
		serverConfigPath = getOptionalStringConfig("serverConfigPath", "");

		System.out.println("address --->" + address);
		System.out.println("app config path --->" + appConfigPath);
		System.out.println("db config path --->" + dbConfigPath);
		System.out.println("server config path --->" + serverConfigPath);

		if (logger.isDebugEnabled()) {
			logger.debug("address = " + address);
			logger.debug("app config file path = " + appConfigPath);
			logger.debug("server config file path = " + serverConfigPath);
			logger.debug("db config file path = " + dbConfigPath);
		}

		eb.registerHandler(address, this);
	}

	@Override
	public void handle(final Message<JsonObject> message) {
		String fileType = message.body().getString("type");
		System.out.println("type --->" + fileType);
		switch (fileType) {
		case "app":
			doUpdateAppConfig(message);
			break;
		case "db":
			doUpdateDBConfig(message);
			break;
		case "server":
			doUpdateServerConfig(message);
			break;
		default:
			logger.error("Oops!!! Error type.");
			break;
		}
	}

	private void doUpdateDBConfig(Message<JsonObject> message) {
		String driver = message.body().getString("dbDriver");
		String name = message.body().getString("dbName");
        String host = message.body().getString("dbHost");
		String port = message.body().getString("dbPort");
		String userName = message.body().getString("dbUserName");
		String password = message.body().getString("dbPwd");

		try {
//            String URL = JDBCHelper.generateURL(host, port);
//			boolean flag = JDBCHelper.executeCreateDB(driver, URL, userName, password, name);
//			if (flag) {
				String URL = JDBCHelper.generateURL(host, port, name);
				boolean flag = XMLHelper.updateDBConfig(userName, password, name, driver, URL, dbConfigPath);
				System.out.println("update db config result --->" + flag);
				if (flag) {
					sendOK(message);
				} else {
					sendError(message, "修改数据库信息出错");
				}
//			} else {
//				sendError(message, "创建数据库出错");
//			}
		} catch (XPathExpressionException | ParserConfigurationException
				| SAXException | IOException | TransformerException e) {
			sendError(message, e.getMessage());
			logger.error(e.getMessage(), e);
		} 
	}

	// TODO:考虑是否把修改property 和 jetty.xml 文件放在里面。
	private void doUpdateServerConfig(Message<JsonObject> message) {
		String port = message.body().getString("serverPort");

		try {
			if (!isPortInUse("localhost", new Integer(port))) {
				boolean flag = XMLHelper.updateServerConfig(port, this.getServerConfigPath());
				System.out.println("update server config result --->" + flag);
				if (flag) {
					sendOK(message);
				} else {
					sendError(message, "修改服务端口出错");
				}
			} else {
				sendError(message, "端口已经被占用:" + port);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println(e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (XPathExpressionException e) {
			System.err.println(e.getMessage());
			logger.error(e.getMessage(), e);
		} catch (TransformerException e) {
			System.err.println(e.getMessage());
			logger.error(e.getMessage(), e);
		}
	}

	private void doUpdateAppConfig(Message<JsonObject> message) {
		String appName = message.body().getString("appName");
		String appId = message.body().getString("appId");
		String appPath = message.body().getString("appPath");

		if (logger.isDebugEnabled()) {
			logger.debug("application name = " + appName);
			logger.debug("application id = " + appId);
			logger.debug("application path = " + appPath);
		}

		try {
			File appDir = new File(appPath);
			boolean isCreated = appDir.mkdir();
			if (isCreated) {
				boolean flag = PropertyHelper.updateProperty(appId, this.getAppConfigPath());
				System.out.println("update db config result --->" + flag);
				if (flag) {
					sendOK(message);
				} else {
					sendError(message, "修改应用配置信息出错");
				}
			} else {
				sendError(message, "创建应用工作空间失败");
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private boolean isPortInUse(String hostName, int port) {
		Socket socket = null;
		try {
			socket = new Socket(hostName, port);
			return true;
		} catch (IOException e) {
			return false;
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}

	}

	public String getAddress() {
		return this.address;
	}

	public String getAppConfigPath() {
		return appConfigPath;
	}

	public String getServerConfigPath() {
		return serverConfigPath;
	}

	public String getDbConfigPath() {
		return dbConfigPath;
	}

}