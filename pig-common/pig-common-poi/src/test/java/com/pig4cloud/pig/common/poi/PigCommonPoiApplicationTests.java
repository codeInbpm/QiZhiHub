package com.pig4cloud.pig.common.poi;

import com.pig4cloud.pig.common.poi.utils.PdfUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PigCommonPoiApplicationTests {

	public static final String ROOT_PATH = "./pdf/";

	@Test
	void genPdf() {
		PdfUtil.createTextPdf(ROOT_PATH + "text.pdf");
		PdfUtil.createTablePdf(ROOT_PATH + "table.pdf");
		PdfUtil.htmlToPdf(PdfUtil.getSampleHtml(), ROOT_PATH + "html.pdf");
		PdfUtil.createPdfWithHeaderFooter(ROOT_PATH + "header-footer.pdf");
	}

	@Test
	void mergePdfs() {
		List<String> files = new ArrayList<>();
		files.add(ROOT_PATH + "text.pdf");
		files.add(ROOT_PATH + "table.pdf");

		// 1. 合并
		PdfUtil.mergePdfs(files, ROOT_PATH + "merged.pdf");

		// 2. 拆分（每页一个）
		PdfUtil.splitPdf(ROOT_PATH + "merged.pdf", "split_pages");

		// 3. 拆分指定页（第 1-2 页）
		PdfUtil.splitPdfByRange(ROOT_PATH + "merged.pdf", 1, 2, ROOT_PATH + "range_1_2.pdf");
	}

}
