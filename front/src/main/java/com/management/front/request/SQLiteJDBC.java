package com.management.front.request;

import com.management.front.AppStore;
import com.management.front.model.Student;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


public class SQLiteJDBC {

    private String dbName = "java.db";
    private Map<String, List<OptionItem>> dictListMap = new HashMap<String, List<OptionItem>>();
    private Map<String, Map<String, String>> dictMapMap = new HashMap<String, Map<String, String>>();

    private static SQLiteJDBC instance = new SQLiteJDBC();
    private Connection con = null;

    private SQLiteJDBC() {
        try {
            Boolean dbExists = new File(dbName).isFile();
            if (!dbExists) {
                System.out.println("database dbName is not exists");
                return;
            }
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:" + dbName);
            con.setAutoCommit(false);
        } catch (Exception e) {
            con = null;
        }
        initDictionary();
    }

    public static SQLiteJDBC getInstance() {
        return instance;
    }

    public void close() {
        try {
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initDictionary() {
        List<OptionItem> itemList;
        Statement stmt = null;
        String sql;
        OptionItem item;
        Iterator ie;
        Map<String, Integer> iMap = new HashMap();
        Map<String, String> sMap;
        int id;
        String value;
        ResultSet rs;
        try {
            stmt = con.createStatement();
            sql = "select id,value from dictionary  where pid is null";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                iMap.put(rs.getString("value"), rs.getInt("id"));
            }
            rs.close();
            stmt.close();
            ie = iMap.keySet().iterator();
            while (ie.hasNext()) {
                value = (String) ie.next();
                id = iMap.get(value);
                sMap = new HashMap<String, String>();
                dictMapMap.put(value,sMap);
                itemList = new ArrayList();
                dictListMap.put(value,itemList);
                stmt = con.createStatement();
                sql = "select * from dictionary  where pid =" + id;
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    sMap.put(rs.getString("value"), rs.getString("label"));
                    item = new OptionItem(rs.getInt("id"), rs.getString("value"), rs.getString("label"));
                    itemList.add(item);
                }
                rs.close();
                stmt.close();
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public List<OptionItem> getDictionaryOptionItemList(String code) {
        return dictListMap.get(code);
    }

    public String getDictionaryLabelByValue(String code,String value) {
        if(value == null || value == null)
            return "";
        Map<String, String> m = dictMapMap.get(code);
        if(m == null)
            return "";
        String label = m.get(value);
        if(label == null)
            return "";
        else
            return label;
    }



    public Integer getUserTypeId(Integer userId) {
        Statement stmt = null;
        String sql;
        Integer userTypeId = null;
        try {
            stmt = con.createStatement();
            sql = "select user_type_id from user where user_id=" + userId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                userTypeId = rs.getInt("user_type_id");
            }
            rs.close();
            stmt.close();
            return userTypeId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DataResponse getMenuList(DataRequest request) {
        Integer userId = AppStore.getJwt().getId();
        Integer userTypeId = getUserTypeId(userId);
        Statement stmt = null;
        String sql;
        ResultSet rs;
        Map m, ms;
        Map data = new HashMap();
        List sList;
        Integer id;
        List<Map> dataList = new ArrayList();
        int i;
        try {
            stmt = con.createStatement();
            sql = "select * from menu where pid is null and user_type_id =" + userTypeId;
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                m = new HashMap();
                m.put("id", rs.getInt("id"));
                m.put("name", rs.getString("name"));
                m.put("title", rs.getString("title"));
                m.put("sList", new ArrayList());
                dataList.add(m);
            }
            rs.close();
            stmt.close();
            for (i = 0; i < dataList.size(); i++) {
                m = dataList.get(i);
                sList = (List) m.get("sList");
                id = (Integer) m.get("id");
                stmt = con.createStatement();
                sql = "select * from menu where pid =" + id + " and user_type_id =" + userTypeId;
                rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    ms = new HashMap();
                    ms.put("id", rs.getInt("id"));
                    ms.put("name", rs.getString("name"));
                    ms.put("title", rs.getString("title"));
                    sList.add(ms);
                }
                rs.close();
                stmt.close();
            }
            return new DataResponse(0, dataList, null);
        } catch (Exception e) {
            return new DataResponse(1, null, e.getMessage());
        }
    }

    private int getNewTableId(String tableName, String idName) {
        Statement stmt = null;
        String sql;
        int max = 1;
        try {
            stmt = con.createStatement();
            sql = "select max(" + idName + ") from " + tableName;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                max = rs.getInt(1);
                max = max + 1;
            } else {
                max = 1;
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return max;
        }
    }

    private int getNewPersonId() {
        return getNewTableId("person", "person_id");
    }

    private int getNewUserId() {
        return getNewTableId("user", "user_id");
    }

    private int getNewStudentId() {
        return getNewTableId("student", "student_id");
    }

    public Student getStudentById(Integer studentId) {
        Statement stmt = null;
        String sql;
        Student s = null;
        try {
            stmt = con.createStatement();
            sql = "select s.*,p.* from student s,person p where p.person_id= s.person_id and s.student_id=" + studentId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                s = getStudentFromResultSet(rs);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            if (s == null)
                s = new Student();
            return s;
        }
    }

    public Student getStudentFromResultSet(ResultSet rs) throws Exception {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setPersonId(rs.getInt("person_id"));
        s.setNum(rs.getString("num"));
        s.setName(rs.getString("name"));
        s.setDept(rs.getString("dept"));
        s.setCard(rs.getString("card"));
        s.setGender(rs.getString("gender"));
        s.setGenderName(getDictionaryLabelByValue("XBM",s.getGender()));
        s.setBirthday(rs.getString("birthday"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("Phone"));
        s.setAddress(rs.getString("address"));
        s.setIntroduce(rs.getString("introduce"));
        s.setMajor(rs.getString("major"));
        s.setClassName(rs.getString("class_name"));
        return s;
    }


}
