package com.api.produtos.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.api.produtos.domain.relatorio.RelatorioVendaDetalhadoComTotal;
import com.api.produtos.domain.relatorio.RelatorioVendaDia;
import com.api.produtos.domain.relatorio.RelatorioVendaDiaItemDTO;
import com.api.produtos.domain.venda.VendaRepository;
import com.api.produtos.services.RelatorioService;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private RelatorioService relatorioService;

    private static final DateTimeFormatter DATA_HORA_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @GetMapping("/detalhado")
    public ResponseEntity<RelatorioVendaDetalhadoComTotal> obterRelatorioVendasDetalhado(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {

        List<RelatorioVendaDia> vendas = relatorioService.gerarRelatorioVendasDetalhadoPorPeriodo(dataInicio, dataFim);
        RelatorioVendaDetalhadoComTotal relatorio = new RelatorioVendaDetalhadoComTotal(vendas);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/detalhado/dia")
    public ResponseEntity<RelatorioVendaDetalhadoComTotal> obterRelatorioVendasDetalhadoDoDia(
            @RequestParam("data") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<RelatorioVendaDia> vendas = relatorioService.gerarRelatorioVendasDetalhadoDoDia(data);
        RelatorioVendaDetalhadoComTotal relatorio = new RelatorioVendaDetalhadoComTotal(vendas);
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/detalhado/excel")
    public void exportarRelatorioDetalhadoExcel(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletResponse response) throws IOException {

        List<RelatorioVendaDia> relatorio = relatorioService.gerarRelatorioVendasDetalhadoPorPeriodo(dataInicio, dataFim);
        java.math.BigDecimal valorTotal = relatorio.stream()
            .flatMap(venda -> venda.getItens().stream())
            .map(com.api.produtos.domain.relatorio.RelatorioVendaDiaItemDTO::getSubtotal)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Vendas Detalhadas");

        // Criar estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        CellStyle totalStyle = workbook.createCellStyle();
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);
        totalStyle.setFont(totalFont);
        totalStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        totalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        totalStyle.setBorderTop(BorderStyle.THICK);
        totalStyle.setBorderBottom(BorderStyle.THICK);
        totalStyle.setBorderLeft(BorderStyle.THIN);
        totalStyle.setBorderRight(BorderStyle.THIN);
        totalStyle.setAlignment(HorizontalAlignment.CENTER);
        totalStyle.setVerticalAlignment(org.apache.poi.ss.usermodel.VerticalAlignment.CENTER);

        // Cabeçalho
        Row header = sheet.createRow(0);
        header.setHeight((short) 500); // Altura do cabeçalho
        String[] colunas = {"ID Venda", "Data/Hora", "Método Pagamento", "Produto", "Quantidade", "Valor Unitário", "Subtotal"};
        for (int i = 0; i < colunas.length; i++) {
            org.apache.poi.ss.usermodel.Cell cell = header.createCell(i);
            cell.setCellValue(colunas[i]);
            cell.setCellStyle(headerStyle);
        }

        // Dados
        int rowNum = 1;
        for (RelatorioVendaDia venda : relatorio) {
            String dataHoraFormatada = venda.getDataHora().format(DATA_HORA_FORMATTER);
            String metodoPagamento = venda.getMetodoPagamento() != null ? venda.getMetodoPagamento().name() : "Não finalizada";
            if (venda.getItens().isEmpty()) {
                Row row = sheet.createRow(rowNum++);
                row.setHeight((short) 300);
                
                org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
                cell0.setCellValue(venda.getIdVenda());
                cell0.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
                cell1.setCellValue(dataHoraFormatada);
                cell1.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
                cell2.setCellValue(metodoPagamento);
                cell2.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
                cell3.setCellValue("Sem itens");
                cell3.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
                cell4.setCellValue(0);
                cell4.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
                cell5.setCellValue(0.0);
                cell5.setCellStyle(dataStyle);
                
                org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
                cell6.setCellValue(0.0);
                cell6.setCellStyle(dataStyle);
            } else {
                for (RelatorioVendaDiaItemDTO item : venda.getItens()) {
                    Row row = sheet.createRow(rowNum++);
                    row.setHeight((short) 300);
                    
                    org.apache.poi.ss.usermodel.Cell cell0 = row.createCell(0);
                    cell0.setCellValue(venda.getIdVenda());
                    cell0.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell1 = row.createCell(1);
                    cell1.setCellValue(dataHoraFormatada);
                    cell1.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell2 = row.createCell(2);
                    cell2.setCellValue(metodoPagamento);
                    cell2.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell3 = row.createCell(3);
                    cell3.setCellValue(item.getNomeDoProduto());
                    cell3.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell4 = row.createCell(4);
                    cell4.setCellValue(item.getQuantidade());
                    cell4.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell5 = row.createCell(5);
                    cell5.setCellValue(item.getValorUnitario().doubleValue());
                    cell5.setCellStyle(dataStyle);
                    
                    org.apache.poi.ss.usermodel.Cell cell6 = row.createCell(6);
                    cell6.setCellValue(item.getSubtotal().doubleValue());
                    cell6.setCellStyle(dataStyle);
                }
            }
        }

        // Linha de total
        Row totalRow = sheet.createRow(rowNum);
        totalRow.setHeight((short) 400);
        
        for (int i = 0; i < 5; i++) {
            org.apache.poi.ss.usermodel.Cell cell = totalRow.createCell(i);
            cell.setCellStyle(totalStyle);
        }
        
        org.apache.poi.ss.usermodel.Cell totalLabel = totalRow.createCell(5);
        totalLabel.setCellValue("TOTAL");
        totalLabel.setCellStyle(totalStyle);
        
        org.apache.poi.ss.usermodel.Cell totalValue = totalRow.createCell(6);
        totalValue.setCellValue(valorTotal.doubleValue());
        totalValue.setCellStyle(totalStyle);

        // Auto-dimensionar colunas
        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
            // Adicionar um pouco de espaço extra
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = String.format("relatorio_vendas_%s_a_%s.xlsx", 
            dataInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            dataFim.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/detalhado/pdf")
    public void exportarRelatorioDetalhadoPdf(
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletResponse response) throws IOException {

        List<RelatorioVendaDia> relatorio = relatorioService.gerarRelatorioVendasDetalhadoPorPeriodo(dataInicio, dataFim);
        java.math.BigDecimal valorTotal = relatorio.stream()
            .flatMap(venda -> venda.getItens().stream())
            .map(com.api.produtos.domain.relatorio.RelatorioVendaDiaItemDTO::getSubtotal)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        response.setContentType("application/pdf");
        String fileName = String.format("relatorio_vendas_%s_a_%s.pdf", 
            dataInicio.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
            dataFim.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Cores
            Color headerColor = new DeviceRgb(31, 73, 125); // Azul escuro
            Color totalColor = new DeviceRgb(217, 217, 217); // Cinza claro
            Color borderColor = new DeviceRgb(0, 0, 0); // Preto

            // Bordas
            Border thinBorder = new SolidBorder(borderColor, 0.5f);
            Border thickBorder = new SolidBorder(borderColor, 1.5f);

            // Criar tabela
            Table table = new Table(new float[]{1, 2, 2, 3, 1, 2, 2});
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
            table.setMarginTop(20);
            table.setMarginBottom(20);

            // Cabeçalho
            String[] headers = {"ID", "Data/Hora", "Pagamento", "Produto", "Qtd", "Valor Unit.", "Subtotal"};
            for (String header : headers) {
                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph(header))
                    .setBackgroundColor(headerColor)
                    .setFontColor(com.itextpdf.kernel.colors.ColorConstants.WHITE)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
                    .setBorder(thinBorder)
                    .setPadding(8);
                table.addHeaderCell(cell);
            }

            // Dados
            for (RelatorioVendaDia venda : relatorio) {
                String dataHoraFormatada = venda.getDataHora().format(DATA_HORA_FORMATTER);
                String metodoPagamento = venda.getMetodoPagamento() != null ? venda.getMetodoPagamento().name() : "Não finalizada";
                if (venda.getItens().isEmpty()) {
                    // Venda sem itens
                    table.addCell(createCell(String.valueOf(venda.getIdVenda()), thinBorder));
                    table.addCell(createCell(dataHoraFormatada, thinBorder));
                    table.addCell(createCell(metodoPagamento, thinBorder));
                    table.addCell(createCell("Sem itens", thinBorder));
                    table.addCell(createCell("0", thinBorder));
                    table.addCell(createCell("0", thinBorder));
                    table.addCell(createCell("0", thinBorder));
                } else {
                    // Venda com itens
                    for (RelatorioVendaDiaItemDTO item : venda.getItens()) {
                        table.addCell(createCell(String.valueOf(venda.getIdVenda()), thinBorder));
                        table.addCell(createCell(dataHoraFormatada, thinBorder));
                        table.addCell(createCell(metodoPagamento, thinBorder));
                        table.addCell(createCell(item.getNomeDoProduto(), thinBorder));
                        table.addCell(createCell(String.valueOf(item.getQuantidade()), thinBorder));
                        table.addCell(createCell(String.valueOf(item.getValorUnitario()), thinBorder));
                        table.addCell(createCell(String.valueOf(item.getSubtotal()), thinBorder));
                    }
                }
            }

            // Linha de total
            for (int i = 0; i < 5; i++) {
                table.addCell(createCell("", thickBorder));
            }
            table.addCell(createTotalCell("TOTAL", thickBorder));
            table.addCell(createTotalCell(String.valueOf(valorTotal), thickBorder));

            document.add(table);
        }
    }

    private com.itextpdf.layout.element.Cell createCell(String text, Border border) {
        return new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(text))
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setBorder(border)
            .setPadding(6);
    }

    private com.itextpdf.layout.element.Cell createTotalCell(String text, Border border) {
        return new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(text))
            .setBackgroundColor(new DeviceRgb(217, 217, 217))
            .setBold()
            .setTextAlignment(TextAlignment.CENTER)
            .setVerticalAlignment(com.itextpdf.layout.properties.VerticalAlignment.MIDDLE)
            .setBorder(border)
            .setPadding(8);
    }
}
    