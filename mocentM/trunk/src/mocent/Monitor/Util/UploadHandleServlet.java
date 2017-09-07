package mocent.Monitor.Util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mocent.Monitor.Action.BaseAction;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class UploadHandleServlet extends HttpServlet {
	
	String field = null; //excel表中第一列的字段名
	BaseAction ba = new BaseAction();
	private static final String fileName = "/dbconfig.properties"; //资源文件位置
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
             throws ServletException, IOException {
    		//还未获取实际路径，文件需放在C:/fakepath下
    	 	String filePath = request.getParameter("filePath");
    	 	BaseAction ba = new BaseAction();
    	 	JSONObject obj = new JSONObject();
    	 	try {
				excelLoad(filePath);
				obj.accumulate("errorCode", "2");
				obj.accumulate("errorMsg", "上传成功!");
			} catch (Exception e) {
				//e.printStackTrace();
				obj.accumulate("errorCode", "4");
				obj.accumulate("errorMsg", "文件版本过低,上传失败!");
			}
    	 	String retStr = JSONArray.fromObject(obj).toString();
        	ba.ajaxJson(retStr);
     }
 
     public void doPost(HttpServletRequest request, HttpServletResponse response)
             throws ServletException, IOException {
 
         doGet(request, response);
     }
     /**
      * 将数据写入数据库
      * @param filePath excel文件路径
      * @throws ClassNotFoundException
      * @throws SQLException
      * @throws IOException
      */
     public void excelLoad(String filePath)throws IOException, ClassNotFoundException, SQLException{
    	 Connection conn = null;
    	 JSONObject obj = new JSONObject();
    	 try{
 			conn = getConnection();
 			conn.setAutoCommit(false); //关闭自动提交
 			List<String> list = loadExcel(filePath);
	 		//此处考虑表只有两个字段所以使用*号，使用*号可以动态的获取字段长度，否则需要使用if来进行判断
	 		String querySql = "SELECT * FROM iccid_sim";
	 		PreparedStatement ps = conn.prepareStatement(querySql);
	 		ResultSet rs = ps.executeQuery();
	 		
	 		
	 		ResultSetMetaData rsmd = rs.getMetaData();
	 		int fieldNum = rsmd.getColumnDisplaySize(1); //获取数据库中声明的第一列的长度
	 		int count = rsmd.getColumnCount();//获取表行数
	 		List<String> iccid = getListToDataBase(rs,count);
	 			
	 		if(judgedata(iccid, list, fieldNum)){
	 			obj.accumulate("errorCode", "5");
	 			obj.accumulate("errorMsg", "请勿重复导入相同的数据!");
	 			String retStr = JSONArray.fromObject(obj).toString();
	 			ba.ajaxJson(retStr);
	 			return ;
	 		}
	 		
	 		String insertSql = "INSERT INTO iccid_sim VALUES(?,?)";
	 		PreparedStatement psIn = conn.prepareStatement(insertSql);
	 		for(int i=0; i<list.size(); i=i+2){
	 			if(fieldNum == 20){ //iccid
	 				psIn.setString(1,list.get(i));
	 				psIn.setString(2,list.get(i+1));
	 			}else{ //sim
	 				psIn.setString(1,list.get(i+1));
	 				psIn.setString(2,list.get(i));
	 			}
	 			psIn.addBatch();
	 		}
	 		int[] successNum = psIn.executeBatch();//每插入成功一条返回一个1
	 		//因为数据是成对出现的，集合中的两条数据在数据库中为一条记录，
	 			if( (list.size()/2)%2==0 ){
	 				conn.commit();
	 				conn.setAutoCommit(true);
	 				conn.close();	 				
	 			}else{
	 				conn.rollback();
	 			}
 		}catch(SQLException e){
 			conn.rollback();
 		}
 	}
     /**
      * 判断表前5条数据是否相同
      * @param DatabaseData 数据库的数据
      * @param excelData excel文件的数据
      * @param columnSize 获取数据库第一列的长度
      * @return
      */
    public boolean judgedata(List<String> DatabaseData,List<String> excelData,int columnSize){
    	//如果columnSize为20，index从0开始，因为获取excel的保存在集合的顺序为：iccid，sim，iccid，sim....
    	int index = 0;
    	boolean bool = false;
    	if(columnSize == 20){
    		for(int i=0; i<DatabaseData.size(); i++){
    			if(DatabaseData.get(i).equals(excelData.get(index))){
    				index+=2;
    				bool = true;
    			}
    		}
    	}else{
    		index = 1;
    		for(int i=0; i<DatabaseData.size(); i++){
    			if(DatabaseData.get(i).equals(excelData.get(index))){
    				index+=2;
    				bool = true;
    			}
    		}
    	}
    	return bool;
    }
     /**
      * 得到excel表中的数据
      * @param xlsPath excel表文件路径
      * @return
      * @throws IOException
      */
     public List<String> loadExcel(String xlsPath) throws IOException{
 		int flag = -1; //判断excel中的字段值获取一次
 		List<String> list = new ArrayList<String>();
 		
 		InputStream fileIn = new FileInputStream(xlsPath);
 		
 		Workbook wb = new HSSFWorkbook(fileIn);
 		Sheet sheet = wb.getSheetAt(0);
 		
 		for(Row r : sheet){
 			Cell cell = r.getCell(0);
 			int type = cell.getCellType();
 			if (type==HSSFCell.CELL_TYPE_NUMERIC) {
 				//不管在excel中是sim字段在前还是iccid字段在前，保存在集合中都是iccid字段在前
 				try{
 				if(field.equals("sim")){
 					list.add(r.getCell(1).getStringCellValue());
 					list.add((long)cell.getNumericCellValue()+"");
 				}else{
 					list.add((long)cell.getNumericCellValue()+"");
 					list.add(r.getCell(1).getStringCellValue());
 				}
 				}catch(IllegalStateException e){
 				}
 			}
 			else if(type == HSSFCell.CELL_TYPE_STRING)
 			{
 				if(field != null && flag != -1){
 					list.add(r.getCell(0).getStringCellValue());
 					list.add((long)r.getCell(1).getNumericCellValue()+"");
 				}
 				if(flag == -1){
 					flag = 1;
 					field = cell.getStringCellValue();
 				}
 			};
 		}

 		return list;
 	}
     /**
      * 获取jdbc连接
      * @return
      * @throws ClassNotFoundException
      * @throws SQLException
      * @throws IOException
      */
     public Connection getConnection() throws ClassNotFoundException, SQLException, IOException{
    	Properties p = new Properties();
    	InputStream in = UploadHandleServlet.class.getResourceAsStream(fileName);
    	p.load(in);
    	in.close();
    	
 		String driver= p.getProperty("db.driver");
 		String url=p.getProperty("db.url");
 		String username=p.getProperty("db.username");
 		String password=p.getProperty("db.password");
 		
 		Class.forName(driver);
 		Connection conn = DriverManager.getConnection(url, username, password);
 		
 		return conn;
 	}
    /**
     * 从数据库中拿前5条数据
     * @param rs
     * @param count
     * @return
     * @throws SQLException
     */
    private List<String> getListToDataBase(ResultSet rs,int count) throws SQLException{
    	List<String> iccidList = new ArrayList<String>(); //保存从数据库中取出的iccid
			while(rs.next()){
				for(int i=1; i<count; i++){
					if(i == 6){
						return iccidList;
					}
					iccidList.add(rs.getString(i));
				}
			}
		return iccidList;
    }
 }