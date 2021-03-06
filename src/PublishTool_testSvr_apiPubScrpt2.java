import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.attilax.fileTrans.AuthEx;
import com.attilax.fileTrans.ConnEx;
import com.attilax.fileTrans.SShFileUtilV3t33;
import com.attilax.fileTrans.createSCPClientEx;
import com.attilax.fileTrans.uploadFileEx;
import com.attilax.util.PrettyUtil;
import com.attilax.util.shellUtilV2t33;
import com.google.common.base.Joiner;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

public class PublishTool_testSvr_apiPubScrpt2 {

	final static Logger logger = Logger.getLogger(PublishTool_testSvr_apiPubScrpt2.class);

	public static void main(String[] args) throws IOException, ConnEx, AuthEx, createSCPClientEx, uploadFileEx {

		
	 
				
				Properties prop = new Properties();
        
        InputStream in = PublishTool_testSvr_apiPubScrpt2.class.getClassLoader().getResourceAsStream(
                "publish.properties");
        prop.load(in);
        String actProfile=prop.getProperty("act");
		SShFileUtilV3t33 c = new SShFileUtilV3t33().setScpAddress("47.100.12.36").setScpPort("22").setUsername("root")
				.setPassword("dKttdev.321");

		Connection connection = c.conn();
		logger.info(" conned ok");
		//Session session = connection.openSession();

	//	uploadWar(c, connection);

		
		 rebootTomcat(connection);

		System.out.println("--f");

	}

	private static void uploadWar(SShFileUtilV3t33 c, Connection connection) throws IOException {
		// bek
		String cmd_bek = "mv  /tt/www/api-tomcat9/webapps/api.war    /tt/www/api-tomcat9/backup/api.war.21 ";
		logger.info(cmd_bek);
		List<String> result2 =SShFileUtilV3t33. exec(cmd_bek, connection);
		System.out.println(Joiner.on("\r\n").join(result2));

		// upload
		String scppath = "/tt/www/api-tomcat9/webapps";
		String localFIle = "H:\\gitCode\\tt-api\\com-tt-yxt\\target\\tt-yxt-0.0.1-SNAPSHOT.war";
		logger.info("upload file:" + localFIle + " " + scppath);
		c.scpClient = c.getScpclient(connection);
		// c.upload(connection, localFIle, scppath);

		// rename
		String cmd = " mv  /tt/www/api-tomcat9/webapps/tt-yxt-0.0.1-SNAPSHOT.war  /tt/www/api-tomcat9/webapps/api.war ";
		logger.info(cmd);
		List<String> result =SShFileUtilV3t33. exec(cmd, connection);
		System.out.println(Joiner.on("\r\n").join(result));
	}

	private static void rebootTomcat(Connection connection) throws IOException {
		String kewword_forkillpid = "web-tomcat8";
		try {
			killTomcat(connection, kewword_forkillpid);
		} catch (Exception e) {
			logger.info("", e);
		}
	
		
		//restart
		
		///usr/local/jenkins-tomcat8   /usr/local/jenkins-tomcat8/bin/startup.sh 
		String JAVA_HOME="export JAVA_HOME=/usr/java/jdk1.8.0_77"; 
		String cmd_startTomcat=JAVA_HOME+";"+" /usr/local/web-tomcat8/bin/startup.sh  ";
		logger.info(cmd_startTomcat);
		System.out.println(SShFileUtilV3t33.exec(cmd_startTomcat, connection.openSession(),3));
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		String cmd3 = "ps -ef|grep  tomcat"  ;
		logger.info(cmd3);
		System.out.println(SShFileUtilV3t33.exec(cmd3, connection));
		 
	}

	private static void killTomcat(Connection connection, String kewword_forkillpid) throws IOException {
		//get 
		String cmd3 = "ps -ef|grep  tomcat"  ;
		logger.info(cmd3);
		List<String> result3 =SShFileUtilV3t33. exec(cmd3, connection);
		String ps_rzt_csv = Joiner.on("\r\n").join(result3);
		System.out.println(ps_rzt_csv);
	//	  readAsCsv(ps_rzt_csv);
		logger.info("------------------");

		System.out.println("\r\n");
		List<Map> tab = shellUtilV2t33.toTableNoHeadMode_ByMultiSpace(ps_rzt_csv);
		
		int pid =shellUtilV2t33. getPid(tab, kewword_forkillpid, 1);
		logger.info("---getpid:"+String.valueOf(pid));
		
		 
		System.out.println(PrettyUtil.showListMap(tab));
		
		
		//kill pid
		String cmd4 = "kill "+String.valueOf(pid)  ;
		logger.info(cmd4);
		logger.info("kill ret:"+SShFileUtilV3t33.exec(cmd4, connection ));
	}

}
