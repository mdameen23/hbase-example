package com.demo.HBaseExample;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class HBaseUtils {

    private static Logger logger = LogManager.getLogger(HBaseUtils.class.getName());

    private Configuration hConfig;
    private HTableInterface theTable;
    private HBaseAdmin admin;

    public HBaseUtils() {
        try {
            hConfig = HBaseConfiguration.create();
            //hConfig.set("hbase.zookeeper.quorum", "maprdemo");
            //hConfig.set("hbase.zookeeper.property.clientPort", "5181");
            //hConfig.set("hbase.rootdir", "maprfs:///hbase");
            //hConfig.set("hbase.cluster.distributed", "true");
            //hConfig.set("dfs.support.append", "true");
            //hConfig.set("hbase.fsutil.maprfs.impl", "org.apache.hadoop.hbase.util.FSMapRUtils");

            admin = new HBaseAdmin(hConfig);
        } catch (Exception ex) {
            logger.info("Exception while init: " + ex.toString());
        }
    }

    public void checkTable(String tableName) {
        try {
            logger.info("Checking table: " + tableName);
            if (admin.tableExists(tableName)) {
                logger.info("table '" + tableName + "' exists");
                return;
            }

            HTableDescriptor table = new HTableDescriptor(tableName);
            HColumnDescriptor family = new HColumnDescriptor(Bytes.toBytes("views"));
            table.addFamily(family);
            admin.createTable(table);
            logger.info("table: '" + tableName + "' created");
        } catch (Exception ex) {
            logger.info("Exception while check table: " + ex.toString());
        }
    }

    public void dropTable(String tableName) {
        try {
            logger.info("Checking table: " + tableName);
            if (!admin.tableExists(tableName)) {
                logger.info("table '" + tableName + "' not found");
                return;
            }

            admin.disableTable(tableName);

            admin.deleteTable(tableName);

            logger.info("table: '" + tableName + "' dropped");
        } catch (Exception ex) {
            logger.info("Exception while drop table: " + ex.toString());
        }
    }

    public void scanTable(String tableName) {
        try {
            HTable theTable = new HTable(hConfig, tableName);
            Scan scan = new Scan();
            scan.addColumn(Bytes.toBytes("views"), Bytes.toBytes("total_views"));
            ResultScanner rScanner = theTable.getScanner(scan);
            for (Result result = rScanner.next(); (result != null); result = rScanner.next()) {
                logger.info("Row: " + result);
            }
            rScanner.close();
        } catch (Exception ex) {
            logger.info("Exception while scan: " + ex.toString());
        }
    }

    public void increment_col(String tableName, String rowKey,
                              String colFamily, String col) {

        try {
            logger.info("Increment on: " + tableName + " -> " + rowKey + " " + colFamily + ":" + col);
            HTable theTable = new HTable(hConfig, tableName);
            theTable.incrementColumnValue(Bytes.toBytes(rowKey), Bytes.toBytes(colFamily), Bytes.toBytes(col), 1L);
            theTable.close();
        } catch (Exception ex) {
            logger.info("Exception while increment: " + ex.toString());
        }
    }

    public void table_put(String tableName, String rowKey, String colFamily,
                          String col, String val) throws Exception {

        logger.info("Put on: " + tableName + " -> " + rowKey + " " + colFamily + ":" + col + " = " + val);
        HTable theTable = new HTable(hConfig, tableName);
        Put p = new Put(Bytes.toBytes(rowKey));
        p.add(Bytes.toBytes(colFamily), Bytes.toBytes(col),Bytes.toBytes(val));
        theTable.put(p);
        theTable.close();
    }

    public String table_get(String tableName, String rowKey, String colFamily,
            String col) throws Exception {

        logger.info("Get on: " + tableName + " -> " + rowKey + " " + colFamily + ":" + col);
        HTable theTable = new HTable(hConfig, tableName);
        Get g = new Get(Bytes.toBytes(rowKey));
        Result r = theTable.get(g);
        byte[] value = r.getValue(Bytes.toBytes(colFamily), Bytes.toBytes(col));
        String strVal = Bytes.toString(value);
        theTable.close();
        return strVal;
    }
}
