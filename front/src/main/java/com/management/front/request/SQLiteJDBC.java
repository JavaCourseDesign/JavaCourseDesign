package com.management.front.request;

import com.teach.javafxclient.AppStore;
import com.teach.javafxclient.model.Student;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


public class SQLiteJDBC {

    private String dbName = "/teach/java2/java.db";
    private Map<String, List<OptionItem>> dictListMap = new HashMap<String, List<OptionItem>>();
    private Map<String, Map<String, String>> dictMapMap = new HashMap<String, Map<String, String>>();

    private static SQLiteJDBC instance = new SQLiteJDBC();
    private Connection con = null;
    private PasswordEncoder encoder = new BCryptPasswordEncoder();

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

    public String login(String username, String pwd) {
        Statement stmt = null;
        String sql;
        JwtResponse jwt = null;
        String password = null;
        Integer userId = null;
        Integer userTypeId = null;
        try {
            stmt = con.createStatement();
            sql = "select * from user where user_name='" + username + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                password = rs.getString("password");
                userId = rs.getInt("user_id");
                userTypeId = rs.getInt("user_type_id");
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getClass().getName() + ": " + e.getMessage();
        }
        if (userId == null) {
            return "用户不存在！";
        }
        if (!encoder.matches(pwd, password)) {
            return "用户密码不正确！";
        }
        jwt = new JwtResponse();
        jwt.setId(userId);
        jwt.setUsername(username);
        AppStore.setJwt(jwt);
        return null;
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

    public List<Student> getStudentList(String input) {
        List<Student> studentList = new ArrayList<>();
        Statement stmt = null;
        String sql;
        Student s;
        try {
            stmt = con.createStatement();
            sql = "select distinct s.*,p.* from student s, person p where p.person_id = s.person_id ";
            if (input != null && input.length() > 0) {
                sql += " and  p.num like '%" + input + "%' + or p.name like '%" + input + "%';";
            }
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                studentList.add(getStudentFromResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return studentList;
        }
    }

    public Integer getPersonIdByNum(String num) {
        Statement stmt = null;
        String sql;
        Integer personId = null;
        Student s = null;
        try {
            stmt = con.createStatement();
            sql = "select person_id from person where num ='" + num + "'";
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                personId = rs.getInt(1);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return personId;
        }
    }

    public String getNumByPersonId(Integer personId) {
        Statement stmt = null;
        String sql;
        String num = null;
        Student s = null;
        try {
            stmt = con.createStatement();
            sql = "select num from person where person_id =" + personId;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                num = rs.getString(1);
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            return num;
        }
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


    public Object studentEditSave(Student s, Integer studentId, Integer personId) {
        Statement stmt = null;
        String sql;
        String sql1;
        String newNum = s.getNum();
        Integer nPersonId = getPersonIdByNum(newNum);
        Integer userId;
        String num;
        if(nPersonId != null && (studentId== null || !nPersonId.equals(personId))) {
            return "新学号已经存在，不能添加或修改！";
        }
        try {
            if (studentId == null) {
                personId = getNewPersonId();
                sql = " insert into person (person_id, num,type) values ("+ personId + ",'" + newNum + "','1')";
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                userId = getNewUserId();
                String password = encoder.encode("123456");
                sql = " insert into user (user_id,person_id, user_name, password,user_type_id) values ("+userId+","+ personId + ",'" + newNum + "','"+password +"',2)";
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
                studentId = getNewStudentId();
                sql = " insert into student (student_id,person_id) values ("+studentId+","+ personId + ")";
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }else {
                num = getNumByPersonId(personId);
                if(!num.equals(newNum)) {
                    sql = " update person set num = '"+newNum+"' where person_id=" + personId;
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                    stmt.close();
                    sql = " update user set user_name = '"+newNum+"' where person_id=" + personId;
                    stmt = con.createStatement();
                    stmt.executeUpdate(sql);
                    stmt.close();
                }
            }
            sql = " update person set ";
            sql1 = "";
            if(s.getName() != null)
                sql1 += "name='" + s.getName() + "',";
            if(s.getDept() != null)
                sql += "dept='" + s.getDept() + "',";
            if(s.getCard() != null)
                sql1 += "card='" + s.getCard() + "',";
            if(s.getGender() != null)
                sql1 += "gender='" + s.getGender() + "',";
            if(s.getBirthday() != null)
                sql1 += "birthday='" + s.getBirthday() + "',";
            if(s.getEmail() != null)
                sql1 += "email='" + s.getEmail() + "',";
            if(s.getPhone() != null)
                sql1 += "phone='" + s.getPhone() + "',";
            if(s.getAddress() != null)
                sql1 += "address='" + s.getAddress() + "',";
            if(sql1.length() > 0) {
                sql = sql + sql1.substring(0, sql1.length() - 1) + " where person_id=" + personId;
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
            sql = " update student set ";
            sql1 = "";
            if(s.getMajor() != null)
               sql1 += "major='" + s.getMajor() + "',";
            if(s.getClassName() != null)
                sql1 += "class_name='" + s.getClassName() + "',";
            if(sql1.length() > 0) {
                sql = sql+ sql1.substring(0,sql1.length()-1)+ " where student_id=" + studentId;
                stmt = con.createStatement();
                stmt.executeUpdate(sql);
                stmt.close();
            }
            con.commit();
        } catch (Exception e) {
            return e.getClass().getName() + ": " + e.getMessage();
        }
        return new Integer[]{studentId,personId};
    }

    public String studentDelete(Integer studentId, Integer personId) {
        Statement stmt = null;
        String sql;
        try {
            sql = " delete from student ";
            sql += " where student_id=" + studentId;
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            sql = " delete from user ";
            sql += " where person_id=" + personId;
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            sql = " delete from person ";
            sql += " where person_id=" + personId;
            stmt = con.createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
            con.commit();
        } catch (Exception e) {
            return e.getClass().getName() + ": " + e.getMessage();
        }
        return null;
    }

}
