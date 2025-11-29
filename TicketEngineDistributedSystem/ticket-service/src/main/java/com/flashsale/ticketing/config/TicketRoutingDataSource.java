package com.flashsale.ticketing.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;

public class TicketRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        String shardKey = DbContext.getDbType(); // SHARD_1 or SHARD_2

        if (shardKey == null) {
            return "SHARD_1_PRIMARY"; // Default fallback
        }

        // Check if current transaction is read-only
        boolean isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

        if (isReadOnly) {
            String key = shardKey + "_REPLICA";
            System.out.println(">>> 📖 Reading from: " + key);
            return key;
        } else {
            String key = shardKey + "_PRIMARY";
            System.out.println(">>> 📝 Writing to: " + key);
            return key;
        }
    }

    @Override
    protected DataSource determineTargetDataSource() {
        DataSource ds = super.determineTargetDataSource();

        if (ds instanceof HikariDataSource) {
            HikariDataSource hikari = (HikariDataSource) ds;
            System.out.println(">>> 🔌 Connecting to URL: " + hikari.getJdbcUrl());
        }

        return ds;
    }
}