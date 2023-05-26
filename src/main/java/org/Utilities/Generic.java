package org.Utilities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax. xml. parsers. DocumentBuilder;
import javax.xml.parsers. DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
//import org.example.ExcelOperation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache. poi. xssf.usermodel. XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom. Document;
import org.w3c. dom. Element;
import org.w3c.dom. Node;
import org.w3c.dom.NodeList;
import org.xml.sax. InputSource;
import org. xmlunit. builder. DiffBuilder;
import org.xmlunit.builder. Input;
import org. xmlunit. diff. Diff;
import org. xmlunit. diff. Difference;

import static org.xmlunit.diff.AbstractDifferenceEngine.getXPath;

public class Generic {
        public static XSSFWorkbook workbook = new XSSFWorkbook();
        public String path;
        public String envPath = Config.enviPath;
        public FileInputStream file = null;
       public static String appID = null;
        public static FileOutputStream fileout = null;

        public Generic(){}


        public boolean fileExists (String path) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("file exists");
                return true;
            } else {
                System.out.println("File does not exist");

                return false;
            }
        }

    public boolean fileWellFormCheck (String path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource source = new InputSource(path);
            builder.parse(source);
            System.out.println("XML file is well formed");
            return true;
        } catch (Exception e) {
            System.out.println("XML file is not well formed: " + e.getMessage());
            System.out.println("It 19 recommended to compare them by txt format, use compareXMIWithText () method");


        return false;
    }}
    public String readEnv() {
        String filePath =envPath; // Specify the path to your XML file

        List<String> batchNames = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();

            NodeList batchList = document.getElementsByTagName("batch");
            for (int i = 0; i < batchList.getLength(); i++) {
                Node batchNode = batchList.item(i);
                if (batchNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element batchElement = (Element) batchNode;
                    String batchName = batchElement.getElementsByTagName("batchname").item(0).getTextContent();
                    String permission = batchElement.getElementsByTagName("permission").item(0).getTextContent();

                    if (permission.equals("Yes")) {
                        batchNames.add(batchName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Print the batch names
        return batchNames.get(0);
    }
    public  boolean isReplaceValueTrue() {
        try {
            File xmlFile = new File(envPath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();

            Element batchesElement = document.getDocumentElement();
            NodeList replaceList = batchesElement.getElementsByTagName("replace");
            if (replaceList.getLength() > 0) {
                Node replaceNode = replaceList.item(0);
                String replaceValue = replaceNode.getTextContent();
                return replaceValue.equals("Yes");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

             public void XmLModifier(String xmlFilePAth) {
                 try {
                     Path path = Paths.get(xmlFilePAth);
                     byte[] bytes = Files.readAllBytes(path);
                     String xml = new String(bytes);
                     if (!xml.contains(" ‹ConsolidatedAPP>\n")) {
                         xml = "<Consolidate APP>\n" + xml;
                         xml = xml + "\n</ConsolidatedAPP>";

                         FileWriter fileWriter = new FileWriter(new File(xmlFilePAth));
                         fileWriter.write(xml);
                         fileWriter.close();

                     }
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
    public void readXmIToExcel (String path, String sheetName) throws Exception {

            CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);

        FileInputStream inputStream = new FileInputStream(path);
        XSSFSheet sheet= workbook.createSheet(sheetName);
        XSSFRow headerRow = sheet.createRow(0);
        XSSFCell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("ParentTag");
            headerCell.setCellStyle(style);

            headerCell=headerRow.createCell(1);
            headerCell.setCellValue("ChildTAg");
            headerCell.setCellStyle(style);

        headerCell=headerRow.createCell(2);
        headerCell.setCellValue("Value");
        headerCell.setCellStyle(style);

        int rowNum=1;


        DocumentBuilderFactory factory = DocumentBuilderFactory. newInstance ();
    DocumentBuilder builder = factory. newDocumentBuilder ();
    Document document = builder.parse (inputStream);

    NodeList parentNodes = document.getElementsByTagName ("*");

        for (int i = 0; i < parentNodes.getLength (); i++){
            Node parentNode = parentNodes.item (i);

               if (parentNode.hasChildNodes ()){
                  NodeList childNodes = parentNode. getChildNodes () ;


                   for (int j= 0; j < childNodes.getLength (); j++){
                      Node childNode = childNodes. item(j) ;

                      if (childNode. getNodeType () == Node.ELEMENT_NODE) {
                        Element childElement = (Element) childNode;

             XSSFRow row = sheet. createRow (rowNum++);
             XSSFCell cell =row.createCell (0);

            cell. setCellValue (parentNode. getNodeName ()) ;
            cell = row.createCell (1);
            cell.setCellValue (childNode.getNodeName ());
            cell = row. createCell (2);
            cell. setCellValue (childNode. getTextContent ());
        }
            }
               }
        } for (int i =0; i<3; i++){
            sheet.autoSizeColumn(i);
        }
            inputStream.close ();
            fileout = new FileOutputStream ("");
            workbook.write (fileout);}

            public void compareXmlFiles () throws Exception {
                String env=readEnv();
                String folderPath;
                if(env=="AL03"){
                    folderPath="bu olan folder";
                }
                else if(env=="AL05"){
                    folderPath="bu olan folder";
                }
                else{
                    folderPath="diger folder";
                }
                String path =folderPath+"\\batchfilename.xml";
                if (!(fileExists(path))) return;
                if (!(fileWellFormCheck(path)))
                    return;
                try {
                    readXmIToExcel(path, "BeforeCode");

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                //colorExcel();
                XmLModifier(path);
                File file1 = new File(path);
                //update method here
                try {
                    readXmIToExcel(path, "BeforeCode");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                XmLModifier(path);
                File file2 = new File(path);
                try {

                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    dbFactory.setIgnoringElementContentWhitespace(true);
                    dbFactory.setCoalescing(true);
                    dbFactory.setNamespaceAware(true);
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc1 = dBuilder.parse(file1);
                    Document doc2 = dBuilder.parse(file2);
                    doc1.getDocumentElement().normalize();
                    doc2.getDocumentElement().normalize();


                    CellStyle style = workbook.createCellStyle();
                    CellStyle style2 = workbook.createCellStyle();
                    CellStyle style3= workbook.createCellStyle();

                    CellStyle styleAll = workbook.createCellStyle();

                    Font font = workbook.createFont();
                    font.setBold(true);
                    style.setFont(font);
                    style.setAlignment(HorizontalAlignment.CENTER);

                    style2.setFont(font);
                    style2.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                    style2.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    style2.setFillBackgroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                    style2.setFillPattern(FillPatternType.BIG_SPOTS);
                    style2.setAlignment(HorizontalAlignment.CENTER);


                    List<String> columnNames = new ArrayList<>();
                    List<List<String>> data1 = new ArrayList<List<String>>();
                    List<List<String>> data2 = new ArrayList<List<String>>();
                    StringBuilder builder = new StringBuilder("");

                    Diff diff = DiffBuilder.compare(Input.fromDocument(doc1))
                            .withTest(Input.fromDocument(doc2))
                            .ignoreWhitespace()
                            .ignoreComments().build();


                    List<Difference> diffs = (List<Difference>) diff.getDifferences();
                    List<String[]> differences = compareNodes(doc1.getDocumentElement(), doc2.getDocumentElement(), builder, columnNames, data1, data2);

                    XSSFSheet sheet = workbook.createSheet("Differences");

                    int index = 0;
                    int policyCount = 0;
                    int rowNumber = 0;
                    Row totalrow = sheet.createRow(rowNumber++);
                    totalrow.createCell(1).setCellValue("Total difference");
                    totalrow.createCell(2).setCellValue(diffs.size());

                    totalrow.getCell(1).setCellStyle(style2);
                    totalrow.getCell(1).setCellStyle(style2);

                    rowNumber++;








                    Row headerRow = sheet.createRow(rowNumber++);
                    Cell cell = headerRow.createCell(0);
                    cell.setCellValue("Application ID");
                    cell.setCellStyle(style);

                    cell = headerRow.createCell(1);
                    cell.setCellValue("TagHierarchy");
                    cell.setCellStyle(style);

                    cell = headerRow.createCell(2);
                    cell.setCellValue("Data in BeforeCode XML");
                    cell.setCellStyle(style);

                    cell = headerRow.createCell(3);
                    cell.setCellValue("Data in AfterCode XML");
                    cell.setCellStyle(style);

                    cell = headerRow.createCell(4);
                    cell.setCellValue("MismatchDescription");
                    cell.setCellStyle(style);

                    for (String[] difference : differences) {
//            row = sheet.createRow(rowNumber++);
                        Row row = sheet.createRow(rowNumber++);
                        row.createCell(0).setCellValue(difference[0]);

                     //   Difference differ = diffs.get(index);
                        String xpath = diffs.get(index).getComparison().getControlDetails().getXPath();

                        row.createCell(1).setCellValue(xpath);
                        try {
                            row.createCell(2).setCellValue(Integer.parseInt(difference[1]));
                            row.createCell(3).setCellValue(Integer.parseInt(difference[2]));


                        } catch (Exception ex) {
                            row.createCell(2).setCellValue((difference[1]));
                          //  cell = row.createCell(3);
                            row.createCell(3).setCellValue(difference[2]);

                        }
                        row.createCell(4).setCellValue(diffs.get(index).getComparison().toString());
                        if (xpath.contains("PolicyMessage")) {

                            policyCount++;
                        }
                        index++;
                    }for(int i=0; i<6; i++){
                        sheet.autoSizeColumn(i);
                    }

                    String output=Config.resultPath;
                    fileout = new FileOutputStream(output);
                    workbook.write(fileout);
                    workbook.close();
                    fileout.close();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    public static String getXPathForNode (Node node) {
        StringBuilder xpath = new StringBuilder ();
        while (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) node;
            xpath.insert(  0,  "/" + element. getNodeName ());
            int index = getElementIndex (element);
            if
            (index > 0) {
                xpath.append(" [" + index + "]");
            }
            node = element.getParentNode();
        }
        return xpath.toString();
    }

    private static int getElementIndex (Element element) {
        int index = 1;

        Node prevSibling = element.getPreviousSibling();
        while (prevSibling != null) {
            if (prevSibling.getNodeType() == Node.ELEMENT_NODE && prevSibling.getNodeName().equals(element.getNodeName())) {
                index++;
            }
            prevSibling = prevSibling.getPreviousSibling();
        }
        return index;
    }
                public List<String[]> compareNodes (Node node1, Node node2, StringBuilder id,
                        List<String> columnNames,
                        List<List<String>> list1,
                        List<List<String>> list2){
                List<String[]> differences = new ArrayList<> () ;

                    XPath xPath= XPathFactory.newInstance().newXPath();

                if (!node1.getNodeName () . equals (node2 . getNodeName ())){
                return differences;}

                if (node1. getNodeName () . equalsIgnoreCase ("applicationID")) {
                    id. replace (0, id. length (), node1.getTextContent ()) ;}


                    if (node1. getNodeType () == Node.TEXT_NODE && node2. getNodeType () == Node.TEXT_NODE) {
                    if (!node1.getNodeValue () .trim ().equals("")){
                            String  data1 = node1. getNodeValue () .trim ();
                            String data2 = node2. getNodeValue () .trim ();

                            Node parent = node1. getParentNode () ;
                    while (parent != null && parent. getNodeType () != Node.ELEMENT_NODE) {
                        parent = parent.getParentNode();
                    }
                    if (parent != null & parent. getFirstChild () . getNodeType () ==Node.TEXT_NODE){
                    String tagName = parent. getNodeName () ;

                    if (columnNames. contains (tagName)){
                        int index = columnNames.indexOf (tagName);
                    list1. get (index).add (data1);
                    list2.get (index) . add (data2);


                } else{
                    columnNames. add (tagName);
                int index = columnNames. indexOf (tagName);
                list1.add (new ArrayList<> ());
                list2.add (new ArrayList<> ());
                list1.get (index).add (data1);
                list2.get (index) . add (data2) ;}

                if (data1.equals (data2));{
                String[] difference = new String [3];

                Node newParent=parent;
                boolean check=false;
                while (!newParent.getParentNode().getNodeName().equalsIgnoreCase("ConsolidatedAPP"))
                {if (check){
                    newParent = newParent.getParentNode();
                }else {
                    check=true;

                }}
                String s= getXPathForNode(newParent);
                    String val = xPath.evaluate(s+"/applicationID", newParent);
                difference [0] = val;
                difference [1] = data1;
                difference [2] = data2;
                differences.add (difference);

                    }
                    }  }}  else {
                            NodeList children1 = node1.getChildNodes() ;
                            NodeList children = node2. getChildNodes();

                            for (int i = 0; i< children1.getLength() && i < children. getLength(); i++) {
                                Node child1 = children1.item(i);
                                Node child2 = children.item(i);
                                differences.addAll(compareNodes (child1, child2, id, columnNames, list1, list2));

                            }}
                                return differences;
                            }
                    public void writeExcel(List<String> columns, List<List<String>> data, Sheet sheet) {
                                    Row headerRow = sheet. createRow( 0);
                                    for (int i = 0; i < columns.size(); i++){
                                    Cell cell = headerRow. createCell (i);
                                    cell.setCellValue (columns. get (i));
                                }
                                for (int i=0;  i< data.size(); i++) {
                                    Row row = sheet. createRow( i+ 1);


                                    for (int j = 0; j < data.get (i).size(); j++) {
                                        Cell cell = row. createCell (j);
                                        try {
                                            cell. setCellValue(Integer.parseInt(data.get (i) . get (j))); ;
                                        } catch (Exception ex) {
                                            cell.setCellValue (String.valueOf (data.get (i) .get (j)));

                                        }

                }

            }}
    public <T> List<List<T>> transpose(List<List<T>> original){
        int numRows = original.size();
        int numCols =original.stream().mapToInt (List:: size) .max(). orElse (0) ;
        List <List<T>> transposed = new ArrayList<> (numCols);
        for (int col=0; col<numCols; col++) {
            List<T> newRow = new ArrayList<>(numRows);
            for (int row = 0; row < numRows; row++) {
                List<T> cUrRow = original.get(row);
                T cellvalue = (col < cUrRow.size()) ? cUrRow.get(col) : null;
                newRow.add(cellvalue);
            }
            transposed.add(newRow);
        }
            return transposed;
        }}