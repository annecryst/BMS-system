/*
 * Certificate Print Preview Frame
 * Opens after a certificate is saved; fills the matching .docx template
 * with the applicant's name, then sends to the selected printer.
 */
package Frontend.forms;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;

public class CertificatePrintFrame extends JFrame {

    private final String certType;
    private final String applicantName;
    private final String purpose;
    private final String issuedBy;
    private final String dateIssued;
    private JComboBox<String> printerCombo;

    public CertificatePrintFrame(String certType, String applicantName,
                                  String purpose, String issuedBy, String dateIssued) {
        this.certType      = certType      != null ? certType      : "";
        this.applicantName = applicantName != null ? applicantName : "";
        this.purpose       = purpose       != null ? purpose       : "";
        this.issuedBy      = issuedBy      != null ? issuedBy      : "";
        this.dateIssued    = dateIssued    != null ? dateIssued    : "";
        buildUI();
        loadPrinters();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void buildUI() {
        setTitle("Certificate – Print Preview");
        setSize(500, 430);
        setResizable(false);
        setLayout(new BorderLayout(0, 0));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── HEADER ──────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 65, 217));
        header.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel certLbl = new JLabel(this.certType.toUpperCase());
        certLbl.setForeground(Color.WHITE);
        certLbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JLabel subLbl = new JLabel("Print Preview");
        subLbl.setForeground(new Color(170, 195, 255));
        subLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        header.add(certLbl, BorderLayout.NORTH);
        header.add(subLbl,  BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // ── BODY (preview rows) ──────────────────────────────────────────────
        JPanel body = new JPanel();
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(18, 28, 10, 28));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        body.add(makeRow("Applicant Name :", this.applicantName, true));
        body.add(Box.createVerticalStrut(8));
        body.add(makeRow("Certificate Type:", this.certType,      false));
        body.add(Box.createVerticalStrut(8));
        body.add(makeRow("Purpose         :", this.purpose,       false));
        body.add(Box.createVerticalStrut(8));
        body.add(makeRow("Issued By       :", this.issuedBy,      false));
        body.add(Box.createVerticalStrut(8));
        body.add(makeRow("Date Issued     :", this.dateIssued,    false));

        add(body, BorderLayout.CENTER);

