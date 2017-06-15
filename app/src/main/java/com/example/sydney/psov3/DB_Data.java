package com.example.sydney.psov3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;
import static com.example.sydney.psov3.Constants.*;

    class DB_Data extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pos_db.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase dbr = this.getReadableDatabase();
    private SQLiteDatabase dbw = this.getWritableDatabase();
    private ContentValues cv = new ContentValues();

    DB_Data(Context ctx){
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase arg0) {
        arg0.execSQL("CREATE TABLE "+TABLE_ADMIN+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_ADMIN_USERNAME+" TEXT NOT NULL UNIQUE, "
                +COLUMN_ADMIN_PASSWORD+" TEXT NOT NULL );");
        arg0.execSQL("CREATE TABLE "+TABLE_CASHIER+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_CASHIER_NUMBER+" INTEGER NOT NULL UNIQUE, "
                +COLUMN_CASHIER_NAME+" TEXT NOT NULL, "
                +COLUMN_CASHIER_POSITION+" TEXT NOT NULL,"
                +COLUMN_CASHIER_PASSWORD+" TEXT NOT NULL );");
        arg0.execSQL("CREATE TABLE "+TABLE_PRODUCT+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_PRODUCT_ID+" INTEGER NOT NULL UNIQUE, "
                +COLUMN_PRODUCT_NAME+" TEXT NOT NULL, "
                +COLUMN_PRODUCT_DESCRIPTION+" TEXT NOT NULL, "
                +COLUMN_PRODUCT_PRICE+" DOUBLE NOT NULL,"
                +COLUMN_PRODUCT_QUANTITY+" INTEGER NOT NULL, "
                +COLUMN_PRODUCT_VATABLE+" INTEGER );");
        arg0.execSQL("CREATE TABLE "+TABLE_INVOICE+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_INVOICE_TRANSACTION+"INTEGER NOT NULL ,"
                +COLUMN_INVOICE_CASHIER+" INTEGER NOT NULL, "
                +COLUMN_INVOICE_DISCOUNT+" INTEGER NOT NULL, "
                +COLUMN_INVOICE_CUSTOMER+" INTEGER NOT NULL, "
                +COLUMN_INVOICE_DATE+" INTEGER NOT NULL, "
                +COLUMN_INVOICE_TIME+" DATETIME);");
        arg0.execSQL("CREATE TABLE " +TABLE_ITEM+" ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +COLUMN_ITEM_INVOICE+" INTEGER NOT NULL, "
                +COLUMN_ITEM_PRODUCT+" INTEGER NOT NULL, "
                +COLUMN_ITEM_QUANTITY+" INTEGER NOT NULL );");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS cashierlog(date TEXT, time TEXT,userNum TEXT,lastname TEXT,username TEXT,transactionnumber INTEGER PRIMARY KEY AUTOINCREMENT);");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS sessions(time TEXT,date TEXT, username TEXT ); ");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS receipts(invoicenumber INTEGER,transactionnumber INTEGER);");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS departments(department TEXT,category TEXT,subcategory TEXT);");
        arg0.execSQL("CREATE TABLE "+TABLE_XREPORT+ " ("
                +_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_XREPORT_REPORTNUMBER+" INTEGER NOT NULL, "
                + COLUMN_XREPORT_DATE + " INTEGER NOT NULL, "
                + COLUMN_XREPORT_TIME + " INTEGER NOT NULL, "
                + COLUMN_XREPORT_CASHIER + " INTEGER NOT NULL);");
        arg0.execSQL("CREATE TABLE "+ TABLE_TRANSACTION+ "("
                + _ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TRANSACTION_TYPE + " TEXT NOT NULL);");
        arg0.execSQL("CREATE TABLE "+ TABLE_LOG + " ("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_LOG_STRING + " TEXT NOT NULL);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_ADMIN);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_CASHIER);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_PRODUCT);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_INVOICE);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_ITEM);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_XREPORT);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+TABLE_TRANSACTION);
        onCreate(arg0);
        arg0.execSQL("DROP TABLE IF EXISTS "+ TABLE_LOG);
        onCreate(arg0);
    }

    private static String[] FROM_ADMIN = {COLUMN_ADMIN_USERNAME,COLUMN_ADMIN_PASSWORD};
    int adminLogin(String usernum, String Pass) {
        String WHERE_ADMIN = "Username = ? and Password = ?";
        String[] WHERE_ARGS_ADMIN = new String[]{usernum,Pass};
        try {
            int i;
            Cursor curse = dbr.query(TABLE_ADMIN,FROM_ADMIN,WHERE_ADMIN,WHERE_ARGS_ADMIN,null,null,null);
            curse.moveToFirst();
            i = curse.getCount();
            curse.close();
            return i;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String[] FROM_CASH = {COLUMN_CASHIER_PASSWORD};
    int cashierLogin(String cname, String cpass) {
        String WHERE_CASH = "Cashiernum LIKE ? and Password = ?";
        String[] WHERE_ARGS_CASH = new String[]{cname.toLowerCase().trim(),cpass};
        try {
            Cursor curse = dbr.query(TABLE_CASHIER,FROM_CASH,WHERE_CASH,WHERE_ARGS_CASH,null,null,null);
            curse.moveToFirst();
            int i = curse.getCount();
            curse.close();
            return i;
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    void addCashier(String Name,String UserNum, String Pass, String Pos){
        cv.clear();
        cv.put(COLUMN_CASHIER_NAME, Name);
        cv.put(COLUMN_CASHIER_NUMBER, UserNum);
        cv.put(COLUMN_CASHIER_POSITION, Pos);
        cv.put(COLUMN_CASHIER_PASSWORD, Pass);
        dbw.insertOrThrow(TABLE_CASHIER, null, cv);
    }

    void addProduct(String ProdId,String ProdName,String ProdDesc, String ProdPrice, String ProdQuan){
        cv.clear();
        cv.put(COLUMN_PRODUCT_ID, ProdId);
        cv.put(COLUMN_PRODUCT_NAME, ProdName);
        cv.put(COLUMN_PRODUCT_DESCRIPTION, ProdDesc);
        cv.put(COLUMN_PRODUCT_PRICE, ProdPrice);
        cv.put(COLUMN_PRODUCT_QUANTITY, ProdQuan);
        dbw.insertOrThrow(TABLE_PRODUCT, null, cv);
    }

    void addAdmin(String mName,String mPass){
        cv.clear();
        cv.put(COLUMN_ADMIN_USERNAME, mName);
        cv.put(COLUMN_ADMIN_PASSWORD, mPass);
        dbw.insertOrThrow(TABLE_ADMIN, null, cv);
    }

    void addInvoice(String inTrans, String inCash, String inDisc, String inCustomer, String inDate, String inTime){
        cv.clear();
        cv.put(COLUMN_INVOICE_TRANSACTION,inTrans);
        cv.put(COLUMN_INVOICE_CASHIER,inCash);
        cv.put(COLUMN_INVOICE_DISCOUNT,inDisc);
        cv.put(COLUMN_INVOICE_CUSTOMER, inCustomer);
        cv.put(COLUMN_INVOICE_DATE,inDate);
        cv.put(COLUMN_INVOICE_TIME,inTime);
        dbw.insertOrThrow(TABLE_INVOICE, null, cv);
    }

    void addItem(String itemIn, String itemProd, String itemQuan){
        cv.clear();
        cv.put(COLUMN_ITEM_INVOICE,itemIn);
        cv.put(COLUMN_ITEM_PRODUCT,itemProd);
        cv.put(COLUMN_ITEM_QUANTITY,itemQuan);
        dbw.insertOrThrow(TABLE_ITEM, null, cv);
    }

    void addTransaction(String transactionType){
        cv.clear();
        cv.put(COLUMN_TRANSACTION_TYPE,transactionType);
        dbw.insertOrThrow(TABLE_TRANSACTION, null, cv);
    }

    void addLog(String log){
        cv.clear();
        cv.put(COLUMN_LOG_STRING,log);
    }

    List<String> getAllLabels(){
        String[] all = {COLUMN_CASHIER_NUMBER};
        Cursor cursor = dbr.query(TABLE_CASHIER,all,null,null,null,null,null);
        ArrayList<String> cashnum = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                cashnum.add(cursor.getString(0));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return cashnum;
    }

    private static String[] ALL = {COLUMN_CASHIER_NUMBER, COLUMN_CASHIER_NAME, COLUMN_CASHIER_POSITION};
    String[] searchStaff(String cnum) {
        String WHERE_CASH = COLUMN_CASHIER_NUMBER+" = ?";
        String[] WHERE_ARGS_CASH = new String[]{cnum};
        String[] res_staff= new String[4];
        Cursor cursor = dbr.query(TABLE_CASHIER,ALL,WHERE_CASH,WHERE_ARGS_CASH,null,null,null);
        cursor.moveToFirst();
        res_staff[0]=cursor.getString(1);
        res_staff[1]=cursor.getString(2);
        res_staff[2]=cursor.getString(3);
        cursor.close();
        return res_staff;
    }
    int searchStaffNumber(String staffNum) {
        SQLiteDatabase database = this.getReadableDatabase();
        String[] selectionArgs = new String[]{staffNum};
        try {
            int i;
            Cursor cursor;
            cursor = database.rawQuery("SELECT * FROM " + TABLE_CASHIER + " where " + COLUMN_CASHIER_NUMBER + "=?", selectionArgs );
            cursor.moveToFirst();
            i = cursor.getCount();
            cursor.close();
            return i;
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
        }

    String[] searchProduct(String id){
        String[] mALL = {COLUMN_PRODUCT_ID,COLUMN_PRODUCT_NAME,COLUMN_PRODUCT_DESCRIPTION,COLUMN_PRODUCT_PRICE,COLUMN_PRODUCT_QUANTITY,COLUMN_PRODUCT_VATABLE};
        String mWHERE = COLUMN_PRODUCT_ID+" = ?";
        String[] mWHERE_ARGS = new String[]{id};
        String[] itemResult = new String[5];
        Cursor cursor = dbr.query(TABLE_PRODUCT, mALL, mWHERE, mWHERE_ARGS, null, null, null);
        cursor.moveToFirst();
        itemResult[0]=cursor.getString(1);
        itemResult[1]=cursor.getString(2);
        itemResult[2]=cursor.getString(3);
        itemResult[3]=cursor.getString(4);
        itemResult[4]=cursor.getString(5);
        itemResult[5]=cursor.getString(6);
        cursor.close();
        return itemResult;
    }
     Cursor queryDataRead(String query) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(query,null);
        }
    void updateAdmin(String A_name,String A_pass){
        cv.clear();
        String WHERE = "ID = ?";
        String[] WHERE_ARGS = new String[]{"1"};
        cv.put(COLUMN_ADMIN_USERNAME,A_name);
        cv.put(COLUMN_ADMIN_PASSWORD,A_pass);
        dbw.update(TABLE_ADMIN, cv, WHERE,WHERE_ARGS);
    }

    void updateStaff(String Name,String UserNum, String Pass, String Pos){
        cv.clear();
        String WHERE_CASH = "Cashiernum = ?";
        String[] WHERE_ARGS_CASH = new String[]{UserNum};
        cv.put(COLUMN_CASHIER_NAME, Name);
        cv.put(COLUMN_CASHIER_POSITION, Pos);
        cv.put(COLUMN_CASHIER_PASSWORD, Pass);
        dbw.update(TABLE_CASHIER, cv, WHERE_CASH,WHERE_ARGS_CASH);
    }

    void updateProd(String P_id,String P_name,String P_desc, String P_price, String P_quan){
        cv.clear();
        String WHERE_CASH = COLUMN_PRODUCT_ID+" = ?";
        String[] WHERE_ARGS_CASH = new String[]{P_id};
        cv.put(COLUMN_PRODUCT_NAME, P_name);
        cv.put(COLUMN_PRODUCT_DESCRIPTION, P_desc);
        cv.put(COLUMN_PRODUCT_PRICE, P_price);
        cv.put(COLUMN_PRODUCT_QUANTITY, P_quan);
        dbw.update(TABLE_PRODUCT, cv, WHERE_CASH,WHERE_ARGS_CASH);
    }
//OLD SEARCH FOR PRODUCTS!!!!
//    public Cursor searchViewProduct(String searchItem) {
//        String[] columns = {_ID,COLUMN_PRODUCT_ID,COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_DESCRIPTION, COLUMN_PRODUCT_PRICE, COLUMN_PRODUCT_QUANTITY};
//        Cursor cursor = null;
//
//        if(searchItem != null && searchItem.length() > 0 ){
//            String sql = "SELECT * FROM "+ TABLE_PRODUCT + " WHERE "
//                    + COLUMN_PRODUCT_ID + " LIKE '%" + searchItem + "%' OR "
//                    + COLUMN_PRODUCT_NAME + " LIKE '%" + searchItem + "%' OR "
//                    + COLUMN_PRODUCT_DESCRIPTION + " LIKE '%" + searchItem + "%' OR "
//                    + COLUMN_PRODUCT_PRICE + " LIKE '%" + searchItem + "%' OR "
//                    + COLUMN_PRODUCT_QUANTITY + " LIKE '%" + searchItem +"%'";
//            cursor = dbr.rawQuery(sql,null);
//            return cursor;
//        }
//
//        cursor = dbr.query(TABLE_PRODUCT,columns,null,null,null,null,null,null);
//        return cursor;
//    }

        Cursor searchProductBaKamo(String searchItem, String WHERE) {
        String[] columns = {_ID,COLUMN_PRODUCT_ID,COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_DESCRIPTION, COLUMN_PRODUCT_PRICE, COLUMN_PRODUCT_QUANTITY};
        Cursor cursor = null;
        if(searchItem != null && searchItem.length() > 0 ){
            String sql = "SELECT * FROM "+ TABLE_PRODUCT + " WHERE " + WHERE + " LIKE '%" + searchItem + "%'";
            cursor = dbr.rawQuery(sql,null);
            return cursor;
        }
        else {
            cursor = dbr.query(TABLE_PRODUCT, columns, null, null, null, null, null, null);
            return cursor;
        }
    }
}