package com.demo.HBaseExample;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class App
{
    private static Logger logger = LogManager.getLogger(App.class.getName());

    public static void main( String[] args )
    {
        Properties props = new Properties();
        try {
            logger.debug("Loading Properties for HBase");
            props.load(new FileInputStream("src/main/resources/hbase.properties"));
        } catch(IOException ex)
        {
            System.out.println(ex.toString());
            return;
        }

        HBaseUtils hUtils = new HBaseUtils();
        hUtils.checkTable("page_views");

        hUtils.scanTable("page_views");
    }
}
