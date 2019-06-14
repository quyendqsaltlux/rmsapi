package saltlux.ctv.tranSS.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import saltlux.ctv.tranSS.enums.*;
import saltlux.ctv.tranSS.model.*;
import saltlux.ctv.tranSS.payload.invoice.InvoiceAuditResponse;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;
import saltlux.ctv.tranSS.payload.po.POResponse;
import saltlux.ctv.tranSS.payload.po.PoProjectAssignmentResponse;
import saltlux.ctv.tranSS.payload.project.ProjectRequest;
import saltlux.ctv.tranSS.payload.projectAssignment.ProjectAssignmentRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.google.common.base.Strings.isNullOrEmpty;

@Slf4j
public class ExcelUtil {
    private static final String TOMCAT_BASE = System.getProperty("catalina.base");
//    private static final String TOMCAT_BASE = Paths.get("").toAbsolutePath().getParent().toString();

    private static final int RDB_SHEET = 3;
    private static final int PROJECT_FINISHED_SHEET = 0;
    private static final int PAYMENT_OVERSEA_SHEET = 1;
    private static final int PAYMENT_KOREA_SHEET = 0;
    private static final int ASSIGNMENT_SHEET = 0;
    private static String RDB_FILE_PATH = "RDB.xlsx";
    private static String PROJECT_FINISHED_FILE_PATH = "Finished_projects.xlsx";
    private static String PROJECT_ONGOING_FILE_PATH = "Ongoing_projects.xlsx";
    private static String PAYMENT_FILE_PATH = "Invoicing.xlsx";

    private static String[] ASSIGNMENT_FILE_PATHS = new String[]{"GRA.xlsx", "ANN.xlsx", "HAN.xlsx", "KMH.xlsx", "TAM.xlsx", "XUA.xlsx", "Seoul.xlsx"};

    private static String PO_TEMPLATE_SP = "PO_TEMPLATE_SP.xlsx";
    private static String PO_TEMPLATE = "PO_TEMPLATE.xlsx";
    private static String INVOICE_TEMPLATE_SP = "Invoice_SP.xlsx";
    private static String INVOICE_TEMPLATE = "Invoice_non-SP.xlsx";

    private static String[][] PO_MAT = new String[26][14];
    private static final int MAX_COL_INVOICE = 13;
    private static String[][] INVOICE_MAT = new String[28][MAX_COL_INVOICE];
    private static String[] INVOICE_PO_COLS = new String[13];

    private static String RESOURCE_PATH = "transs-resource";
    private static String TEST_WAITING_PATH = "Test_tracking.xlsx";

    static {
        PO_MAT[13][4] = "code";
        PO_MAT[15][5] = "resourceName";
        PO_MAT[16][5] = "ho";
        PO_MAT[17][5] = "hb";
        PO_MAT[15][11] = "pm";
        PO_MAT[16][11] = "pmEmail";
        PO_MAT[17][11] = "pmTel";
        PO_MAT[21][1] = "jobCode";
        PO_MAT[21][4] = "task";
        PO_MAT[21][5] = "source";
        PO_MAT[21][6] = "target";
        PO_MAT[21][7] = "reprep";
        PO_MAT[21][8] = "rep100";
        PO_MAT[21][9] = "rep99_95";
        PO_MAT[21][10] = "rep94_85";
        PO_MAT[21][11] = "rep84_75";
        PO_MAT[21][12] = "repnoMatch";
        PO_MAT[21][13] = "totalRep";
        PO_MAT[23][7] = "wrep";
        PO_MAT[23][8] = "w100";
        PO_MAT[23][9] = "w99_95";
        PO_MAT[23][10] = "w94_85";
        PO_MAT[23][11] = "w84_75";
        PO_MAT[23][12] = "wnoMatch";
        PO_MAT[23][13] = "netOrHour";
        PO_MAT[25][1] = "unitPrice";
        PO_MAT[25][5] = "currency";
        PO_MAT[25][8] = "total";

        INVOICE_MAT[5][3] = "dateOfInvoice";
        INVOICE_MAT[6][3] = "resourceName";
        INVOICE_MAT[7][3] = "address";
        INVOICE_MAT[8][3] = "mobile";
        INVOICE_MAT[9][3] = "email";
        INVOICE_MAT[5][7] = "bankName";
        INVOICE_MAT[6][7] = "account";
        INVOICE_MAT[7][7] = "depositor";
        INVOICE_MAT[8][7] = "swiftCode";
        INVOICE_MAT[9][7] = "payPal";
        INVOICE_MAT[11][6] = "totalMoney";
        INVOICE_MAT[11][10] = "currency";

        INVOICE_PO_COLS[2] = "ho";
        INVOICE_PO_COLS[3] = "poNo";
        INVOICE_PO_COLS[4] = "projectName";
        INVOICE_PO_COLS[6] = "totalRep";
        INVOICE_PO_COLS[7] = "unitPrice";
        INVOICE_PO_COLS[8] = "total";
        INVOICE_PO_COLS[10] = "pmName";
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getCandidateTestWaitingFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, "code");
        fieldMap.put(1, "name");
        fieldMap.put(2, "source");
        fieldMap.put(3, "target");
        fieldMap.put(4, "contact");
        fieldMap.put(5, "testContents");
        fieldMap.put(6, "tool");
        fieldMap.put(7, "testInvitation");
        fieldMap.put(8, "testSending");
        fieldMap.put(9, "hbReceipt");
        fieldMap.put(10, "hbFiles");
        fieldMap.put(11, "internalCheck");
        fieldMap.put(12, "testEvaluation");
        fieldMap.put(13, "testResult");
        fieldMap.put(14, "comments");
        fieldMap.put(15, "evaluator");
        fieldMap.put(16, "otherNote");
        fieldMap.put(17, "attachment");
        fieldMap.put(18, "field");
        fieldMap.put(19, "catTool");
        fieldMap.put(20, "processStatus");
        fieldMap.put(21, "expectedRateRange");
        fieldMap.put(22, "negotiationDate");
        fieldMap.put(23, "shortListDate");

