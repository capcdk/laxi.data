/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package com.laxi.data.xlsx;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * XSSF and SAX (Event API)
 */
public class ExcelParser {
    private static Pattern indexPattern = Pattern.compile("^A[0-9]+$");
    private static List<Map<String, String>> dataListT;
    private final int startRow;
    private final int endRow;
    private int currentRow = 0;
    private final String filename;
    private static Map<String, String> map;
    private static char[] strChar;

    /**
     * 构造方法
     */
    public ExcelParser(String filename, int startRow, int endRow) throws Exception {
        dataListT = new ArrayList<>();
        if (filename.isEmpty()) {
            throw new Exception("文件名不能空");
        }
        this.filename = filename;
        this.startRow = startRow;
        this.endRow = endRow + 1;
        processSheet();
    }

    /**
     * 指定获取第一个sheet
     */
    private void processSheet() throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        Iterator<InputStream> it = r.getSheetsData();
        while (it.hasNext()) {
            map = null;
            InputStream sheet1 = it.next();
            InputSource sheetSource = new InputSource(sheet1);
            parser.parse(sheetSource);
            sheet1.close();
        }
    }

    /**
     * 加载sax 解析器
     */
    private XMLReader fetchSheetParser(SharedStringsTable sst) throws SAXException {
        XMLReader parser =
                XMLReaderFactory.createXMLReader(
                        "org.apache.xerces.parsers.SAXParser"
                );
        ContentHandler handler = new PagingHandler(sst);
        parser.setContentHandler(handler);
        return parser;
    }

    /**
     * See org.xml.sax.helpers.DefaultHandler javadocs
     */
    private class PagingHandler extends DefaultHandler {
        private SharedStringsTable sst;
        private String lastContents;
        private boolean nextIsString;
        private String index = null;

        private PagingHandler(SharedStringsTable sst) {
            this.sst = sst;
        }

        /**
         * 开始元素 （获取key 值）
         */
        @Override
        public void startElement(String uri, String localName, String name,
                                 Attributes attributes) throws SAXException {
            if ("c".equals(name)) {
                index = attributes.getValue("r");
                //判断是否是新的一行
                if (indexPattern.matcher(index).find()) {
                    if (map != null && isAccess() && !map.isEmpty()) {
                        dataListT.add(map);
                    }
                    map = new LinkedHashMap<>();
                    currentRow++;
                }
                if (isAccess()) {
                    String cellType = attributes.getValue("t");
                    nextIsString = "s".equals(cellType);
                }
            }
            lastContents = "";
        }

        /**
         * 获取value
         */
        @Override
        public void endElement(String uri, String localName, String name)
                throws SAXException {
            if (isAccess()) {
                if (nextIsString) {
                    int idx = Integer.parseInt(lastContents);
                    lastContents = new XSSFRichTextString(sst.getItemAt(idx).getString()).toString();
                    nextIsString = false;
                }
                if ("v".equals(name)) {
                    map.put(index, lastContents);
                }
            }

        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {
            if (isAccess()) {
                lastContents += new String(ch, start, length);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            if (map != null && isAccess() && !map.isEmpty()) {
                dataListT.add(map);
            }
        }

    }

    private boolean isAccess() {
        return currentRow >= startRow && currentRow <= endRow;
    }

    /**
     * 获取数据 并且填补字段值为空的数据
     */
    public List<Map<String, String>> getMyDataList() {
        List<Map<String, String>> list = dataListT.subList(startRow, dataListT.size());
        if (!list.isEmpty()) {
            Map<String, String> map = dataListT.get(0);
            List<String> com = data("A", map.size() - 1);
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> returnMap = list.get(i);
                for (String str : com) {
                    boolean flag = true;
                    for (String key : returnMap.keySet()) {
                        if (key.contains(str)) {
                            //有
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        //空白单元格置null值
                        returnMap.put(str + (i + 2), null);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 封装数据
     */
    private static List<String> data(String str, int counts) {
        List<String> list = new ArrayList<>();
        list.add(str);
        for (int i = 0; i < counts; i++) {
            strChar = str.toCharArray();
            carryNum(0);
            str = new String(strChar);
            list.add(str);
        }
        return list;
    }

    //数字进位
    private static void carryNum(int index) {
        char a = 'A';
        int aint = (int) ('A');
        if ((strChar.length - 1) - index >= 0) {
            int sc = (int) strChar[(strChar.length - 1) - index];
            if (sc - 25 >= aint) {
                carryNum(index + 1);
                strChar[(strChar.length - 1) - index] = a;
            } else {
                strChar[strChar.length - 1 - index] = (char) (sc + 1);
            }
        } else {
            strChar[(strChar.length - 1) - index + 1] = a;
            String str = "A" + String.valueOf(strChar);
            strChar = str.toCharArray();
        }
    }

}
