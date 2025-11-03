// src/main/java/com/pig4cloud/pig/common/poi/utils/PdfUtil.java
package com.pig4cloud.pig.common.poi.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.pig4cloud.pig.common.poi.except.PoiPdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * iText 5.5.13.4 生产级 PDF 工具类（日志 + 异常安全）
 */
public class PdfUtil {

	private static final Logger log = LoggerFactory.getLogger(PdfUtil.class);

	// 字体（每次创建，线程安全）
	private static BaseFont getChineseFont() {
		try {
			return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
		} catch (Exception e) {
			log.error("加载中文字体失败", e);
			throw new PoiPdfException("中文字体加载失败", e);
		}
	}

	private static Font getFont(int size, int style) {
		return new Font(getChineseFont(), size, style);
	}

	// ==================== 1. 纯文本 PDF ====================
	public static void createTextPdf(String outputPath) {
		validateOutputPath(outputPath);
		Document document = null;
		try {
			document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			document.open();

			document.add(new Paragraph("用户协议", getFont(16, Font.BOLD)));
			document.add(Chunk.NEWLINE);
			document.add(new Paragraph("欢迎使用本服务。本协议由您与本公司签订，具有法律效力。", getFont(12, Font.NORMAL)));
			document.add(new Paragraph("1. 服务内容：我们提供在线工具服务。", getFont(12, Font.NORMAL)));
			document.add(new Paragraph("2. 用户义务：请遵守法律法规。", getFont(12, Font.NORMAL)));

			log.info("文本 PDF 生成成功: {}", outputPath);
		} catch (Exception e) {
			log.error("生成文本 PDF 失败: {}", outputPath, e);
			throw new PoiPdfException("文本 PDF 生成失败", e);
		} finally {
			closeDocument(document);
		}
	}

	// ==================== 2. 表格 PDF ====================
	public static void createTablePdf(String outputPath) {
		validateOutputPath(outputPath);
		Document document = null;
		try {
			document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			document.open();

			PdfPTable table = new PdfPTable(3);
			table.setWidthPercentage(100);
			table.setWidths(new int[]{1, 2, 2});

			Font bold = getFont(16, Font.BOLD);
			Font normal = getFont(12, Font.NORMAL);

			table.addCell(new PdfPCell(new Phrase("ID", bold)));
			table.addCell(new PdfPCell(new Phrase("姓名", bold)));
			table.addCell(new PdfPCell(new Phrase("邮箱", bold)));

			table.addCell(new PdfPCell(new Phrase("1", normal)));
			table.addCell(new PdfPCell(new Phrase("张三", normal)));
			table.addCell(new PdfPCell(new Phrase("zhangsan@example.com", normal)));

			document.add(table);
			log.info("表格 PDF 生成成功: {}", outputPath);
		} catch (Exception e) {
			log.error("生成表格 PDF 失败: {}", outputPath, e);
			throw new PoiPdfException("表格 PDF 生成失败", e);
		} finally {
			closeDocument(document);
		}
	}

	// ==================== 3. HTML 转 PDF ====================
	public static void htmlToPdf(String html, String outputPath) {
		if (html == null || html.trim().isEmpty()) {
			throw new PoiPdfException("HTML 内容不能为空");
		}
		validateOutputPath(outputPath);

		ITextRenderer renderer = null;
		try (OutputStream os = new FileOutputStream(outputPath)) {
			renderer = new ITextRenderer();
			renderer.setDocumentFromString(html);
			renderer.layout();
			renderer.createPDF(os);
			log.info("HTML 转 PDF 成功: {}", outputPath);
		} catch (Exception e) {
			log.error("HTML 转 PDF 失败", e);
			throw new PoiPdfException("HTML 转 PDF 失败", e);
		}
	}

	// 示例 HTML
	public static String getSampleHtml() {
		return """
            <!DOCTYPE html>
            <html><head><meta charset="UTF-8" />
            <style>
                body { font-family: 'SimSun', sans-serif; margin: 40px; }
                h1 { color: #2c3e50; text-align: center; }
                table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                th { background-color: #f2f2f2; }
            </style>
            </head><body>
                <h1>用户报表</h1>
                <table>
                    <tr><th>ID</th><th>姓名</th><th>状态</th></tr>
                    <tr><td>1</td><td>李四</td><td>活跃</td></tr>
                </table>
                <p style="color: red;">本报告仅供参考。</p>
            </body></html>
            """;
	}

