/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package supermarket;

/**
 *
 * @author Romil Chauhan
 */

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class dbconnect {
    
    private Connection con;
    private Statement st;
    private ResultSet rs;
    
    public dbconnect(){
        try{
            Class.forName("com.mysql.jdbc.Driver");     //load mysql or sqlite particular database
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/supermarket","root","");   //link with the particular database in xampp
            st=con.createStatement();   //as connection is establish so particular statements can be executed
        }catch(Exception ex){
            System.out.println(ex);
        }
         
    }
    
    ResultSet getResult(String query){
        try{
            st=con.createStatement();
            ResultSet res=st.executeQuery(query);
            return res;
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
    
    
    
    /*public void getData(){
        try{
            String query="select * from login";             //execute particular databse statment for eg. to retrieve data
            rs=st.executeQuery(query);                   //this stores the particular data in rs
            System.out.println("Records:"); 
            while(rs.next()){                            //rs.next check whether next row have particular data or not
                String user=rs.getString("username");           //this get partiluar usename in that row in user variable
                String password=rs.getString("password");           //this get that particular password in row in password variable
                System.out.println("Username:"+user);           //prints the username
                System.out.println("Password:"+password);           //prints the password 
             }
        }catch(Exception ex){
            System.out.println(ex);
        }
    }*/
    
    public static void main(String args[]){
        dbconnect d=new dbconnect();        //made db object
        //d.getData();                            //this calls the getdata method to retrieve data
    }

    public void ani(int pid, String pname) throws SQLException {
        float defqty=0;
        float defi=0;
        //st=con.createStatement();
        try
        {
            st.executeUpdate("INSERT INTO stock_details (`Product ID`, `Product Name`, `Qty`) VALUES ("+pid+",'"+pname+"',"+defqty+")");
            st.executeUpdate("INSERT INTO turn_over (`Product ID`, `Product Name`, `Quantity Sold`) VALUES ("+pid+",'"+pname+"',"+defqty+")");      
        }catch(SQLException ex){}//To change body of generated methods, choose Tools | Templates.
    }
    
   void addsup(String sname, int pid, String pname, float pqty, float pprice, float tprice, String od, String dd) throws SQLException 
    {   
        boolean flag=false;
        float getqty = 0;
        try
        {
            String query="SELECT * FROM `stock_details`";         
            rs=st.executeQuery(query);                 
            
            while(rs.next()){                            
                int getp=rs.getInt("Product ID"); 
                String getn=rs.getString("Product Name");
                if(pid==getp && pname.equalsIgnoreCase(getn))
                {
                    getqty=rs.getFloat("Qty");
                    flag=true;
                    break;
                }
             }
        }catch(SQLException ex){
            System.out.println(ex);
            
        }
        
        if(flag==true)
        {
            getqty+=pqty;
            try
            {    
                st.executeUpdate("UPDATE `stock_details` SET `Qty`="+getqty+" WHERE `Product ID`="+pid);
                
                st.executeUpdate("INSERT INTO `supplier_details`(`Supplier Name`, `Product ID`, `Product Name`, `Quantity`, `Price Per Product`, `Total Price`, `Order Date`, `Delivery Date`) VALUES ('"+sname+"',"+pid+",'"+pname+"',"+pqty+","+pprice+","+tprice+",'"+od+"','"+dd+"')");
                st.executeUpdate("UPDATE `turn_over` SET `Cost Price`="+pprice+" WHERE `Product ID`="+pid);
                
            }catch(SQLException ex){}
            
            
        }
                    
    }

    void addbill(int pid, String pname, float pqty, float pprice) throws SQLException {
        
        boolean f=false;
        float soldqty=0;
        float getqty = 0;
        try
        {
            String query="SELECT * FROM `stock_details";         
            rs=st.executeQuery(query);                 
            
            while(rs.next()){                            
                int getp=rs.getInt("Product ID"); 
                String getn=rs.getString("Product Name");
                if(pid==getp && pname.equalsIgnoreCase(getn))
                {
                    getqty=rs.getFloat("Qty");
                    f=true;
                    break;
                }
             }
        }catch(SQLException ex){
            System.out.println(ex);
            
        }
        
        if(f==true)
        {
            float sp=0,cp=0,profit=0;
            try
            {
                String query="SELECT * FROM `turn_over";         
                rs=st.executeQuery(query);                 

                while(rs.next()){                            
                    int getp=rs.getInt("Product ID"); 
                    if(pid==getp)
                    {
                        soldqty=rs.getFloat("Quantity Sold");
                        
                        cp=rs.getFloat("Cost Price");
                        
                        break;
                    }
                 }
            }catch(SQLException ex){
                System.out.println(ex);

            }
            
            
            
            soldqty+=pqty;
            
            profit=(pprice-cp)*soldqty;
            getqty-=pqty;
            try
            {    
                st.executeUpdate("UPDATE `stock_details` SET `Qty`="+getqty+" WHERE `Product ID`="+pid);
                st.executeUpdate("UPDATE `turn_over` SET `Selling Price`="+pprice+" WHERE `Product ID`="+pid);
                st.executeUpdate("UPDATE `turn_over` SET `Quantity Sold`="+soldqty+" WHERE `Product ID`="+pid);
                st.executeUpdate("UPDATE `turn_over` SET `Profit`="+profit+" WHERE `Product ID`="+pid);
                
                
                
            }catch(SQLException ex){}
            
            
        }
    }

    float getsd(String pname) throws SQLException{
        float qty = -1;
        
        try {
                            
            
            String query="SELECT * FROM `stock_details";
            rs=st.executeQuery(query); 
            
            
            while(rs.next()){
                
                String gname=rs.getString("Product Name");
                
                
                if(pname.equalsIgnoreCase(gname))
                {
                   
                    
                    qty=rs.getFloat("Qty");
                    
                    
                    
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(dbconnect.class.getName()).log(Level.SEVERE, null, ex);
        }
        return qty;
        
    }

    float getcp(int pid) throws SQLException {
        float cp=0;
        try
            {
                String query="SELECT * FROM `turn_over";         
                rs=st.executeQuery(query);                 

                while(rs.next()){                            
                    int getp=rs.getInt("Product ID"); 
                    
                    if(pid==getp)
                    {
                        cp=rs.getFloat("Cost Price");
                        
                        break;
                    }
                 }
            }catch(SQLException ex){
                System.out.println(ex);

            }
            return cp;
        //To change body of generated methods, choose Tools | Templates.
    }

    float getsp(int pid) {
        float sp=0;
        try
            {
                String query="SELECT * FROM `turn_over";         
                rs=st.executeQuery(query);                 

                while(rs.next()){                            
                    int getp=rs.getInt("Product ID"); 
                    if(pid==getp)
                    {
                        sp=rs.getFloat("Selling Price");
                        
                        break;
                    }
                 }
            }catch(SQLException ex){
                System.out.println(ex);

            }
            return sp; //To change body of generated methods, choose Tools | Templates.
    }

    float getqs(int pid) {
        float qs=0;
        try
            {
                String query="SELECT * FROM `turn_over";         
                rs=st.executeQuery(query);                 

                while(rs.next()){                            
                    int getp=rs.getInt("Product ID"); 
                    if(pid==getp)
                    {
                        qs=rs.getFloat("Quantity Sold");
                        
                        break;
                    }
                 }
            }catch(SQLException ex){
                System.out.println(ex);

            }
            return qs; //To change body of generated methods, choose Tools | Templates.
    }

   

    void setto(int pid, float pto) {
        try {
            st.executeUpdate("UPDATE `turn_over` SET `Profit`="+pto+" WHERE `Product ID`="+pid);
            //To change body of generated methods, choose Tools | Templates.
        } catch (SQLException ex) {
            Logger.getLogger(dbconnect.class.getName()).log(Level.SEVERE, null, ex);
        } //To change body of generated methods, choose Tools | Templates.
    }

    float dbupdateto() {
        float t=0;
        try
            {
                String query="SELECT * FROM `turn_over";         
                rs=st.executeQuery(query);                 

                while(rs.next()){                            
                    t+=rs.getFloat("Profit");
                    
                 }
            }catch(SQLException ex){
                System.out.println(ex);

            }
            return t;//To change body of generated methods, choose Tools | Templates.
    }

}
