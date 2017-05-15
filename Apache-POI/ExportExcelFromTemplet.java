
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExportExcelFromTemplet<T>
{
	public Workbook exportExcel(Integer reportType, Collection<T> dataset, Map<String, Object> param, String filepath)
	{

		return exportExcel(dataset, "yyyy/MM/dd", reportType, param, filepath);
	}

	/**
	 * 读取Excel模板(兼容xlsx和xls)，利用了JAVA的反射机制，可以将放置在JAVA集合中并且符合一定条件的数据以EXCEL 的形式输出。
	 * 
	 * @param dataset
	 *            需要显示的数据集合,集合中一定要放置符合javabean风格的类的对象。javabean属性的先后顺序，注意要与模板保持一致
	 *            。
	 * @param pattern
	 *            如果有时间数据，设定输出格式
	 * @param reportType
	 *            报表模板类型
	 * @param param
	 *            报表头参数
	 * @param filepath
	 *            模板路径
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Workbook exportExcel(Collection<T> dataset, String pattern, Integer reportType, Map<String, Object> param,
			String filepath)
	{

		if (filepath == null && "".equals(filepath))
		{
			return null;
		}
		Workbook wb = null;
		Sheet sheet = null;
		Row row;
		Cell cell;
		try
		{
			wb = WorkbookFactory.create(new FileInputStream(filepath));
		}
		catch (FileNotFoundException e1)
		{
			e1.printStackTrace();
		}
		catch (InvalidFormatException e1)
		{
			e1.printStackTrace();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		sheet = wb.getSheetAt(0);
		int index = 0;
		switch (reportType)
		{
			case 1:
				cell = sheet.getRow(0).getCell(0);
				cell.setCellValue("项目表（" + param.get("dateEnd") + "）");
				index = 2;
				break;

			case 2:
				cell = sheet.getRow(0).getCell(0);
				cell.setCellValue("项目表2\r\n（打印日期：" + param.get("dateEnd") + "）");
			  index = 2;
				break;
			default:
				break;
		}
		// 生成数据样式
		CellStyle dateStyle = wb.createCellStyle();
		dateStyle.setWrapText(true);
		dateStyle.setBorderBottom(CellStyle.BORDER_THIN);
		dateStyle.setBorderLeft(CellStyle.BORDER_THIN);
		dateStyle.setBorderRight(CellStyle.BORDER_THIN);
		dateStyle.setBorderTop(CellStyle.BORDER_THIN);
		if(dataset == null){
			return wb;
		}
		// 遍历集合数据，产生数据行
		Iterator<T> it = dataset.iterator();
		while (it.hasNext())
		{
			index++;
			row = sheet.createRow(index);
			T t = (T) it.next();
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			Field[] fields = t.getClass().getDeclaredFields();
			for (short i = 0; i < fields.length; i++)
			{
				cell = row.createCell(i);
				Field field = fields[i];
				String fieldName = field.getName();
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try
				{
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					if (value instanceof Date)
					{
						Date date = (Date) value;
						SimpleDateFormat sdf = new SimpleDateFormat(pattern);
						textValue = sdf.format(date);
					}
					else
					{
						// 其它数据类型都当作字符串简单处理
						if (value != null)
						{
							textValue = value.toString();
						}

					}
					// 如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if (textValue != null)
					{
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if (matcher.matches())
						{
							// 是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						}
						else
						{
							cell.setCellValue(textValue);
						}
					}
					cell.setCellStyle(dateStyle);
					//
					if (reportType == 2 && i == 9)
					{
						String tempNum = String.valueOf(index + 1);
						String rate = "TEXT(H" + tempNum + "/(I" + tempNum + "+H" + tempNum + "),\"0.0%\")";
						cell.setCellFormula(rate);
					}
					if (reportType == 1 && i == 13)
					{
						String tempNum = String.valueOf(index + 1);
						String rate = "TEXT(J" + tempNum + "/(J" + tempNum + "+K" + tempNum + "),\"0.0%\")";
						cell.setCellFormula(rate);
					}
				}
				catch (SecurityException e)
				{
					e.printStackTrace();
				}
				catch (NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
				finally
				{
					// 清理资源
				}
			}
		}
		return wb;

	}
}
