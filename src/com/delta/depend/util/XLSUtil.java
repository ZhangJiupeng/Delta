package com.delta.depend.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * The {@code XLSUtil} class give simple options to read/write xls
 * documents.<br/>[need poi.jar]
 *
 * @author Jim Zhang
 * @see org.apache.poi
 * @since Delta1.0
 */
@SuppressWarnings("ALL")
public final class XLSUtil {
    private static HSSFWorkbook workbook;
    private static HSSFSheet sheet;
    private static HSSFCellStyle style;

    private XLSUtil() {
    }

    static {
        workbook = new HSSFWorkbook();
        // Sheet Init.
        sheet = workbook.createSheet();

        // CellStyle Init.
        style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // Border Settings
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

        // FontStyle Init.
        HSSFFont font = workbook.createFont();
        font.setFontName("Microsoft YaHei");
        font.setBoldweight((short) 100);
        font.setFontHeight((short) 200);
        font.setColor(HSSFColor.BLACK.index);
        style.setFont(font);

        // Word Wrap Config.
        style.setWrapText(true);

        // Merge cell if needed. (startRow, endRow, startCol, endCol)
        /*sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 0));*/

    }

    /**
     * transform object array and save as .xls file.
     * (private variable is auto ignored)
     *
     * @param filePath destination path
     * @param objects  objects need to be written
     * @param withHead write table head or not
     * @throws IOException, IllegalAccessException
     */
    public static void write(String filePath, Object[] objects, boolean withHead)
            throws IOException, IllegalAccessException {
        if (objects == null) {
            return;
        }
        int fieldCounter = 0;
        Field[] fields = objects[0].getClass().getDeclaredFields();
        StringBuilder sbd = new StringBuilder();
        if (withHead) {
            for (Field field : fields) {
                if (field.getModifiers() == 2) continue;
                sbd.append(field.getName()).append("\t");
                fieldCounter++;
            }
            if (fieldCounter > 0) {
                sbd.setLength(sbd.length() - 1);
            }
        }
        for (Object o : objects) {
            sbd.append("\n");
            for (Field field : fields) {
                if (field.getModifiers() == Modifier.PRIVATE) continue;
                field.setAccessible(true);
                sbd.append(field.get(o)).append("\t");
            }
            sbd.setLength(sbd.length() - 1);
        }
        if (!withHead && sbd.length() > 0) write(filePath, sbd.substring(1));
        else write(filePath, sbd.toString());
    }

    /**
     * simple parse of plainText and save as .xls file.
     *
     * @param filePath give a file path on load
     * @param plainText plain pattern string
     * @throws IOException
     */
    public static void write(String filePath, String plainText) throws IOException {
        if (plainText == null || plainText.length() == 0) {
            FileOutputStream os = new FileOutputStream(filePath);
            workbook.write(os);
            os.close();
            return;
        }
        int rowPos = 0;
        int colPos;

        for (String rowText : plainText.split("\n")) {
            HSSFRow row = sheet.createRow(rowPos++);
            colPos = 0;
            for (String colTest : rowText.split("\t")) {
                sheet.setColumnWidth(colPos, 3000);
                HSSFCell cell = row.createCell(colPos++);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                cell.setCellStyle(style);
                cell.setCellValue(colTest);
            }
        }
        FileOutputStream os = new FileOutputStream(filePath);
        workbook.write(os);
        os.close();
    }

    /**
     * read from .xls file and filled into string array.
     *
     * @param filePath give a file path on load
     * @throws IOException
     */
    public static String[][] read(String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        HSSFWorkbook workbook = new HSSFWorkbook(is);
        HSSFSheet sheet = workbook.getSheetAt(0);
        String[][] content = new String[sheet.getLastRowNum() + 1][sheet.getRow(0).getLastCellNum()];
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            for (int j = 0; j < sheet.getRow(i).getLastCellNum(); j++) {
                switch (sheet.getRow(i).getCell(j).getCellType()) {
                    case HSSFCell.CELL_TYPE_STRING:
                        content[i][j] = sheet.getRow(i).getCell(j).getStringCellValue();
                        break;
                    case HSSFCell.CELL_TYPE_BLANK:
                        content[i][j] = "";
                        break;
                    case HSSFCell.CELL_TYPE_BOOLEAN:
                        content[i][j] = sheet.getRow(i).getCell(j).getBooleanCellValue() ? "true" : "false";
                        break;
                    case HSSFCell.CELL_TYPE_ERROR:
                        content[i][j] = String.valueOf(sheet.getRow(i).getCell(j).getErrorCellValue());
                        break;
                    case HSSFCell.CELL_TYPE_FORMULA:
                        content[i][j] = sheet.getRow(i).getCell(j).getCellFormula();
                        break;
                    case HSSFCell.CELL_TYPE_NUMERIC:
                        content[i][j] = String.valueOf(sheet.getRow(i).getCell(j).getNumericCellValue());
                        break;
                }
            }
        }
        return content;
    }

}
