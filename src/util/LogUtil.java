package util;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * LogUtil，日志辅助工具
 *
 * huang weijing huang weijing 2012-12 -18 下午2:48:09
 *
 * @version 1.0.0
 *
 */
public class LogUtil {

    private static Logger _logger;

    private static void initialize() {
        // 设置日志
        PropertyConfigurator.configure("log4j.properties");
        _logger = Logger.getLogger("sparsetp");
    }

    public static Logger logger() {
        if (_logger == null) {
            initialize();
        }
        return _logger;
    }

    public static void main(String[] args) {
        LogUtil.logger().info("hello");
        LogUtil.logger().debug("aha");
    }

}