        // ── FOOTER (printer + buttons) ──────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout(10, 8));
        footer.setBackground(new Color(245, 247, 250));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(215, 215, 215)),
            BorderFactory.createEmptyBorder(12, 22, 14, 22)
        ));

        JPanel printerRow = new JPanel(new BorderLayout(8, 0));
        printerRow.setOpaque(false);
        JLabel printerLbl = new JLabel("Printer:");
        printerLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        printerLbl.setPreferredSize(new Dimension(65, 30));
        printerCombo = new JComboBox<>();
        printerCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        printerRow.add(printerLbl,  BorderLayout.WEST);
        printerRow.add(printerCombo, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        JButton closeBtn = new JButton("Close");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        closeBtn.setPreferredSize(new Dimension(90, 32));

        JButton printBtn = new JButton("Print");
        printBtn.setBackground(new Color(30, 65, 217));
        printBtn.setForeground(Color.WHITE);
        printBtn.setOpaque(true);
        printBtn.setBorderPainted(false);
        printBtn.setFocusPainted(false);
        printBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        printBtn.setPreferredSize(new Dimension(90, 32));
        printBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        closeBtn.addActionListener(e -> dispose());
        printBtn.addActionListener(e -> printCertificate());

        btnRow.add(closeBtn);
        btnRow.add(printBtn);

        footer.add(printerRow, BorderLayout.CENTER);
        footer.add(btnRow,     BorderLayout.SOUTH);
        add(footer, BorderLayout.SOUTH);
    }

    private JPanel makeRow(String label, String value, boolean highlight) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setPreferredSize(new Dimension(145, 26));

        String display = (value == null || value.isBlank()) ? "—" : value;
        JLabel val = new JLabel(display);
        val.setFont(highlight
            ? new Font("Segoe UI", Font.BOLD, 14)
            : new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(highlight ? new Color(30, 65, 217) : new Color(50, 50, 50));
        val.setToolTipText(display);

        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.CENTER);
        return row;
    }

    private void loadPrinters() {
        PrintService defaultPrinter = PrintServiceLookup.lookupDefaultPrintService();
        PrintService[] services     = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService ps : services) printerCombo.addItem(ps.getName());
        if (defaultPrinter != null) printerCombo.setSelectedItem(defaultPrinter.getName());
        if (printerCombo.getItemCount() == 0) printerCombo.addItem("No printers found");
    }

    private void printCertificate() {
        try {
            File modified = fillTemplate(certType, applicantName);
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.PRINT)) {
                desktop.print(modified);
            } else {
                desktop.open(modified);
                JOptionPane.showMessageDialog(this,
                    "The document has been opened.\nPlease use the application's Print menu to print.",
                    "Print", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error preparing document for printing:\n" + ex.getMessage(),
                "Print Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private File fillTemplate(String type, String name) throws Exception {
        String tplName;
        switch (type) {
            case "Barangay Clearance": tplName = "Barangay_Clearance_Final.docx";        break;
            case "Barangay Indigency": tplName = "Certificate_of_Indigency_Final.docx";  break;
            default:                   tplName = "Certificate_of_Residency_Final.docx";  break;
        }

        File tpl = new File("src/LocalStorage/certs/" + tplName);
        if (!tpl.exists()) tpl = new File("LocalStorage/certs/" + tplName);
        if (!tpl.exists()) throw new java.io.FileNotFoundException("Template not found: " + tplName);

        File out = File.createTempFile("cert_print_", ".docx");
        out.deleteOnExit();

        try (ZipFile zIn   = new ZipFile(tpl);
             ZipOutputStream zOut = new ZipOutputStream(new java.io.FileOutputStream(out))) {

            java.util.Enumeration<? extends ZipEntry> entries = zIn.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                zOut.putNextEntry(new ZipEntry(entry.getName()));
                java.io.InputStream is = zIn.getInputStream(entry);

                if ("word/document.xml".equals(entry.getName())) {
                    byte[] bytes = is.readAllBytes();
                    String xml   = new String(bytes, "UTF-8");
                    xml = xml.replace("[Resident Name]", escapeXml(name));
                    xml = fillDate(xml);
                    zOut.write(xml.getBytes("UTF-8"));
                } else {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = is.read(buf)) > 0) zOut.write(buf, 0, len);
                }
                zOut.closeEntry();
            }
        }
        return out;
    }

    /**
     * Replaces the blank-tab date structure in the docx XML
     * ("Issued this [tab] day of [tab] 20[tab]") with the real formatted date.
     */
    private String fillDate(String xml) {
        if (this.dateIssued == null || this.dateIssued.isBlank()) return xml;
        try {
            LocalDate date = LocalDate.parse(this.dateIssued);
            int    day       = date.getDayOfMonth();
            String ordinal   = day + getOrdinalSuffix(day);
            String month     = date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            String year      = String.valueOf(date.getYear());
            String dateText  = ordinal + " day of " + month + " " + year;

            // Exact XML block that represents the blank date fields in the template
            String oldBlock =
                ">Issued this </w:t></w:r>" +
                "<w:r><w:rPr><w:u w:val=\"single\"/></w:rPr><w:tab/></w:r>" +
                "<w:r><w:t xml:space=\"preserve\">day of </w:t></w:r>" +
                "<w:r><w:rPr><w:u w:val=\"single\"/></w:rPr><w:tab/></w:r>" +
                "<w:r><w:rPr><w:spacing w:val=\"-5\"/></w:rPr><w:t>20</w:t></w:r>" +
                "<w:r><w:rPr><w:u w:val=\"single\"/></w:rPr><w:tab/></w:r>";

            // Replacement: one run with the full date text, keep one trailing tab for "at Barangay"
            String newBlock =
                ">Issued this </w:t></w:r>" +
                "<w:r><w:t xml:space=\"preserve\">" + escapeXml(dateText) + "</w:t></w:r>" +
                "<w:r><w:rPr><w:u w:val=\"single\"/></w:rPr><w:tab/></w:r>";

            return xml.replace(oldBlock, newBlock);
        } catch (Exception ex) {
            return xml;
        }
    }

    private String getOrdinalSuffix(int day) {
        if (day >= 11 && day <= 13) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