        return fieldMap;
    }

    private static List<ResourceTestWaiting> getChamTest(
            String path, int sheet,
            Map<Integer, String> candidateFieldMap,
            boolean isShortList)
            throws IOException, InvocationTargetException, IllegalAccessException {
        List<ResourceTestWaiting> testWaitingList = new ArrayList<>();
        Iterator<Row> iterator = getData(path, sheet);
        iterator.next();
        iterator.next();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            ResourceTestWaiting candidate = new ResourceTestWaiting();

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                Object colValue = getCellObject(currentCell);
                int colIndex = currentCell.getColumnIndex();

                String candidateColName = candidateFieldMap.get(colIndex);
                fillDataIntoTestWaiting(candidateColName, colIndex, candidate, colValue);
            }
            if (null != candidate.getSource()) {
                candidate.setIsShortList(isShortList ? 1 : 0);
                testWaitingList.add(candidate);
            }
        }
        return testWaitingList;
    }

    /**
     * @return PaymentReqs
     */
    public static List<ResourceTestWaiting> loadCandidateTestWaiting()
            throws IOException, InvocationTargetException, IllegalAccessException {
        Map<Integer, String> candidateFieldMap = getCandidateTestWaitingFieldMap();
        List<ResourceTestWaiting> testWaitingList = new ArrayList<>();
        testWaitingList.addAll(getChamTest(TEST_WAITING_PATH, 1, candidateFieldMap, false));
        testWaitingList.addAll(getChamTest(TEST_WAITING_PATH, 0, candidateFieldMap, true));
        return testWaitingList;
    }

    /**
     * @param colName  projectColName
     * @param colIndex colIndex
     * @param project  project
     * @param colValue colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataIntoTestWaiting(String colName, int colIndex,
                                                ResourceTestWaiting project, Object colValue)
            throws InvocationTargetException, IllegalAccessException {
        if (colName == null || null == colValue) {
            return;
        }
        if (colIndex == 7 || colIndex == 8 || colIndex == 9 || colIndex == 11 || colIndex == 22 || colIndex == 23) {
            Date date = getDateValue(colValue);
            if (null != date) {
                BeanUtils.setProperty(project, colName, date);
            }
        } else {
            BeanUtils.setProperty(project, colName, colValue);
        }
    }

    /**
     * @param invoice invoice
     * @return Map
     */
    private static Map<String, Object> getInvoiceMap(InvoiceAuditResponse invoice) {
        Map<String, Object> map = new HashMap<>();
        map.put("dateOfInvoice", invoice.getDateOfInvoice());
        map.put("resourceName", invoice.getResourceName());
        map.put("address", invoice.getAddress());
        map.put("mobile", invoice.getMobile());
        map.put("email", invoice.getEmail());
        map.put("bankName", invoice.getBankName());
        map.put("account", invoice.getAccount());
        map.put("depositor", invoice.getDepositor());
        map.put("swiftCode", invoice.getSwiftCode());
        map.put("payPal", invoice.getPayPal());
        map.put("currency", invoice.getCurrency());

        List<PoProjectAssignmentResponse> purchaseOrders = invoice.getPurchaseOrders();
        BigDecimal total = new BigDecimal("0");
        for (PoProjectAssignmentResponse po : purchaseOrders) {
            total = total.add(po.getTotal());
        }
        map.put("totalMoney", total);

        return map;
    }

    /**
     * @param purchaseOrders purchaseOrders
     * @return List
     */
    private static List<Map<String, Object>> getInvoicePosMap(List<PoProjectAssignmentResponse> purchaseOrders) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PoProjectAssignmentResponse order : purchaseOrders) {
            Map<String, Object> map = new HashMap<>();
            map.put("ho", order.getHo());
            map.put("poNo", order.getPoNo());
            map.put("projectName", order.getProject().getName());
            map.put("totalRep", order.getTotalRep());
            map.put("unitPrice", order.getUnitPrice());
            map.put("total", order.getTotal());
            map.put("pmName", order.getProject().getPmVtc().getName());

            list.add(map);
        }

        return list;
    }

    /**
     * @param invoice invoice
     * @return String
     * @throws IOException e
     */
    public static String exportInvoice(InvoiceAuditResponse invoice) throws IOException, ParseException {
        final int START_PO_ROW = 14;

        Map<String, Object> map = getInvoiceMap(invoice);
        List<Map<String, Object>> poMaps = getInvoicePosMap(invoice.getPurchaseOrders());
        boolean isSP = CompanyEnum.SP.toString().equals(invoice.getCompany());
        String fileName = isSP ? INVOICE_TEMPLATE_SP : INVOICE_TEMPLATE;
        FileInputStream file = new FileInputStream(
                new File(TOMCAT_BASE + "/" + RESOURCE_PATH + "/" + fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = -1;
        while (rowIterator.hasNext()) {
            rowIndex++;
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Object celValue;

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int colIndex = currentCell.getColumnIndex();
                if (rowIndex <= 11 && colIndex < MAX_COL_INVOICE && INVOICE_MAT[rowIndex][colIndex] != null) {
                    celValue = map.get(INVOICE_MAT[rowIndex][colIndex]);
                    if (null != celValue) {
                        if (celValue instanceof BigDecimal) {
                            if (isInvoiceTotal(rowIndex, colIndex)) {
                                double outValue = TransformUtil.roundByCurrency(invoice.getCurrency(), new BigDecimal(celValue.toString()))
                                        .stripTrailingZeros()
                                        .doubleValue();
                                currentCell.setCellValue(outValue);
                            } else {
                                currentCell.setCellValue(new BigDecimal(celValue.toString()).stripTrailingZeros().doubleValue());
                            }
                        } else {
                            currentCell.setCellValue(celValue.toString());
                        }
                    }
                } else if (rowIndex >= START_PO_ROW && null != INVOICE_PO_COLS[colIndex]
                        && rowIndex - START_PO_ROW < poMaps.size()) {
                    celValue = poMaps.get(rowIndex - START_PO_ROW).get(INVOICE_PO_COLS[colIndex]);
                    if (celValue instanceof Date) {
                        currentCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(celValue));
                    } else if (celValue instanceof BigDecimal) {
                        currentCell.setCellValue(new BigDecimal(celValue.toString()).stripTrailingZeros().doubleValue());
                    } else {
                        currentCell.setCellValue(celValue.toString());
                    }
                }
            }

        }

        String filePath = getInvoiceFilePath(invoice);

        FileOutputStream out = new FileOutputStream(new File(filePath));
        workbook.write(out);
        out.close();

        return filePath;
    }

    /**
     * @param po po
     * @return Map
     */
    private static Map<String, Object> getPOMap(POResponse po) {
        Map<String, Object> map = new HashMap<>();

        if (null == po.getAssignment().getExternalResource() || !po.getAssignment().getExternalResource()) {
            map.put("resourceName", po.getAssignment().getCandidate().getName());
        } else {
            map.put("resourceName", po.getAssignment().getExternalResourceName());
        }
        map.put("code", po.getCode());
        map.put("ho", po.getAssignment().getHo());
        map.put("hb", po.getAssignment().getHb());
        map.put("pm", po.getAssignment().getProject().getPmVtc().getName());
        map.put("pmEmail", po.getAssignment().getProject().getPmVtc().getEmail());
        map.put("pmTel", po.getAssignment().getProject().getPmVtc().getTel());
        map.put("jobCode", po.getAssignment().getProject().getCode());
        map.put("task", po.getAssignment().getTask());
        map.put("source", po.getAssignment().getSource());
        map.put("target", po.getAssignment().getTarget());
        map.put("reprep", po.getAssignment().getReprep());
        map.put("rep100", po.getAssignment().getRep100());
        map.put("rep99_95", po.getAssignment().getRep99_95());
        map.put("rep94_85", po.getAssignment().getRep94_85());
        map.put("rep84_75", po.getAssignment().getRep84_75());
        map.put("repnoMatch", po.getAssignment().getRepnoMatch());
        map.put("totalRep", po.getAssignment().getTotalRep());

        map.put("wrep", po.getAssignment().getWrep().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("w100", po.getAssignment().getW100().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("w99_95", po.getAssignment().getW99_95().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("w94_85", po.getAssignment().getW94_85().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("w84_75", po.getAssignment().getW84_75().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("wnoMatch", po.getAssignment().getWnoMatch().divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING));
        map.put("netOrHour", po.getAssignment().getNetOrHour());

        map.put("unitPrice", po.getAssignment().getUnitPrice());
        map.put("currency", po.getCurrency());
        map.put("total", po.getAssignment().getTotal());

        return map;
    }

    /**
     * @param po po
     * @return path of file
     * @throws IOException e
     */
    public static String exportPO(POResponse po) throws IOException {
        Map<String, Object> map = getPOMap(po);
        boolean isSP = CompanyEnum.SP.toString().equals(po.getCompany());
        String fileName = isSP ? PO_TEMPLATE_SP : PO_TEMPLATE;
        FileInputStream file = new FileInputStream(
                new File(TOMCAT_BASE + "/" + RESOURCE_PATH + "/" + fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        int rowIndex = -1;
        while (rowIterator.hasNext()) {
            rowIndex++;
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Object celValue;
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int colIndex = currentCell.getColumnIndex();
                if (rowIndex < 26 && colIndex < 14 && PO_MAT[rowIndex][colIndex] != null) {
                    celValue = map.get(PO_MAT[rowIndex][colIndex]);
                    if (null != celValue) {
                        if (celValue instanceof BigDecimal) {
                            if (isPoTotal(rowIndex, colIndex)) {
                                double outValue = TransformUtil.roundByCurrency(po.getCurrency(), new BigDecimal(celValue.toString()))
                                        .stripTrailingZeros()
                                        .doubleValue();
                                currentCell.setCellValue(outValue);
                            } else {
                                currentCell.setCellValue(new BigDecimal(celValue.toString()).stripTrailingZeros().doubleValue());
                            }
                        } else {
                            currentCell.setCellValue(celValue.toString());
                        }
                    }
                }
            }
        }
        String filePath = getPOFilePath(po);

        FileOutputStream out = new FileOutputStream(new File(filePath));
        workbook.write(out);
        out.close();

        return filePath;
    }

    private static boolean isPoTotal(int row, int col) {
        return row == 25 && col == 8;
    }

    private static boolean isInvoiceTotal(int row, int col) {
        return row == 11 && col == 6;
    }


    private static String getPOFilePath(POResponse po) {
        return TOMCAT_BASE + "/" + RESOURCE_PATH + "/PO_" + po.getCode() + ".xlsx";
    }

    private static String getInvoiceFilePath(InvoiceAuditResponse invoice) {
        return TOMCAT_BASE + "/" + RESOURCE_PATH + "/PO_" + invoice.getResourceName() + ".xlsx";
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getPaymentKoreaFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, "candidateCode");
        fieldMap.put(1, "registrationNumber");
        fieldMap.put(2, "bankName");
        fieldMap.put(3, "account");
        fieldMap.put(4, "accountOwner");
        fieldMap.put(5, "visa");

        return fieldMap;
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getPaymentOverseaFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, "candidateCode");
        fieldMap.put(1, null);
        fieldMap.put(2, "bankId");
        fieldMap.put(3, "iban");
        fieldMap.put(4, "bankName");
        fieldMap.put(5, "bankAddress");
        fieldMap.put(6, "account");
        fieldMap.put(7, "accountOwner");
        fieldMap.put(8, "ownerAddress");
        fieldMap.put(9, "swiftCode");
        fieldMap.put(10, "payPal");

        return fieldMap;
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getCandidateFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, null);
        fieldMap.put(1, "code");
        fieldMap.put(2, null);
        fieldMap.put(3, "grade");
        fieldMap.put(4, "name");
        fieldMap.put(5, null);
        fieldMap.put(6, "majorField");
        fieldMap.put(7, null);
        fieldMap.put(8, null);
        fieldMap.put(9, null);
        fieldMap.put(10, null);
        fieldMap.put(11, null);
        fieldMap.put(12, null);
        fieldMap.put(13, null);
        fieldMap.put(14, null);
        fieldMap.put(15, "currency");
        fieldMap.put(16, null);
        fieldMap.put(17, null);
        fieldMap.put(18, null);
        fieldMap.put(19, null);
        fieldMap.put(20, null);
        fieldMap.put(21, null);
        fieldMap.put(22, "email");
        fieldMap.put(23, "email2");
        fieldMap.put(24, "mobile");
        fieldMap.put(25, "messenger");
        fieldMap.put(26, "catTool");
        fieldMap.put(27, "remark");
        fieldMap.put(28, null);
        fieldMap.put(29, "bank");
        fieldMap.put(30, "payPal");
        fieldMap.put(31, "cv");
        fieldMap.put(32, "personalId");
        fieldMap.put(33, "education");
        fieldMap.put(34, "copyOfBankBook");
        fieldMap.put(35, "gender");
        fieldMap.put(36, "dateOfBirth");
        fieldMap.put(37, null);
        fieldMap.put(38, "address");
        fieldMap.put(39, null);
        fieldMap.put(40, "availableTime");
        fieldMap.put(41, null);
        fieldMap.put(42, "country");
        fieldMap.put(43, "nativeLanguage");
        fieldMap.put(44, null);

        return fieldMap;
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getAbilityFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, null);
        fieldMap.put(1, null);
        fieldMap.put(2, "projectType");
        fieldMap.put(3, null);
        fieldMap.put(4, null);
        fieldMap.put(5, null);
        fieldMap.put(6, null);
        fieldMap.put(7, "task");
        fieldMap.put(8, "sourceLanguage");
        fieldMap.put(9, "targetLanguage");
        fieldMap.put(10, "rate");
        fieldMap.put(11, "rateUnit");
        fieldMap.put(12, "rate2");
        fieldMap.put(13, "rate2unit");
        fieldMap.put(14, "minimumVolum");
        fieldMap.put(15, null);
        fieldMap.put(16, "wrep");
        fieldMap.put(17, "w100");
        fieldMap.put(18, "w99_95");
        fieldMap.put(19, "w94_85");
        fieldMap.put(20, "w84_75");
        fieldMap.put(21, "wnoMatch");
        fieldMap.put(22, null);
        fieldMap.put(23, null);
        fieldMap.put(24, null);
        fieldMap.put(25, null);
        fieldMap.put(26, null);
        fieldMap.put(27, null);
        fieldMap.put(28, null);
        fieldMap.put(29, null);
        fieldMap.put(30, null);
        fieldMap.put(31, null);
        fieldMap.put(32, null);
        fieldMap.put(33, null);
        fieldMap.put(34, null);
        fieldMap.put(35, null);
        fieldMap.put(36, null);
        fieldMap.put(37, null);
        fieldMap.put(38, null);
        fieldMap.put(39, null);
        fieldMap.put(40, null);
        fieldMap.put(41, null);
        fieldMap.put(42, null);
        fieldMap.put(43, null);

        return fieldMap;
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getProjectFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, "no");
        fieldMap.put(1, "requestDate");
        fieldMap.put(2, "dueDate");
        fieldMap.put(3, "dueTime");
        fieldMap.put(4, "pmCode");
        fieldMap.put(5, "category");
        fieldMap.put(6, "code");
        fieldMap.put(7, "folderName");
        fieldMap.put(8, "client");
        fieldMap.put(9, "contents");
        fieldMap.put(10, "reference");
        fieldMap.put(11, "termbase");
        fieldMap.put(12, "instruction");
        fieldMap.put(13, "remark");
        fieldMap.put(14, "totalVolume");
        fieldMap.put(15, "unit");
        fieldMap.put(16, "target");
        fieldMap.put(17, "progressStatus");
        fieldMap.put(18, "pmVtc");
        fieldMap.put(19, "ho");
        fieldMap.put(20, "hb");
        fieldMap.put(21, "reviewSchedule");
        fieldMap.put(22, null);
        fieldMap.put(23, "finalDelivery");

        return fieldMap;
    }

    /**
     * @return Map
     */
    private static Map<Integer, String> getAssignmentFieldMap() {
        Map<Integer, String> fieldMap = new HashMap<>();
        fieldMap.put(0, null);
        fieldMap.put(1, null);
        fieldMap.put(2, "projectCode");
        fieldMap.put(3, "candidateCode");
        fieldMap.put(4, null);
        fieldMap.put(5, "task");
        fieldMap.put(6, "source");
        fieldMap.put(7, "target");
        fieldMap.put(8, "unitPrice");
        fieldMap.put(9, "ho");
        fieldMap.put(10, "hb");
        fieldMap.put(11, null);
        fieldMap.put(12, "total");
        fieldMap.put(13, null);
        fieldMap.put(14, "netOrHour");
        fieldMap.put(15, null);
        fieldMap.put(16, "reprep");
        fieldMap.put(17, "rep100");
        fieldMap.put(18, "rep99_95");
        fieldMap.put(19, "rep94_85");
        fieldMap.put(20, "rep84_75");
        fieldMap.put(21, "repnoMatch");
        fieldMap.put(22, "totalRep");
        fieldMap.put(23, "wrep");
        fieldMap.put(24, "w100");
        fieldMap.put(25, "w99_95");
        fieldMap.put(26, "w94_85");
        fieldMap.put(27, "w84_75");
        fieldMap.put(28, "wnoMatch");

        return fieldMap;
    }

    /**
     * @param colValue colValue
     * @return date
     */
    private static Date getDateValue(Object colValue) {
        if (colValue instanceof Date) {
            return (Date) colValue;
        }
        if (colValue.toString().contains("/")) {
            try {
                return new SimpleDateFormat("MM/dd/yyyy").parse(colValue.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (colValue.toString().contains(".")) {
            try {
                return new SimpleDateFormat("yyyy.MM.dd").parse(colValue.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * @param colName  colName
     * @param colIndex colIndex
     * @param project  project
     * @param colValue colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataFromCellIntoAssignment(String colName, int colIndex,
                                                       ProjectAssignmentRequest project,
                                                       Object colValue) throws InvocationTargetException, IllegalAccessException {
        if (colName != null && null != colValue) {
            if ("N/A".equals(colValue.toString())) {
                return;
            }
            if (colIndex == 9 || colIndex == 10) {
                Date date = getDateValue(colValue);
                if (null != date) {
                    BeanUtils.setProperty(project, colName, date);
                }
            } else if ((colIndex >= 23 && colIndex <= 28) || colIndex == 12 || colIndex == 8 || colIndex == 14) {
                try {
                    Double.parseDouble(String.valueOf(colValue));
                    BeanUtils.setProperty(project, colName, colValue);
                } catch (NumberFormatException e) {
                    log.error(e.getLocalizedMessage());
                }
            } else if ((colIndex >= 16 && colIndex <= 22)) {
                try {
                    Integer.parseInt(String.valueOf(colValue));
                    BeanUtils.setProperty(project, colName, colValue);
                } catch (NumberFormatException e) {
                    log.error(e.getLocalizedMessage());
                }
            } else {
                log.info("COL:" + colName + " VAL: " + colValue);
                BeanUtils.setProperty(project, colName, colValue);
            }
        }
    }

    /**
     * migrate Assignment from file
     *
     * @return list of Assignments
     */
    public static Set<ProjectAssignmentRequest> loadAssignment() {
        Map<Integer, String> fieldMap = getAssignmentFieldMap();
        Set<ProjectAssignmentRequest> assignments = new HashSet<>();
        for (String assignmentFile : ASSIGNMENT_FILE_PATHS) {
            try {
                Iterator<Row> iterator = getData(assignmentFile, ASSIGNMENT_SHEET);
                iterator.next();
                iterator.next();
                while (iterator.hasNext()) {
                    Row currentRow = iterator.next();
                    Iterator<Cell> cellIterator = currentRow.iterator();
                    ProjectAssignmentRequest assignment = new ProjectAssignmentRequest();
                    while (cellIterator.hasNext()) {
                        Cell currentCell = cellIterator.next();
                        int colIndex = currentCell.getColumnIndex();
                        Object colValue = getCellObject(currentCell);

                        if (colIndex == 0 && null != colValue &&
                                ("Issued in 2018".equals(colValue.toString()) ||
                                        "Use this row as template only. It is protected.".equals(colValue.toString()))) {
                            continue;
                        }
                        if (colIndex == 2 &&
                                (null == colValue || isNullOrEmpty(colValue.toString()))) {
                            continue;
                        }
                        if (colIndex == 3 &&
                                (null == colValue || isNullOrEmpty(colValue.toString()) ||
                                        "#NR".equals(colValue.toString()) ||
                                        "#NA".equals(colValue.toString())
                                )) {
                            continue;
                        }
                        assignment.setStatus(ConfirmStatusEnum.CONFIRMED.toString());
                        assignment.setProgress(ProgressEnum.FINISHED.toString());
                        fillDataFromCellIntoAssignment(fieldMap.get(colIndex), colIndex, assignment, colValue);
                    }

                    assignments.add(assignment);
                }
            } catch (IllegalAccessException | InvocationTargetException | IOException e) {
                e.printStackTrace();
            }
        }

        return assignments;
    }

    /**
     * @param fieldMap fieldMap
     * @param iterator iterator
     * @return PaymentReqs
     */
    private static Set<PaymentReq> loadPayment(Map<Integer, String> fieldMap, Iterator<Row> iterator) {
        Set<PaymentReq> projects = new HashSet<>();
        try {
            iterator.next();
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();
                PaymentReq paymentReq = new PaymentReq();

                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    int colIndex = currentCell.getColumnIndex();
                    Object colValue = getCellObject(currentCell);
                    String projectColName = fieldMap.get(colIndex);

                    fillDataFromCellIntoKoreaPayment(projectColName, paymentReq, colValue);
                }
                projects.add(paymentReq);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return projects;
    }

    /**
     * @return PaymentReq
     */
    public static Set<PaymentReq> loadOverseaPayment() {
        Map<Integer, String> fieldMap = getPaymentOverseaFieldMap();
        Set<PaymentReq> projects = new HashSet<>();

        try {
            Iterator<Row> iterator = getData(PAYMENT_FILE_PATH, PAYMENT_OVERSEA_SHEET);
            projects = loadPayment(fieldMap, iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projects;
    }

    /**
     * @return Set
     */

    public static Set<PaymentReq> loadKoreaPayment() {
        Map<Integer, String> fieldMap = getPaymentKoreaFieldMap();
        Set<PaymentReq> paymentReqHashSet = new HashSet<>();

        try {
            Iterator<Row> iterator = getData(PAYMENT_FILE_PATH, PAYMENT_KOREA_SHEET);
            iterator.next();
            paymentReqHashSet = loadPayment(fieldMap, iterator);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paymentReqHashSet;
    }

    /**
     * @param colName   colName
     * @param candidate candidate
     * @param colValue  colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataFromCellIntoKoreaPayment(String colName, PaymentReq candidate, Object colValue)
            throws InvocationTargetException, IllegalAccessException {
        if (colName != null && null != colValue) {
            BeanUtils.setProperty(candidate, colName, colValue.toString());
        }
    }

    /**
     * @return List<Candidate>
     */
    public static List<Candidate> loadCandidate() throws IOException, InvocationTargetException, IllegalAccessException {
        Map<Integer, String> candidateFieldMap = getCandidateFieldMap();
        Map<Integer, String> abilityFieldMap = getAbilityFieldMap();
        List<Candidate> candidateList = new ArrayList<>();

        Iterator<Row> iterator = getData(RDB_FILE_PATH, RDB_SHEET);
        iterator.next();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            Candidate candidate = new Candidate();
            CandidateAbility ability = new CandidateAbility();

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                Object colValue = getCellObject(currentCell);
                int colIndex = currentCell.getColumnIndex();
                String candidateColName = candidateFieldMap.get(colIndex);
                String abilityColName = abilityFieldMap.get(colIndex);

                fillDataFromCellIntoCandidate(candidateColName, colIndex, candidate, colValue);
                fillDataFromCellIntoAbility(abilityColName, colIndex, ability, colValue);
            }
            /*ADD CANDIDATE TO LIST*/
            if (null != candidate.getCode()) {
                Candidate found = candidateList.stream().filter(
                        candidate1 -> candidate.getCode().equals(candidate1.getCode()))
                        .findAny().orElse(null);
                if (null != found) {
                    found.addAbility(ability);
                }
                if (null == found || !found.getCode().equals(candidate.getCode())) {
                    candidate.addAbility(ability);
                    candidateList.add(candidate);
                }
            }
        }
        return candidateList;
    }

    /**
     * @param abilityColName abilityColName
     * @param colIndex       colIndex
     * @param ability        ability
     * @param colValue       colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataFromCellIntoAbility(String abilityColName,
                                                    int colIndex,
                                                    CandidateAbility ability,
                                                    Object colValue) throws InvocationTargetException, IllegalAccessException {
        if (abilityColName != null) {
            if (null != colValue) {
                if (colIndex == 10 || colIndex == 12 || colIndex == 14) {
                    try {
                        Float value = Float.parseFloat(colValue.toString());
                        BeanUtils.setProperty(ability, abilityColName, value);
                    } catch (Exception e) {
                    }
                } else if (colIndex >= 16 && colIndex <= 21) {
                    try {
                        Float value = Float.parseFloat(colValue.toString());
                        BeanUtils.setProperty(ability, abilityColName, 100 * value);
                    } catch (Exception e) {
                    }
                } else {
                    BeanUtils.setProperty(ability, abilityColName, colValue.toString().trim());
                }
            }
        }
    }

    /**
     * @param candidateColName candidateColName
     * @param colIndex         colIndex
     * @param candidate        candidate
     * @param colValue         colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataFromCellIntoCandidate(String candidateColName, int colIndex, Candidate candidate, Object colValue)
            throws InvocationTargetException, IllegalAccessException {
        if (candidateColName != null) {
            /*Code*/
            if (colIndex == 1) {
                BeanUtils.setProperty(candidate, candidateColName, colValue);
                if (null != colValue) {
                    BeanUtils.setProperty(candidate, "type", colValue.toString().substring(0, 2));
                }
            } else
                /*DOB*/
                if (colIndex == 36) {
                    if (null != colValue) {
                        if (colValue instanceof Date) {
                            BeanUtils.setProperty(candidate, candidateColName, colValue);
                        } else if (colValue.toString().contains("/")) {
                            try {
                                Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(colValue.toString());
                                BeanUtils.setProperty(candidate, candidateColName, dob);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    if (null != colValue) {
                        BeanUtils.setProperty(candidate, candidateColName, colValue.toString().trim());
                    }
                }
        }
    }

    /**
     * @param currentCell currentCell
     * @return Object
     */
    private static Object getCellObject(Cell currentCell) {
        CellType type;
        if (currentCell.getCellType() == CellType.FORMULA) {
            type = currentCell.getCachedFormulaResultType();
        } else {
            type = currentCell.getCellType();
        }

        if (type == CellType.STRING) {
            Object colValue = currentCell.getStringCellValue();
            if (null != colValue && colValue.toString().trim().length() == 0) {
                return null;
            }
            return colValue;
        }

        if (type == CellType.NUMERIC) {
            if (HSSFDateUtil.isCellDateFormatted(currentCell)) {
                return currentCell.getDateCellValue();
            }
            return currentCell.getNumericCellValue() + "";
        }

        return null;
    }

    /**
     * @param currentCell currentCell
     * @param index       index
     * @return Object
     */
    private static Object getCellObjectProject(Cell currentCell, int index) {
        if (index == 13) {
            if (!"N/A".equals(currentCell.getStringCellValue())) {
                if ("Refer to memo".equals(currentCell.getStringCellValue()) && null != currentCell.getCellComment()) {
                    return currentCell.getCellComment().getString();
                } else {
                    return currentCell.getStringCellValue();
                }
            }

        }
        return getCellObject(currentCell);
    }


    /**
     * migrate PM from file
     *
     * @return list of users
     */
    public static Set<User> loadPM(Set<Role> roles) throws IOException {
        Set<User> users = new HashSet<>();


        Iterator<Row> iterator = getData(PROJECT_FINISHED_FILE_PATH, PROJECT_FINISHED_SHEET);
        iterator.next();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();


            for (Cell currentCell : currentRow) {
                int colIndex = currentCell.getColumnIndex();
                if (colIndex != 4 && colIndex != 18) {
                    continue;
                }
                Object colValue = getCellObject(currentCell);
                if (null == colValue || isNullOrEmpty(colValue.toString())) {
                    continue;
                }

                String value = colValue.toString().toUpperCase();
                User user = new User();
                user.setName(value);
                user.setCode(value);
                user.setUsername(value);
                user.setPassword("$2a$10$h94S5dX83dlYvUEPUBQuIejiWoQ4d7/xOrVidhTuyv7H17SOebnlO");
                user.setRoles(roles);
                User found = users.stream().filter(user1 -> user.getCode().equals(user1.getCode()))
                        .findAny().orElse(null);
                if (null == found) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    /**
     * @param type ONGOING or FINISHED
     * @return ProjectRequest
     */
    public static Set<ProjectRequest> loadProject(String type) throws InvocationTargetException, IllegalAccessException, IOException {
        Map<Integer, String> projectFieldMap = getProjectFieldMap();
        Set<ProjectRequest> projects = new HashSet<>();

        Iterator<Row> iterator;
        if ("FINISHED".equals(type)) {
            iterator = getData(PROJECT_FINISHED_FILE_PATH, PROJECT_FINISHED_SHEET);
        } else if ("ON_GOING".equals(type)) {
            iterator = getData(PROJECT_ONGOING_FILE_PATH, PROJECT_FINISHED_SHEET);
        } else {
            return null;
        }
        iterator.next();
        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            ProjectRequest project = new ProjectRequest();

            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                int colIndex = currentCell.getColumnIndex();
                Object colValue = getCellObjectProject(currentCell, colIndex);
                String projectColName = projectFieldMap.get(colIndex);

                fillDataFromCellIntoProject(projectColName, colIndex, project, colValue);
            }
            if (ProjectProgressEnum.FINISHED.toString().equals(type)) {
                project.setIsOld(true);
                project.setProgressPoint(1f);
            } else {
                project.setIsOld(false);
                project.setProgressPoint(0f);
            }
            projects.add(project);
        }

        return projects;
    }

    /**
     * @param projectColName projectColName
     * @param colIndex       colIndex
     * @param project        project
     * @param colValue       colValue
     * @throws InvocationTargetException e
     * @throws IllegalAccessException    e
     */
    private static void fillDataFromCellIntoProject(String projectColName, int colIndex, ProjectRequest project, Object colValue)
            throws InvocationTargetException, IllegalAccessException {
        if (projectColName != null && null != colValue) {
            if ("N/A".equals(colValue.toString())) {
                return;
            }
            if (colIndex == 0) {
                boolean isSP = colValue.toString().contains("SP");
                boolean isP = colValue.toString().contains("P");
                if (isSP) {
                    project.setCompany("SP");
                } else if (isP) {
                    project.setCompany("P");
                }
                project.setNo(colValue.toString());
                return;
            }
            if (colIndex == 15) {
                if ("C".equals(colValue.toString())) {
                    BeanUtils.setProperty(project, projectColName, UnitEnum.CHAR.toString());
                } else if ("W".equals(colValue.toString())) {
                    BeanUtils.setProperty(project, projectColName, UnitEnum.WORD.toString());
                } else if ("H".equals(colValue.toString())) {
                    BeanUtils.setProperty(project, projectColName, UnitEnum.HOUR.toString());
                } else if ("P".equals(colValue.toString())) {
                    BeanUtils.setProperty(project, projectColName, UnitEnum.PARAGRAPH.toString());
                }
                return;
            }

            if (colIndex == 17) {
                if ("Finish".equals(colValue.toString())) {
                    BeanUtils.setProperty(project, projectColName, ProjectProgressEnum.FINISHED.toString());
                }
                return;
            }

            if (colIndex == 1 || colIndex == 2 || colIndex == 19 || colIndex == 20 || colIndex == 21 || colIndex == 23) {
                if (colValue instanceof Date) {
                    BeanUtils.setProperty(project, projectColName, colValue);
                    return;
                }
                if (colValue.toString().contains("/")) {
                    try {
                        Date dob = new SimpleDateFormat("MM/dd/yyyy").parse(colValue.toString());
                        BeanUtils.setProperty(project, projectColName, dob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (colValue.toString().contains(".")) {
                    try {
                        Date dob = new SimpleDateFormat("yyyy.MM.dd").parse(colValue.toString());
                        BeanUtils.setProperty(project, projectColName, dob);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                BeanUtils.setProperty(project, projectColName, colValue);
            }
        }
    }


    /**
     * @param fileName fileName
     * @param sheet    sheet
     * @return Iterator<Row>
     * @throws IOException e
     */
    private static Iterator<Row> getData(String fileName, int sheet) throws IOException {

        FileInputStream excelFile = new FileInputStream(
                new File(TOMCAT_BASE + "/" + RESOURCE_PATH + "/" + fileName));
        Workbook workbook = new XSSFWorkbook(excelFile);
        Sheet rdbSheet = workbook.getSheetAt(sheet);
        return rdbSheet.iterator();
    }
}