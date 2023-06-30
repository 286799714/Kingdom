package com.maydaymemory.kingdom.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;
import com.maydaymemory.kingdom.PluginKingdom;
import com.maydaymemory.kingdom.core.config.ConfigInject;
import com.maydaymemory.kingdom.data.extend.HikariCPConnectionSource;
import com.maydaymemory.kingdom.model.chunk.ChunkInfo;
import com.maydaymemory.kingdom.model.player.PlayerInfo;
import com.maydaymemory.kingdom.model.region.PrivateRegion;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.Configuration;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SQLDataLoader implements DataLoader{
    @ConfigInject
    private static Configuration config;

    private HikariCPConnectionSource connectionSource;

    private Dao<PrivateRegion, String> privateRegionDao;

    private Dao<ChunkInfo, String> chunkInfoDao;

    private Dao<PlayerInfo, UUID> playerInfoDao;

    private static int serialNumber = 1;

    public void initial(SQLType type){
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setConnectionTimeout(config.getInt("data.cp.timeout", 30000));
        hikariConfig.setMinimumIdle(config.getInt("data.cp.minimum-idle", 10));
        hikariConfig.setMaximumPoolSize(config.getInt("data.cp.maximum-pool-size", 50));
        String databaseUrl;
        switch (type){
            case MYSQL:
                databaseUrl = "jdbc:mysql://"
                        + config.getString("data.ip", "127.0.0.1")
                        + ":" + config.getString("data.port", "3306")
                        + "/" + config.getString("data.db-name", "kingdom")
                        + "?characterEncoding=utf-8";
                hikariConfig.setDriverClassName("com.mysql.jdbc.Driver");
                break;
            case SQLITE:
                databaseUrl = "jdbc:sqlite:" + PluginKingdom.getInstance().getDataFolder().getPath() + "\\kingdom.db";
                hikariConfig.setDriverClassName("org.sqlite.JDBC");
                break;
            default:
                return;
        }
        hikariConfig.setPoolName("HikariPool-Kingdom-" + serialNumber++);
        hikariConfig.setJdbcUrl(databaseUrl);
        hikariConfig.setUsername(config.getString("data.user"));
        hikariConfig.setPassword(config.getString("data.password"));
        hikariConfig.setAutoCommit(true);
        HikariDataSource hds = new HikariDataSource(hikariConfig);
        try {
            connectionSource = new HikariCPConnectionSource(hds, databaseUrl);

            TableUtils.createTableIfNotExists(connectionSource, PrivateRegion.class);
            TableUtils.createTableIfNotExists(connectionSource, ChunkInfo.class);
            TableUtils.createTableIfNotExists(connectionSource, PlayerInfo.class);

            privateRegionDao = DaoManager.createDao(connectionSource, PrivateRegion.class);
            chunkInfoDao = DaoManager.createDao(connectionSource, ChunkInfo.class);
            playerInfoDao = DaoManager.createDao(connectionSource, PlayerInfo.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PrivateRegion> loadPrivateRegions() {
        try {
            return privateRegionDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePrivateRegions(Collection<PrivateRegion> regions) {
        try {
            for(PrivateRegion region : regions){
                privateRegionDao.createOrUpdate(region);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ChunkInfo> loadAllChunkInfo() {
        try {
            return chunkInfoDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAllChunkInfo(Collection<ChunkInfo> chunkInfo) {
        try {
            for(ChunkInfo info : chunkInfo){
                if(info.isClaimed())
                    chunkInfoDao.createOrUpdate(info);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PlayerInfo> loadAllPlayerInfo() {
        try {
            return playerInfoDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveAllPlayerInfo(Collection<PlayerInfo> playerInfo) {
        try {
            for(PlayerInfo info : playerInfo){
                playerInfoDao.createOrUpdate(info);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void discard() {
        connectionSource.close();
    }

    public enum SQLType{
        SQLITE, MYSQL;
    }
}
