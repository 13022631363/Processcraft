package cn.gionrose.facered.database

import cn.gionrose.facered.ProcessCraft
import cn.gionrose.facered.configure.DatabaseProcess
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

object MysqlWrapper
{



    var dataSource: HikariDataSource


    init {

        val config = HikariConfig ().apply {
            jdbcUrl = DatabaseProcess.getUrl()
            username = DatabaseProcess.getUsername()
            password = DatabaseProcess.getPassword()
            maxLifetime = 1200000
        }
        try {
            dataSource = HikariDataSource(config)
        }catch (e: Exception)
        {
            throw  RuntimeException ("请修改 database.yml 中的信息 ...")
        }
        ProcessCraft.INSTANCE.logger.info("processcraft => 数据库链接成功 ...")
    }

    fun connection (): Connection
    {
        return dataSource.connection
    }

}