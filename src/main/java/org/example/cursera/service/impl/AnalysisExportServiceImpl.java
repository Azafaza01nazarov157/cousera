package org.example.cursera.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.cursera.domain.dtos.CourseStatisticsDto;
import org.example.cursera.domain.dtos.errors.ErrorDto;
import org.example.cursera.domain.entity.Course;
import org.example.cursera.domain.entity.User;
import org.example.cursera.domain.repository.CourseRepository;
import org.example.cursera.exeption.NotFoundException;
import org.example.cursera.service.analysis.AnalysisExportService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisExportServiceImpl implements AnalysisExportService {

    private final CourseRepository courseRepository;

    @Override
    public List<CourseStatisticsDto> getSubscribersAnalysis(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException(new ErrorDto("404", "Курс с ID " + courseId + " не найден")));

        return course.getSubscribers().stream()
                .map(user -> mapToStatisticsDto(user, course))
                .collect(Collectors.toList());
    }

    @Override
    public void exportAnalysisToExcel(Long courseId, HttpServletResponse response) throws IOException {
        List<CourseStatisticsDto> analysis = getSubscribersAnalysis(courseId);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Subscribers Analysis");
            sheet.setDefaultColumnWidth(20);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(true);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            createHeaderRow(sheet, headerStyle, new String[]{
                    "Email", "Course Name",
                    "Completed Lessons", "Total Lessons",
                    "Completion Percentage", "Average Test Score",
                    "Test Success Percentage"
            });

            int rowNum = 1;
            for (CourseStatisticsDto dto : analysis) {
                Row row = sheet.createRow(rowNum++);
                createDataRow(row, dataStyle, dto);
            }

            for (int i = 0; i < 7; i++) {
                sheet.autoSizeColumn(i);
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=subscribers_analysis.xlsx");
            workbook.write(response.getOutputStream());
        }
    }

    private void createHeaderRow(Sheet sheet, CellStyle style, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void createDataRow(Row row, CellStyle style, CourseStatisticsDto dto) {
        Cell cell0 = row.createCell(0);
        cell0.setCellValue(dto.getEmail());
        cell0.setCellStyle(style);

        Cell cell1 = row.createCell(1);
        cell1.setCellValue(dto.getCourseName());
        cell1.setCellStyle(style);

        Cell cell2 = row.createCell(2);
        cell2.setCellValue(dto.getCompletedLessons());
        cell2.setCellStyle(style);

        Cell cell3 = row.createCell(3);
        cell3.setCellValue(dto.getTotalLessons());
        cell3.setCellStyle(style);

        Cell cell4 = row.createCell(4);
        cell4.setCellValue(dto.getCompletionPercentage());
        cell4.setCellStyle(style);

        Cell cell5 = row.createCell(5);
        cell5.setCellValue(dto.getAverageTestScore());
        cell5.setCellStyle(style);

        Cell cell6 = row.createCell(6);
        cell6.setCellValue(dto.getTestSuccessPercentage());
        cell6.setCellStyle(style);
    }

    @Override
    public ByteArrayInputStream exportSubscribersAnalysisFromDatabase(Long courseId) throws IOException {
        return createExcelStream(getSubscribersAnalysis(courseId));
    }

    private ByteArrayInputStream createExcelStream(List<CourseStatisticsDto> analysis) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Subscribers Analysis");
            sheet.setDefaultColumnWidth(20);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setWrapText(true);

            createHeaderRow(sheet, headerStyle, new String[]{
                    "Email", "Course Name",
                    "Completed Lessons", "Total Lessons",
                    "Completion Percentage", "Average Test Score",
                    "Test Success Percentage"
            });

            int rowNum = 1;
            for (CourseStatisticsDto dto : analysis) {
                Row row = sheet.createRow(rowNum++);
                createDataRow(row, dataStyle, dto);
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                return new ByteArrayInputStream(out.toByteArray());
            }
        }
    }

    private CourseStatisticsDto mapToStatisticsDto(User user, Course course) {
        int totalLessons = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .toList()
                .size();

        int completedLessons = (int) course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .filter(lesson -> lesson.getTopics().stream()
                        .allMatch(topic -> topic.getTests().stream()
                                .allMatch(test -> test.getTestResults().stream()
                                        .anyMatch(result -> result.getUser().equals(user) && result.isCorrect()))))
                .count();

        double completionPercentage = totalLessons > 0 ? (completedLessons * 100.0) / totalLessons : 0.0;

        double averageTestScore = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .flatMap(lesson -> lesson.getTopics().stream())
                .flatMap(topic -> topic.getTests().stream())
                .flatMap(test -> test.getTestResults().stream())
                .filter(result -> result.getUser().equals(user))
                .mapToInt(result -> result.getScore())
                .average()
                .orElse(0.0);

        long totalTests = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .flatMap(lesson -> lesson.getTopics().stream())
                .flatMap(topic -> topic.getTests().stream())
                .count();

        long successfulTests = course.getModules().stream()
                .flatMap(module -> module.getLessons().stream())
                .flatMap(lesson -> lesson.getTopics().stream())
                .flatMap(topic -> topic.getTests().stream())
                .flatMap(test -> test.getTestResults().stream())
                .filter(result -> result.getUser().equals(user) && result.isCorrect())
                .count();

        double testSuccessPercentage = totalTests > 0 ? (successfulTests * 100.0) / totalTests : 0.0;

        return CourseStatisticsDto.builder()
                .email(user.getEmail())
                .courseName(course.getName())
                .totalLessons(totalLessons)
                .completedLessons(completedLessons)
                .completionPercentage(completionPercentage)
                .averageTestScore(averageTestScore)
                .testSuccessPercentage(testSuccessPercentage)
                .build();
    }
}
