
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;



public class Test 
{
	
	
	public static void main(String[] args)
	{
		Map<String, Object> param = new HashMap<String, Object>();
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		param.put("dateEnd", sdf.format(date));
		//获取数据
		List<AllProjectDTO> dataset = projectFacade.findAllProjectForReport(param);
		ExportExcelFromTemplet<AllProjectDTO> ex = new ExportExcelFromTemplet<AllProjectDTO>();
		Workbook workbook = ex.exportExcel(1, dataset, param, "allProject.xlsx");
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment;filename=" + MimeUtility.encodeWord("项目表.xlsx"));
		OutputStream ouputStream = response.getOutputStream();
		workbook.write(ouputStream);
		ouputStream.flush();
		ouputStream.close();
		return null;
	}

	
}
