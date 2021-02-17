package core.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;

import java.io.ByteArrayOutputStream;

public class JasperExportAggregator {

    public static byte[] generateJasperPdf(JasperPrint print) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(print, outputStream);

        return outputStream.toByteArray();
    }

    public static byte[] generateJasperHtml(JasperPrint print) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        HtmlExporter exporter = new HtmlExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    public static byte[] generateJasperWord(JasperPrint print) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRDocxExporter exporter = new JRDocxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    public static byte[] generateJasperExcel(JasperPrint print) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        JRXlsxExporter exporter = new JRXlsxExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    public static byte[] generateJasperCsv(JasperPrint print, String csvDelimiter) throws JRException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        SimpleCsvMetadataExporterConfiguration configuration = new SimpleCsvMetadataExporterConfiguration();
        configuration.setFieldDelimiter(csvDelimiter);

        JRCsvExporter exporter = new JRCsvExporter();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

}
