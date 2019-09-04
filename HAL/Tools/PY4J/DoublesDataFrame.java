package HAL.Tools.PY4J;

import HAL.Tools.FileIO;
import HAL.Util;

import java.util.ArrayList;
import java.util.HashMap;

public class DoublesDataFrame {
    public HashMap<String,Integer> columns=new HashMap<>();
    public ArrayList<double[]> data;
    public String[]headers;
    public DoublesDataFrame(String csvPath){
        this(csvPath,",");
    }
    public DoublesDataFrame(String []headers){
        this.headers=headers;
        SetCols(headers);
        this.data=new ArrayList<>();
    }
    public DoublesDataFrame(String csvPath,String delim){
        FileIO reader=new FileIO(csvPath,"r");
        headers=reader.ReadLineDelimit(delim);
        SetCols(headers);
        data=reader.ReadDoubles(delim);
    }
    public void SetCols(String[]headers){
        for (int i = 0; i < headers.length; i++) {
            columns.put(headers[i],i);
        }

    }
    public DoublesDataFrame(String[] headers,double[][]rows){
        this.headers=headers;
        SetCols(headers);
        this.data=new ArrayList<>();
        for (double[] row : rows) {
            data.add(row);
        }
    }
    public DoublesDataFrame(String[]headers,ArrayList<double[]>rows){
        this.headers=headers;
        SetCols(headers);
        this.data=rows;
    }

    public double Get(int row,String column){
        return data.get(row)[columns.get(column)];
    }
    public double Get(int row,int column){
        return data.get(row)[column];
    }
    public void Set(int row,String column,double val){
        data.get(row)[columns.get(column)]=val;
    }
    public void Set(int row,int column,double val){
        data.get(row)[column]=val;
    }
    public double[]GetRow(int row){
        return data.get(row);
    }
    public int ICol(String column){
        return columns.get(column);
    }
    public int Nrows(){
        return data.size();
    }
    public int Ncols(){
        return headers.length;
    }
    public void ToCSV(String path){
        FileIO out=new FileIO(path,"w");
        out.Write(Util.ArrToString(headers,",")+"\n");
        for (double[] row : data) {
            out.Write(Util.ArrToString(row,",")+"\n");
        }
        out.Close();
    }
}

