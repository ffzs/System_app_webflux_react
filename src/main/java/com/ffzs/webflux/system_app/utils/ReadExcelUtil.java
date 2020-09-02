package com.ffzs.webflux.system_app.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author: ffzs
 * @Date: 2020/8/31 上午8:56
 */


@Slf4j
public class ReadExcelUtil {

    private static final String XLS = "xls";
    private static final String XLSX = "xlsx";

    public static Workbook getWorkbook(InputStream inputStream, String fileType) throws IOException {
        Workbook workbook = null;
        if (fileType.equalsIgnoreCase(XLS)) {
            workbook = new HSSFWorkbook(inputStream);
        } else if (fileType.equalsIgnoreCase(XLSX)) {
            workbook = new XSSFWorkbook(inputStream);
        }
        return workbook;
    }

    @SneakyThrows
    public static List<Object> readExcel (MultipartFile file, int sheetId, Class<?> clazz) {
        return readExcel(file.getInputStream(), file.getOriginalFilename(), sheetId, clazz);

    }

    @SneakyThrows
    public static List<Object> readExcel (String fileName, int sheetId, Class<?> clazz) {
        InputStream inputStream = new FileInputStream(new File(fileName));
        return readExcel(inputStream, fileName, sheetId, clazz);

    }

    public static List<Object> readExcel (InputStream inputStream, String fileName, int sheetId, Class<?> clazz) throws IOException {

        Workbook workbook = null;

        try {
            if (fileName == null || fileName.isEmpty() || fileName.lastIndexOf(".") < 0) {
                log.warn("解析Excel失败，因为获取到的Excel文件名非法！");
                return null;
            }

            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
            // 获取Excel文件
            workbook = getWorkbook(inputStream, fileType);

            // 读取excel中的数据
            return parseExcel(fileName, workbook, sheetId, clazz);
        } catch (Exception e) {
            log.warn("错误类型 {}， 错位内容 {}", e.getClass(), e.getMessage());
//            throw e;
            return null;
        } finally {
            try {
                if (null != workbook) {
                    workbook.close();
                }
            } catch (Exception e) {
                log.warn("关闭数据流出错！错误信息：" + e.getMessage());
                return null;
            }
        }
    }

    /**
     * 默认sheet的第一行为对应的类的名称
     * @param workbook
     */
    @SneakyThrows
    public static List<Object> parseExcel (String fileName, Workbook workbook, int sheetId, Class<?> clazz) {
        Sheet sheet = workbook.getSheetAt(sheetId);

        if (sheet == null) {
            log.warn("没有第{}个sheet", sheetId+1);
            return null;
        }

        Row header = sheet.getRow(0);
        if (header == null) {
            log.warn("解析失败表头没有数据");
            return null;
        }
        int columnNum = header.getPhysicalNumberOfCells();
        String[] properties = new String[columnNum];
        for (int i = 0; i < properties.length; i++) {
            properties[i] = header.getCell(i).toString();
        }
//        log.info("{}:{}", columnNum, properties);

        int startRowNum = 1;
        int endRowNum = sheet.getPhysicalNumberOfRows();

        Map<String, Method> methods = Stream.of(clazz.getMethods())
                .filter(method -> method.getName().startsWith("set"))
                .collect(Collectors.toMap(Method::getName, it->it));

        Map<String, Type> fieldMap = Stream.of(clazz.getDeclaredFields())
                .collect(Collectors.toMap(Field::getName, Field::getType));

        var objs = new ArrayList<>();
        for (int i = startRowNum; i <= endRowNum; ++i) {
            Row row = sheet.getRow(i);

            if (row == null) continue;

            Object obj = clazz.getDeclaredConstructor().newInstance();
            try {
                for (int j = 0; j < columnNum; j++) {
                    var methodName = "set" + properties[j].substring(0, 1).toUpperCase() + properties[j].substring(1);
                    if (methods.containsKey(methodName)){
                        Method method = methods.get(methodName);
                        Cell cell = row.getCell(j);
                        Type type = fieldMap.get(properties[j]);
//                        log.error("{}; {}",cell, type);
                        method.invoke(obj,cell2Obj(cell, type));
                    }
                }
            } catch (Exception e) {
                log.error("{}文件的第{}行解析出现错误，错误类型为{}，错误内容{}", fileName, i, e.getClass(), e.getMessage());
//                throw e;
            }
            objs.add(obj);
        }
        return objs;
    }

    private static Object cell2Obj(Cell cell, Type type) {
        if (cell == null) return null;
        Object value = null;
        switch (cell.getCellType()) {
            case NUMERIC:
                Double doubleValue = cell.getNumericCellValue();
                DecimalFormat decimalFormat = new DecimalFormat("0");
                value = decimalFormat.format(doubleValue);
                if (type.getTypeName().equals("long")) value = Long.valueOf((String) value);
                break;
            case STRING:
                String str = cell.getStringCellValue().trim();
                if (str.equals("")) break;
                switch (type.getTypeName()) {
                    case "long":
                        value = Long.valueOf(str);
                        break;
                    case "java.time.LocalDateTime":
                        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        value = LocalDateTime.parse(str, dateTimeFormatter);
                        break;
                    case "java.util.List":
                        value = Arrays.stream(str.substring(1, str.length() - 1).split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        break;
                    default:
                        value = str;
                        break;
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case FORMULA:
                value = cell.getCellFormula();
                break;
            case BLANK:
            case _NONE:
                if (type.getTypeName().equals("java.util.List")) value = new ArrayList<>();
                break;
            default:
                break;
        }
        return value;
    }
}
