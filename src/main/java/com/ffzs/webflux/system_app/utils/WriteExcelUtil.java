package com.ffzs.webflux.system_app.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import reactor.core.publisher.Mono;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: ffzs
 * @Date: 2020/8/31 上午9:01
 */

@Slf4j
public class WriteExcelUtil {

    public static List<Field> getFieldsInfo(Class<?> clazz) {

        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>(Arrays.asList(fields));
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            Field[] superFields = superClazz.getDeclaredFields();
            list.addAll(Arrays.asList(superFields));
        }
        return list;
    }

    /**
     * 设置header的风格
     * @param workbook
     * @return
     */
    private static CellStyle buildHeadCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex()); // 下边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex()); // 左边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex()); // 右边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex()); // 上边框
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle buildNormalCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }


    private static Sheet buildDataSheet(Workbook workbook, List<String> header) {
        Sheet sheet = workbook.createSheet();
        // 设置列头宽度
        for (int i=0; i<header.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }
        // 设置默认行高
        sheet.setDefaultRowHeight((short) 400);
        // 构建头单元格样式
        CellStyle cellStyle = buildHeadCellStyle(sheet.getWorkbook());
        // 写入第一行各列的数据
        Row head = sheet.createRow(0);
        for (int i = 0; i < header.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(header.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }


    @SneakyThrows
    public static void writeExcel (String fileName, List<?> objs, Class<?> clazz) {
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8),"iso8859-1");

        Workbook workbook = Objects.requireNonNull(data2Workbook(objs, clazz)).block();
        OutputStream outputStream = new FileOutputStream(fileName);
        if (workbook != null) {
            workbook.write(outputStream);
            workbook.close();
        }
    }

    @SneakyThrows
    public static Mono<Workbook> data2Workbook (List<?> objs, Class<?> clazz) {

        List<Field> fields = getFieldsInfo(clazz);
        List<Method> methods = Stream.of(clazz.getMethods())
                .filter(method -> method.getName().startsWith("get"))
                .collect(Collectors.toList());
        List<String> methodNames = methods.stream()
                .map(Method::getName)
                .collect(Collectors.toList());
        if (fields.isEmpty()) return Mono.empty();
        List<String> header = fields.stream()
                .map(Field::getName)
                .collect(Collectors.toList());

        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook, header);

        CellStyle normalStyle = buildNormalCellStyle(workbook);

        for (int i = 0; i < objs.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Object obj = objs.get(i);
            for (int j = 0; j < header.size(); j++) {
                Cell cell = row.createCell(j);
                var methodName = "get" + header.get(j).substring(0, 1).toUpperCase() + header.get(j).substring(1);
                int index = methodNames.indexOf(methodName);
                if (index != -1) {
                    Method method = methods.get(index);
                    Class<?> fieldType = fields.get(j).getType();
                    Object value = method.invoke(obj);
                    if (value != null && fieldType == LocalDateTime.class) {
                        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String date = df.format((LocalDateTime)value);
                        cell.setCellValue(date);
                    } else {
                        if (value == null) value = "";
                        cell.setCellValue(value.toString());
                    }
                    cell.setCellStyle(normalStyle);
                }
            }
        }

        return Mono.justOrEmpty(workbook);
    }
}
