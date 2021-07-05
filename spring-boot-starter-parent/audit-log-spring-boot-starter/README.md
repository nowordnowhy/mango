# **通用日志链路Starter**


## 1.使用方法
   1.引入pom文件
   
    <!-- com.mango.log starter -->
    <dependency>
        <groupId>com.vcgplus</groupId>
        <artifactId>audit-com.mango.log-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</pre>
    2.默认用户信息获取-默认从header获取相关信息<br>
      可自定义IUserInfoService实现方法来重新定义信息获取方式
        
    
    @Service
    public class UserInfoServiceImpl implements IUserInfoService {
        @Override
        public UserInfo getUserInfo(HttpServletRequest request) {
            String userId = request.getHeader(SecurityConstants.USER_ID_HEADER);
            String userName = request.getHeader(SecurityConstants.USER_NAME_HEADER);
            String customerId = request.getHeader(SecurityConstants.CUSTOMER_HEADER);
            UserInfo userInfo = new UserInfo();
            userInfo.setUserId(userId);
            userInfo.setUserName(userName);
            userInfo.setCustomerId(customerId);
            return userInfo;
        }
    }
</pre>
    3.yml配置<br>
    默认关闭,可以通过改变 mango.audit-com.mango.log.enabled 的值控制开关<br>
    链路trace默认关闭 可通过 trace.enable 开关 
    
    mango:
      audit-com.mango.log:
        com.mango.log-type: logger
        enabled: true
        datasource:
          driver-class-name: com.mysql.jdbc.Driver
          jdbc-url: jdbc:mysql://localhost:3306/com.mango.log?allowMultiQueries=true&useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
          username: ******
          password: ******
      trace:
        enable: true
</pre>
    4.注解使用支持SpEL表达式
    
    @AuditLog(operation = "'查看用户信息:'+#id")-> 查看用户信息:11111
    @GetMapping("viewUserInfo")
    public User viewUserInfo (@RequestParam(value="id") Integer id)){
    }
    
释: 本项目基于 https://gitee.com/zhou_wenxuan/central-platform.git 部分功能开发
    
        
