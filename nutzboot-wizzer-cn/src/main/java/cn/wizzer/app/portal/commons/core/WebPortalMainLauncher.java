package cn.wizzer.app.portal.commons.core;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.annotation.Modules;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Created by wizzer on 2018/3/17.
 */
@IocBean(create = "init", depose = "depose")
@Modules(packages = "cn.wizzer")
public class WebPortalMainLauncher {
    private static final Log log = Logs.get();
    @Inject("refer:$ioc")
    private Ioc ioc;
    @Inject
    private Dao dao;
    @Inject
    private PropertiesProxy conf;
    public static void main(String[] args) throws Exception {
        NbApp nb = new NbApp().setArgs(args).setPrintProcDoc(true);
        nb.getAppContext().setMainPackage("cn.wizzer");
        nb.run();
    }

    public void init() {

    }


    public void depose() {
        // 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
        try {
            Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
        } catch (Throwable e) {
        }
        // 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
        // 多webapp共享mysql驱动的话,以下语句删掉
        Enumeration<Driver> en = DriverManager.getDrivers();
        while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                log.debug("deregisterDriver: " + className);
                DriverManager.deregisterDriver(driver);
            } catch (Exception e) {
            }
        }
    }
}