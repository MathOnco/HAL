package HAL.Tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * used to read from and write to files, can open a file in "read" "write", "readBinary" and "writeBinary" modes.
 * FileIO objects keep their mode and file permanently. to open a different file or change modes, create a different new FileIO object
 */
public class FileIO {
    public final String fileName;
    public final char ReadWriteAppend;
    public final char mode;
    public final BufferedReader reader;
    public final BufferedWriter writer;
    //binary reader and writer
    public final DataOutputStream writerBin;
    public final DataInputStream readerBin;
    //object reader and writer
    public final FileInputStream serialfileReader;
    public final ObjectInputStream serialobjectReader;
    public final FileOutputStream serialfileWriter;
    public final ObjectOutputStream serialobjectWriter;
    boolean isClosed = false;

    /**
     * @param fileName name of the file to read from or write to
     * @param mode     should be either "r":read, "w":write, "rb":readBinary, "wb":writeBinary, "rs": readSerialization, "ws": writeSerialization
     */
    public FileIO(String fileName, String mode) {
        char[] modeChars = mode.toCharArray();
        if(modeChars.length==2&&modeChars[1]=='s' && (modeChars[0]=='r'||modeChars[0]=='w')) {
            reader=null;
            writer=null;
            writerBin=null;
            readerBin=null;
            this.fileName=fileName;
            this.ReadWriteAppend=modeChars[0];
            this.mode='s';
            ObjectOutputStream serialobjectWriter=null;
            FileOutputStream serialfileWriter=null;
            ObjectInputStream serialobjectReader=null;
            FileInputStream serialfileReader=null;
            if (modeChars[0] == 'r') {
                //setup serialization reader
                serialobjectWriter=null;
                serialfileWriter=null;
                try {
                    serialfileReader=new FileInputStream(fileName);
                    serialobjectReader=new ObjectInputStream(serialfileReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                //setup serialization writer
                serialobjectReader=null;
                serialfileReader=null;
                try {
                    serialfileWriter=new FileOutputStream(fileName);
                    serialobjectWriter=new ObjectOutputStream(serialfileWriter);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.serialfileReader=serialfileReader;
            this.serialobjectReader=serialobjectReader;
            this.serialfileWriter=serialfileWriter;
            this.serialobjectWriter=serialobjectWriter;
        }
        else {
            if (modeChars.length > 2 || (modeChars.length > 1 && modeChars[1] != 'b') || (modeChars[0] != 'w' && modeChars[0] != 'r' && modeChars[0] != 'a')) {
                throw new IllegalArgumentException("inccorect mode argument! mode should be 'r' for read, 'w' for write, or 'a' for append, followed by optional 'b' for binary or 's' for serialization");
            }
            this.fileName = fileName;
            this.ReadWriteAppend = modeChars[0];
            this.mode = modeChars.length > 1?'b':'n';
            boolean appendOut = false;
            if (ReadWriteAppend == 'a') {
                appendOut = true;
            }

            BufferedReader reader = null;
            BufferedWriter writer = null;
            DataOutputStream writerBin = null;
            DataInputStream readerBin = null;
            try {
                if (ReadWriteAppend == 'r') {
                    if (this.mode=='b') {
                        readerBin = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));

                    } else {
                        reader = new BufferedReader(new FileReader(fileName));
                    }
                } else if (ReadWriteAppend == 'w' || ReadWriteAppend == 'a') {
                    if (this.mode=='b') {
                        writerBin = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName, appendOut)));
                    } else {
                        writer = new BufferedWriter(new FileWriter(fileName, appendOut));
                    }
                } else {
                    throw new IllegalArgumentException("rwa character must be one of r(read) w(write) or a(append)");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.reader = reader;
            this.writer = writer;
            this.writerBin = writerBin;
            this.readerBin = readerBin;
            this.serialfileReader=null;
            this.serialfileWriter=null;
            this.serialobjectReader=null;
            this.serialobjectWriter=null;
        }
    }

    /**
     * returns whether the FileIO object has closed the file already
     *
     * @return
     */
    public boolean IsClosed() {
        return isClosed;
    }

    //READ FUNCTIONS

    /**
     * requires read mode ("r") pulls a line from the file, splits it by the delimiter, and returns an array of line
     * segments
     *
     * @param delimiter the delimiter used to divide the line
     */
    public String[] ReadLineDelimit(String delimiter) {
        String[] read = null;
        try {
            String line = reader.readLine();
            if (line != null && !line.equals("")) {
                read = line.split(delimiter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    /**
     * requires read mode ("r") pulls a line from the file, splits it by the delimeter and returns an array of integers
     * from that line
     *
     * @param delimiter the delimiter used to divide the line
     */
    public int[] ReadLineInts(String delimiter) {
        String[] raw = ReadLineDelimit(delimiter);
        if (raw == null) {
            return null;
        }
        int[] ret = new int[raw.length];
        for (int i = 0; i < raw.length; i++) {
            ret[i] = Integer.parseInt(raw[i]);
        }
        return ret;
    }

    /**
     * requires read mode ("r") pulls a line from the file, splits it by the delimeter and returns an array of floats
     * from that line
     *
     * @param delimiter the delimiter used to divide the line
     */
    public float[] ReadLineFloats(String delimiter) {
        String[] raw = ReadLineDelimit(delimiter);
        float[] ret = new float[raw.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Float.parseFloat(raw[i]);
        }
        return ret;
    }

    /**
     * requires read mode ("r") pulls a line from the file, splits it by the delimeter and returns an array of doubles
     * from that line
     *
     * @param delimiter the delimiter used to divide the line
     */
    public double[] ReadLineDoubles(String delimiter) {
        String[] raw = ReadLineDelimit(delimiter);
        if (raw == null) {
            return null;
        }
        double[] ret = new double[raw.length];
        for (int i = 0; i < raw.length; i++) {
            ret[i] = Double.parseDouble(raw[i].trim());
        }
        return ret;
    }

    /**
     * requires read mode ("r") pulls a line from the file, splits it by the delimeter and returns an array of longs
     * from that line
     *
     * @param delimiter the delimiter used to divide the line
     */
    public long[] ReadLineLongs(String delimiter) {
        String[] raw = ReadLineDelimit(delimiter);
        if (raw == null) {
            return null;
        }
        long[] ret = new long[raw.length];
        for (int i = 0; i < raw.length; i++) {
            ret[i] = Long.parseLong(raw[i]);
        }
        return ret;
    }

    /**
     * requires read mode ("r") pulls all lines from the file, splits them by the delimiter, and returns an arraylist,
     * where each entry is an array of line segments from that line
     *
     * @param delimiter the delimiter used to divide the lines
     */
    public ArrayList<String[]> ReadDelimit(String delimiter) {
        ArrayList<String[]> lines = new ArrayList<>();
        String[] read = ReadLineDelimit(delimiter);
        while (read != null) {
            lines.add(read);
            read = ReadLineDelimit(delimiter);
        }
        return lines;
    }

    /**
     * requires read mode ("r") pulls all lines from the file, splits them by the delimiter, and returns an arraylist,
     * where each entry is an array of doubles from that line
     *
     * @param delimiter the delimiter used to divide the lines
     */
    public ArrayList<double[]> ReadDoubles(String delimiter) {
        ArrayList<double[]> lines = new ArrayList<>();
        double[] read = ReadLineDoubles(delimiter);
        while (read != null) {
            lines.add(read);
            read = ReadLineDoubles(delimiter);
        }
        return lines;
    }

    /**
     * requires read mode ("r") pulls all lines from the file, splits them by the delimiter, and returns an arraylist,
     * where each entry is an array of ints from that line
     *
     * @param delimiter the delimiter used to divide the lines
     */
    public ArrayList<int[]> ReadInts(String delimiter) {
        ArrayList<int[]> lines = new ArrayList<>();
        int[] read = ReadLineInts(delimiter);
        while (read != null) {
            lines.add(read);
            read = ReadLineInts(delimiter);
        }
        return lines;
    }

    /**
     * requires read mode ("r") pulls all lines from the file, splits them by the delimiter, and returns an arraylist,
     * where each entry is an array of longs from that line
     *
     * @param delimiter the delimiter used to divide the lines
     */
    public ArrayList<long[]> ReadLongs(String delimiter) {
        ArrayList<long[]> lines = new ArrayList<>();
        long[] read = ReadLineLongs(delimiter);
        while (read != null) {
            lines.add(read);
            read = ReadLineLongs(delimiter);
        }
        return lines;
    }

    /**
     * requires read mode ("r") returns one line from the file as a string
     */
    public String ReadLine() {
        String read = "";
        try {
            read = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return read;
    }

    /**
     * requires read mode ("r") returns an array list of all lines from the file as strings
     */
    public ArrayList<String> Read() {
        ArrayList<String> ret = new ArrayList<>();
        String line = ReadLine();
        while (line != null) {
            ret.add(line);
            line = ReadLine();
        }
        return ret;
    }


    //WRITE FUNCTIONS

    /**
     * requires write mode or append mode ("w"/"a")
     *
     * @param text writes the line to the file
     */
    public void Write(String text) {
        try {
            this.writer.write(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") the objects in the list are written to the output file using the
     * toString() method
     *
     * @param data      a list of objects to be written
     * @param delimiter the delimiter used to separate each object
     */
    public void WriteDelimit(List<Object> data, String delimiter) {
        if (data.size() > 0) {
            for (int i = 0; i < data.size() - 1; i++) {
                Write(data.get(i).toString() + delimiter);
            }
            Write(data.get(data.size() - 1).toString());
        }
    }

    public void WriteStrings(List<String> data, String delimiter) {
        if (data.size() > 0) {
            for (int i = 0; i < data.size() - 1; i++) {
                Write(data.get(i) + delimiter);
            }
            Write(data.get(data.size() - 1));
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") the objects in the array are written to the output file using the
     * toString() method
     *
     * @param data      an array of objects to be written
     * @param delimiter the delimiter used to separate each object
     */
    public void WriteDelimit(Object[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(data[i].toString() + delimiter);
            }
            Write(data[data.length - 1].toString());
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") writes the array of ints to the file, separated by the delimiter
     */
    public void WriteDelimit(int[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(String.valueOf(data[i]) + delimiter);
            }
            Write(String.valueOf(data[data.length - 1]));
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") writes the array of longs to the file, separated by the delimiter
     */
    public void WriteDelimit(long[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(String.valueOf(data[i]) + delimiter);
            }
            Write(String.valueOf(data[data.length - 1]));
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") writes the array of floats to the file, separated by the delimiter
     */
    public void WriteDelimit(float[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(String.valueOf(data[i]) + delimiter);
            }
            Write(String.valueOf(data[data.length - 1]));
        }
    }

    /**
     * requires write mode or append mode ("w"/"a") writes the array of doubles to the file, separated by the delimiter
     */
    public void WriteDelimit(double[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(String.valueOf(data[i]) + delimiter);
            }
            Write(String.valueOf(data[data.length - 1]));
        }
    }

    public void WriteDelimit(String[] data, String delimiter) {
        if (data.length > 0) {
            for (int i = 0; i < data.length - 1; i++) {
                Write(data[i] + delimiter);
            }
            Write(data[data.length - 1]);
        }
    }
    //BIN WRITE FUNCTIONS

    public void WriteBinDoubles(double[] writeMe) {
        for (int i = 0; i < writeMe.length; i++) {
            WriteBinDouble(writeMe[i]);
        }
    }

    public void WriteBinFloats(float[] writeMe) {
        for (int i = 0; i < writeMe.length; i++) {
            WriteBinFloat(writeMe[i]);
        }
    }

    public void WriteBinInts(int[] writeMe) {
        for (int i = 0; i < writeMe.length; i++) {
            WriteBinInt(writeMe[i]);
        }
    }

    public void WriteBinLongs(long[] writeMe) {
        for (int i = 0; i < writeMe.length; i++) {
            WriteBinLong(writeMe[i]);
        }
    }

    public void WriteBinBools(boolean[] writeMe) {
        for (int i = 0; i < writeMe.length; i++) {
            WriteBinBool(writeMe[i]);
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a single double to the binary file
     */
    public void WriteBinDouble(double writeMe) {
        try {
            writerBin.writeDouble(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a single float to the binary file
     */
    public void WriteBinFloat(float writeMe) {
        try {
            writerBin.writeFloat(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a single int to the binary file
     */
    public void WriteBinInt(int writeMe) {
        try {
            writerBin.writeInt(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a single long to the binary file
     */
    public void WriteBinLong(long writeMe) {
        try {
            writerBin.writeLong(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a string to the binary file
     */
    public void WriteBinString(String writeMe) {
        try {
            writerBin.writeChars(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * requires writeBinary mode or appendBinary mode ("wb"/"ab") writes a single boolean to the binary file
     */
    public void WriteBinBool(boolean writeMe) {
        try {
            writerBin.writeBoolean(writeMe);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //BIN READ FUNCTIONS

    /**
     * requires readBinary mode ("rb") reads a single double from the binary file
     */
    public double ReadBinDouble() {
        double ret = 0;
        try {
            ret = readerBin.readDouble();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * requires readBinary mode ("rb") reads a single float from the binary file
     */
    public float ReadBinFloat() {
        float ret = 0;
        try {
            ret = readerBin.readFloat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * requires readBinary mode ("rb") reads a single int from the binary file
     */
    public int ReadBinInt() {
        int ret = 0;
        try {
            ret = readerBin.readInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * requires readBinary mode ("rb") reads a single long from the binary file
     */
    public long ReadBinLong() {
        long ret = 0;
        try {
            ret = readerBin.readLong();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * requires readBinary mode ("rb") reads a string from the binary file
     */
    public String ReadBinString() {
        String ret = "";
        try {
            ret = readerBin.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * requires readBinary mode ("rb") reads a boolean from the binary file
     */
    public boolean ReadBinBool() {
        boolean ret = false;
        try {
            ret = readerBin.readBoolean();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public void ReadBinDoubles(double[] putHere) {
        for (int i = 0; i < putHere.length; i++) {
            putHere[i] = ReadBinDouble();
        }
    }

    public void ReadBinFloats(float[] putHere) {
        for (int i = 0; i < putHere.length; i++) {
            putHere[i] = ReadBinFloat();
        }
    }

    public void ReadBinInts(int[] putHere) {
        for (int i = 0; i < putHere.length; i++) {
            putHere[i] = ReadBinInt();
        }
    }

    public void ReadBinLongs(long[] putHere) {
        for (int i = 0; i < putHere.length; i++) {
            putHere[i] = ReadBinLong();
        }
    }

    public void ReadBinBools(boolean[] putHere) {
        for (int i = 0; i < putHere.length; i++) {
            putHere[i] = ReadBinBool();
        }
    }
    //SERIALIZATON FUNCTIONS
    public Object ReadObject(){
        try {
            return serialobjectReader.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void WriteObject(Object toSave){
        try {
            serialobjectWriter.writeObject(toSave);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the length of the file, or -1 if the file does not exist
     */
    public double length() {
        File file = new File(fileName);
        return file.exists() ? file.length() : -1;
    }

    //CLOSE

    /**
     * used to Close the file. if forgotten, write calls may never finish
     */
    public void Close() {
        try {
            this.isClosed = true;
            if (ReadWriteAppend == 'r') {
                if (this.mode == 'b') {
                    readerBin.close();
                } else if (this.mode == 's') {
                    serialfileReader.close();
                    serialobjectReader.close();
                } else {
                    reader.close();
                }
            } else if (ReadWriteAppend == 'w' || ReadWriteAppend == 'a') {
                if (this.mode == 'b') {
                    writerBin.close();
                } else if (this.mode == 's') {
                    serialfileWriter.close();
                    serialobjectWriter.close();
                } else {
                    writer.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
