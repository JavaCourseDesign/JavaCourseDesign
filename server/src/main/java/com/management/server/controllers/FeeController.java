package com.management.server.controllers;

import com.management.server.models.Fee;
import com.management.server.models.Student;
import com.management.server.payload.response.DataResponse;
import com.management.server.repositories.FeeRepository;
import com.management.server.repositories.StudentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

@RestController
public class FeeController {
    @Autowired
    FeeRepository feeRepository;
    @Autowired
    StudentRepository studentRepository;

    @PostMapping("/getAllFees")
    public DataResponse getAllFees(){
        return new DataResponse(0,feeRepository.findAll(),"获取所有费用信息");
    }

    @PostMapping("/importFeeData")
    public DataResponse importFeeData(@RequestBody byte[] barr,
                                      @RequestParam(name = "uploader") String uploader) {
        try {
            InputStream in = new ByteArrayInputStream(barr);
            XSSFWorkbook workbook = new XSSFWorkbook(in);  //打开Excl数据流
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            Row row;
            Cell cell;
            String  name,studentId,time, money,place,goods;
            Double dMoney;
            Fee f;
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                cell = row.getCell(0);
                cell.setCellType(CellType.STRING);
                if (cell == null)
                    break;
                name= cell.getStringCellValue();  //获取一行消费记录 日期 金额
                cell = row.getCell(1);
                cell.setCellType(CellType.STRING);
                studentId = cell.getStringCellValue();
                cell = row.getCell(2);
                cell.setCellType(CellType.STRING);
                money = cell.getStringCellValue();
                cell = row.getCell(3);
                cell.setCellType(CellType.STRING);
                time = cell.getStringCellValue();
                cell = row.getCell(4);
                cell.setCellType(CellType.STRING);
                goods=cell.getStringCellValue();
                cell = row.getCell(5);
                cell.setCellType(CellType.STRING);
                place=cell.getStringCellValue();

                f = new Fee();
                Student s=studentRepository.findByStudentId(studentId);
                f.setPerson(s);
                f.setTime(time);
                if (money != null && money.length() > 0)
                    dMoney = Double.parseDouble(money);
                else
                    dMoney = 0d;
                f.setMoney(dMoney);
                f.setPlace(place);
                f.setGoods(goods);
                feeRepository.save(f);
                //System.out.println(i++);
            }
            workbook.close();  //关闭Excl输入流
            return new DataResponse(0,null,"上传成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new DataResponse(1,null,"上传失败！");
        }
    }
}
