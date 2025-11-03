package com.pig4cloud.pig.common.poi.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfReader;
import com.pig4cloud.pig.common.poi.utils.PdfUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/pdf")
public class PdfController {

    @GetMapping("/text")
    public void downloadTextPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=user-agreement.pdf");

        byte[] pdfBytes = PdfUtil.textPdfToBytes();
        response.getOutputStream().write(pdfBytes);
    }

    @GetMapping("/html")
    public void downloadHtmlPdf(HttpServletResponse response) throws Exception {
        String html = PdfUtil.getSampleHtml();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfUtil.htmlToPdf(html, null); // 临时写文件
        // 或直接用 renderer.createPDF(baos)
        // 省略...
    }

	/**
	 * 下载合并后的 PDF
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/download/merged")
	public void downloadMergedPdf(HttpServletResponse response) throws Exception {
		List<String> files = Arrays.asList("text.pdf", "table.pdf");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document();
		PdfCopy copy = new PdfCopy(document, baos);
		document.open();

		for (String path : files) {
			PdfReader reader = new PdfReader(path);
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				copy.addPage(copy.getImportedPage(reader, i));
			}
			reader.close();
		}
		document.close();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=merged-report.pdf");
		response.getOutputStream().write(baos.toByteArray());
	}

}