	// ==================== 4. 带页眉页脚 ====================
	public static void createPdfWithHeaderFooter(String outputPath) {
		validateOutputPath(outputPath);
		Document document = null;
		try {
			document = new Document(PageSize.A4);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(outputPath));
			writer.setPageEvent(new HeaderFooterEvent());
			document.open();
			document.add(new Paragraph("带页眉页脚的内容页面", getFont(12, Font.NORMAL)));
			log.info("带页眉页脚 PDF 生成成功: {}", outputPath);
		} catch (Exception e) {
			log.error("生成带页眉页脚 PDF 失败", e);
			throw new PoiPdfException("页眉页脚 PDF 生成失败", e);
		} finally {
			closeDocument(document);
		}
	}

	// 页眉页脚事件
	private static class HeaderFooterEvent extends PdfPageEventHelper {
		private final Font headerFont = getFont(12, Font.NORMAL);
		private final Font footerFont = getFont(12, Font.NORMAL);

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			try {
				// 页眉
				PdfPTable header = new PdfPTable(1);
				header.setTotalWidth(50);
				header.addCell(new PdfPCell(new Phrase("机密文件", headerFont)));
				header.writeSelectedRows(0, -1, 40, document.top() + 20, writer.getDirectContent());

				// 页脚
				PdfPTable footer = new PdfPTable(1);
				footer.setTotalWidth(100);
				footer.addCell(new PdfPCell(new Phrase("第 " + writer.getPageNumber() + " 页", footerFont)));
				footer.writeSelectedRows(0, -1, document.leftMargin(), 30, writer.getDirectContent());
			} catch (Exception e) {
				log.warn("页眉页脚渲染异常", e);
			}
		}
	}

	// ==================== 5. 字节流输出 ====================
	public static byte[] textPdfToBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = null;
		try {
			document = new Document();
			PdfWriter.getInstance(document, baos);
			document.open();
			document.add(new Paragraph("动态生成的 PDF 内容", getFont(12, Font.NORMAL)));
			log.debug("字节流 PDF 生成成功");
			return baos.toByteArray();
		} catch (Exception e) {
			log.error("字节流 PDF 生成失败", e);
			throw new PoiPdfException("字节流 PDF 生成失败", e);
		} finally {
			closeDocument(document);
		}
	}

	// ==================== 6. 合并 PDF ====================
	/**
	 * 合并多个 PDF（生产可用）
	 */
	public static void mergePdfs(List<String> inputPaths, String outputPath) {
		if (inputPaths == null || inputPaths.isEmpty()) {
			throw new PoiPdfException("输入 PDF 列表不能为空");
		}
		validateOutputPath(outputPath);

		Document document = null;
		PdfCopy copy = null;
		FileOutputStream fos = null;

		try {
			// 1. 打开文件流（不放在 try-with-resources）
			fos = new FileOutputStream(outputPath);

			// 2. 创建 Document & PdfCopy
			document = new Document();
			copy = new PdfCopy(document, fos);
			document.open();                     // <-- 必须先 open

			int mergedCount = 0;
			for (String path : inputPaths) {
				if (!Files.exists(Paths.get(path))) {
					log.warn("PDF 文件不存在，跳过: {}", path);
					continue;
				}

				PdfReader reader = null;
				try {
					reader = new PdfReader(path);
					int pages = reader.getNumberOfPages();
					for (int i = 1; i <= pages; i++) {
						copy.addPage(copy.getImportedPage(reader, i));
					}
					log.debug("合并文件: {} ({} 页)", path, pages);
					mergedCount++;
				} catch (Exception e) {
					log.error("合并文件失败: {}", path, e);
					throw new PoiPdfException("合并失败: " + path, e);
				} finally {
					closeQuietly(reader);
				}
			}

			log.info("PDF 合并成功: {} (共 {} 个文件)", outputPath, mergedCount);

		} catch (Exception e) {
			log.error("PDF 合并失败", e);
			throw new PoiPdfException("PDF 合并失败", e);
		} finally {
			// 3. 必须先关闭 Document（触发 flush），再关闭流
			if (document != null && document.isOpen()) {
				document.close();               // <-- 关键：flush 所有缓存
			}
			// 4. 最后关闭文件流
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.warn("关闭输出流异常", e);
				}
			}
		}
	}

	// ==================== 7. 拆分 PDF（每页一个） ====================
	public static void splitPdf(String inputPath, String outputDir) {
		validateInputFile(inputPath);
		if (outputDir == null || outputDir.trim().isEmpty()) {
			throw new PoiPdfException("输出目录不能为空");
		}
		File dir = new File(outputDir);
		if (!dir.exists() && !dir.mkdirs()) {
			throw new PoiPdfException("创建输出目录失败: " + outputDir);
		}

		PdfReader reader = null;
		try {
			reader = new PdfReader(inputPath);
			int totalPages = reader.getNumberOfPages();
			if (totalPages == 0) throw new PoiPdfException("PDF 无内容页");

			for (int i = 1; i <= totalPages; i++) {
				String outFile = outputDir + "/page_" + i + ".pdf";
				Document doc = null;
				PdfCopy copy = null;
				FileOutputStream fos = null;

				try {
					// 1. 手动打开 fos（不要 try-with-resources）
					fos = new FileOutputStream(outFile);

					// 2. 创建 Document 和 PdfCopy
					doc = new Document(reader.getPageSizeWithRotation(i));
					copy = new PdfCopy(doc, fos);
					doc.open();

					// 3. 复制当前页
					copy.addPage(copy.getImportedPage(reader, i));

					log.debug("拆分第 {} 页 → {}", i, outFile);

				} catch (Exception e) {
					log.error("拆分第 {} 页失败", i, e);
					throw new PoiPdfException("拆分失败: 第 " + i + " 页", e);
				} finally {
					// 4. 必须先 close Document（触发 flush），再 close fos
					if (doc != null && doc.isOpen()) {
						doc.close();  // 关键：flush 所有缓存
					}
					if (fos != null) {
						try {
							fos.close();
						} catch (IOException e) {
							log.warn("关闭输出流异常: {}", outFile, e);
						}
					}
				}
			}

			log.info("PDF 拆分完成: {} 页 → {}", totalPages, outputDir);

		} catch (Exception e) {
			log.error("PDF 拆分失败", e);
			throw new PoiPdfException("PDF 拆分失败", e);
		} finally {
			closeQuietly(reader);
		}
	}

	// ==================== 8. 拆分范围 ====================
	public static void splitPdfByRange(String inputPath, int startPage, int endPage, String outputPath) {
		validateInputFile(inputPath);
		if (startPage < 1) throw new PoiPdfException("起始页必须 ≥ 1");
		if (endPage < startPage) throw new PoiPdfException("结束页不能小于起始页");
		validateOutputPath(outputPath);

		PdfReader reader = null;
		Document document = null;
		PdfCopy copy = null;
		FileOutputStream fos = null;

		try {
			reader = new PdfReader(inputPath);
			int total = reader.getNumberOfPages();
			if (total == 0) throw new PoiPdfException("PDF 无内容");
			int end = Math.min(endPage, total);

			// 1. 手动打开输出流（不要 try-with-resources）
			fos = new FileOutputStream(outputPath);

			// 2. 创建 Document 和 PdfCopy
			document = new Document();
			copy = new PdfCopy(document, fos);
			document.open();

			// 3. 复制指定范围页
			for (int i = startPage; i <= end; i++) {
				copy.addPage(copy.getImportedPage(reader, i));
			}

			log.info("范围拆分成功: {}~{} → {}", startPage, end, outputPath);

		} catch (Exception e) {
			log.error("范围拆分失败", e);
			throw new PoiPdfException("范围拆分失败", e);
		} finally {
			// 4. 必须先 close Document（触发 flush），再 close fos
			if (document != null && document.isOpen()) {
				document.close();  // 关键：flush 所有缓存
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.warn("关闭输出流异常: {}", outputPath, e);
				}
			}
			closeQuietly(reader);
		}
	}

	// ==================== 9. 字节流合并 ====================
	public static byte[] mergePdfsToBytes(List<byte[]> pdfBytesList) {
		if (pdfBytesList == null || pdfBytesList.isEmpty()) {
			throw new PoiPdfException("字节流列表不能为空");
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = null;
		try {
			document = new Document();
			PdfCopy copy = new PdfCopy(document, baos);
			document.open();

			for (int i = 0; i < pdfBytesList.size(); i++) {
				byte[] data = pdfBytesList.get(i);
				if (data == null || data.length == 0) {
					log.warn("第 {} 个字节流为空，跳过", i + 1);
					continue;
				}
				PdfReader reader = null;
				try {
					reader = new PdfReader(data);
					for (int p = 1; p <= reader.getNumberOfPages(); p++) {
						copy.addPage(copy.getImportedPage(reader, p));
					}
				} catch (Exception e) {
					log.error("合并第 {} 个字节流失败", i + 1, e);
					throw new PoiPdfException("字节流合并失败", e);
				} finally {
					closeQuietly(reader);
				}
			}
			log.info("字节流合并成功: {} 个 PDF", pdfBytesList.size());
			return baos.toByteArray();
		} catch (Exception e) {
			log.error("字节流合并异常", e);
			throw new PoiPdfException("字节流合并失败", e);
		} finally {
			closeDocument(document);
		}
	}

	// ==================== 工具方法 ====================
	private static void validateOutputPath(String path) {
		if (path == null || path.trim().isEmpty()) {
			throw new PoiPdfException("输出路径不能为空");
		}
	}

	private static void validateInputFile(String path) {
		if (path == null || path.trim().isEmpty()) {
			throw new PoiPdfException("输入文件路径不能为空");
		}
		if (!Files.exists(Paths.get(path))) {
			throw new PoiPdfException("输入文件不存在: " + path);
		}
	}

	private static void closeDocument(Document doc) {
		if (doc != null && doc.isOpen()) {
			doc.close();
		}
	}

	private static void closeQuietly(PdfReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
				log.debug("关闭 PdfReader 异常", e);
			}
		}
	}
}