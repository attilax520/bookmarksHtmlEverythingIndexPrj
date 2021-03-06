package resume;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.attilax.util.FileCacheManager;
import com.attilax.util.HtmlUtilV2t33;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import aOPtool.preSvr_adminPubScrpt2publishtool2;
import emailPKg.ExtractTextFromPDF;
import emailPKg.emailMimeParse;
import officefile.wordutilV2t34;

public class resumeGrepper {
	private static final String DOCCACHE = "g:\\0db\\doccache";
	static Logger logger = Logger.getLogger(resumeGrepper.class);

	public static void main(String[] args) throws Exception {
		String dirString = "C:\\Users\\zhoufeiyue\\Downloads\\resm416";
		List li = Lists.newLinkedList();
		Files.walkFileTree(Paths.get(dirString), new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				// TODO Auto-generated method stub
				// return super.visitFile(file, attrs);
				System.out.println("\t正在访问" + file + "文件");

				String basenameString=FilenameUtils.getName(file.toFile().getAbsolutePath());
			 
				if (basenameString.startsWith("~$")) {
					// System.out.println("******找到目标文件Test.java******");
					return FileVisitResult.CONTINUE; // 找到了就终止
				}

				if (file.endsWith(".rar") || file.endsWith(".zip")) {
					// System.out.println("******找到目标文件Test.java******");
					return FileVisitResult.CONTINUE; // 找到了就终止
				}

				// ---------------pdf
				if (file.toAbsolutePath().toString().endsWith(".pdf")) {
					// System.out.println("******找到目标文件Test.java******");
					try {
						String readPDFV2WithCache = ExtractTextFromPDF
								.readPDFV2WithCache(file.toAbsolutePath().toString(), DOCCACHE);
						Map map = parseJilyePDf(readPDFV2WithCache);
						map.put("f", file.toString());
						li.add(map);
//						System.out.println(
//								readPDFV2WithCache);
//						;
					} catch (Exception e) {
						e.printStackTrace();
					}

					return FileVisitResult.CONTINUE; // 找到了就终止
				}
//				if ("1" == "1")
//					return FileVisitResult.CONTINUE; // 没找到继续找

				// =====================jilye html
				if (file.toAbsolutePath().toString().contains("智联")) {

					String html2txt = HtmlUtilV2t33.readHtmlFile2txtWithCache(file.toFile().toString(),
							DOCCACHE);

					// System.out.println(html2txt);
					Map map = parseJilye(html2txt);
					map.put("f", file.toString());
					li.add(map);
					return FileVisitResult.CONTINUE; // 没找到继续找
				}
				FileCacheManager fileCacheManager = new FileCacheManager(DOCCACHE);
				if (file.toFile().getAbsolutePath().contains("完颜杨威"))
					System.out.println("");
				// docx
				if (file.toAbsolutePath().toString().endsWith(".docx")) {
					try {
						String readWordV2 = wordutilV2t34.readWordDocxWithCache(file.toFile().getAbsolutePath(),
								fileCacheManager);
						Map map = parseJilye(readWordV2);
						li.add(map);
						map.put("f", file.toString());
						// System.out.println(readWordV2);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
				
				if (file.toFile().getAbsolutePath().contains("雷耀山"))
					System.out.println("");
				if (file.toFile().getAbsolutePath().contains("曾超 20年"))
					System.out.println("");
			
				// doc
				if (file.toAbsolutePath().toString().endsWith(".doc")) {

					try {
						String eml2txt = wordutilV2t34.readWordWithCache(file.toFile().getAbsolutePath(),
								fileCacheManager);
						// System.out.println(eml2txt);
						Map map = parseJilye(eml2txt);
						map.put("f", file.toString());
						li.add(map);
					} catch (Exception e) {
						//doc eml fmt 
						try {

							 // fileCacheManager = new FileCacheManager(DOCCACHE);
						
							String eml2txt = emailMimeParse.eml2txtWithCache(file.toFile().getAbsolutePath(),
									fileCacheManager);
							// System.out.println(eml2txt);
							Map map = parseJilye(eml2txt);
							map.put("f", file.toString());
							li.add(map);
						} catch (Exception e1) {
							Map debugMap = Maps.newLinkedHashMap();
							debugMap.put("f", file.toFile().getAbsolutePath());
							logger.error(JSON.toJSONString(debugMap), e1);
							e1.printStackTrace();
						}
					}

				}

				return FileVisitResult.CONTINUE; // 没找到继续找
			}

		});

//		File[] faFiles=new File(dirString).listFiles();
//		for (File f : faFiles) {
//			try {
//				System.out.println(f);
//			    System.out.println(wordutilV2t34.readWord(f.getAbsolutePath()));	  
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		
//		}
		System.out.println(JSON.toJSONString(li, true));

	}

	protected static Map parseJilyePDf(String html2txt) {
		Map map = Maps.newLinkedHashMap();
		String[] aStrings = html2txt.split("\r\n");
		for (String line : aStrings) {
			line=line.replaceAll(" ", "");
			if (line.contains("年 龄")) {
				line = line.replaceAll("\\u00A0", "");
				map.put("age", line);

			}

			if (line.contains("薪资"))
				map.put("salary", line);
		}
		return map;
	}

	protected static Map parseJilye(String html2txt) {

		Map map = Maps.newLinkedHashMap();
		String[] aStrings = html2txt.split("\r\n");
		for (String line : aStrings) {
			if (line.contains("工作经验")) {
				line = line.replaceAll("\\u00A0", "");
				if(map.get("age")==null)
				map.put("age", line);

			}

			if (line.contains("薪资"))
				map.put("salary", line);
		}
		
		if(map.get("age")==null)
			map.put("age",getByKeyword(html2txt,"出生日期"));
		if(map.get("salary")==null)
			map.put("salary", getSalaryJilye(html2txt));
		return map;
	}

	private static Object getSalaryJilye(String html2txt) {
	 try {
		 String[] aStrings = html2txt.split("\r\n");
			int n=0;
			for (String line : aStrings) {
				
				if (line.contains("期望月薪")) {
					return aStrings[n+1];

				}
				n++;
			 
			}
			return null;
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}
	
		
		
		 
	}

	private static Object getByKeyword(String html2txt, String keyword) {
		String[] aStrings = html2txt.split("\r\n");
		for (String line : aStrings) {
			if (line.contains(keyword)) {
				return line;

			}
		 
		}
		return null;
	}
		 
	
	
//	Function<String, String> fun = new Function<String, String>() {
//
//		@Override
//		public String apply(String Content) {
//			String CorrectContent = Content;
//			try {
//				if (file.toFile().getAbsolutePath().startsWith("【研发总监")) {
//					CorrectContent = new String(Content.getBytes("iso-8859-1"),
//							"gb2312");
//					return CorrectContent;
//				} else {
//					return Content;
//				}
//			} catch (UnsupportedEncodingException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return Content;
//
//		}
//	};
	 
}
