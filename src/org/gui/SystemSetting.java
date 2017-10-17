package org.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Color;

public class SystemSetting {

	/**
     * File System
     */
	/*
    public enum FileSystem {
        SHARED, LOCAL
    }
    */
    /**
     * 共享存储的最大速率
     */
    
	//public static double storagetransferrate=15;//MB/s
	
	/**
	 * 显示比例
	 */
    
    
	private static double pixrate=0.1;
    public static double getPixrate() {
		return pixrate;
	}
	public static void setPixrate(double pixrate) {
		pixrate = pixrate;
	}
	private static Map<String, Color> ColorMap;
	
	
	/**
     * Map from file name to a file object
     */
    //private static Map<String, FileItem> fileName2File;

    
    
    /**
     * The selection of file.system
     */
    //private static FileSystem fileSystem;
    /**
     * Map from file to a list of data storage
     */
    //private static Map<String, List<String>> dataReplicaCatalog;

    private static rtwsimframe mainframe;
    
    /**
     * Initialize a ReplicaCatalog
     *
     * @param fs the type of file system
     */
    public static void init() {
        //fileSystem = fs;
        //dataReplicaCatalog = new HashMap<>();
        //fileName2File = new HashMap<>();
        ColorMap= new HashMap<>();
    }

    public static Map<String, Color> getColorMap() {
		return ColorMap;
	}


	public static void setMainframe(rtwsimframe rtwsimframe) {
		SystemSetting.mainframe = rtwsimframe;
	}

	public static rtwsimframe getMainframe() {
		return mainframe;
	}

	/**
     * Gets the file system
     *
     * @return file system
     */
    /*
	public static FileSystem getFileSystem() {
        return fileSystem;
    }
    */
    /**
     * Gets the file object based its file name
     *
     * @param fileName, file name
     * @return file object
     */
	/*
    public static FileItem getFile(String fileName) {
        return fileName2File.get(fileName);
    }
*/
    /**
     * Adds a file name and the associated file object
     *
     * @param fileName, the file name
     * @param file , the file object
     */
	/*
    public static void setFile(String fileName, FileItem file) {
        fileName2File.put(fileName, file);
    }
*/
    /**
     * Checks whether a file exists
     *
     * @param fileName file name
     * @return boolean, whether the file exist
     */
	/*
    public static boolean containsFile(String fileName) {
        return fileName2File.containsKey(fileName);
    }
*/
    /**
     * Gets the list of storages a file exists
     *
     * @param file the file object
     * @return list of storages
     */
	/*
    public static List<String> getStorageList(String file) {
        return dataReplicaCatalog.get(file);
    }
*/
    /**
     * Adds a file to a storage
     *
     * @param file, a file object
     * @param storage , the storage associated with this file
     */
	/*
    public static void addFileToStorage(String file, String storage) {
        if (!dataReplicaCatalog.containsKey(file)) {
            dataReplicaCatalog.put(file, new ArrayList<>());
        }
        List<String> list = getStorageList(file);
        if (!list.contains(storage)) {
            list.add(storage);
        }
    }
    */
}
