package com.mango.log.aspect;

import com.alibaba.fastjson.JSONObject;
import com.mango.log.annotation.SysLogAnno;
import com.mango.log.constant.SecurityConstants;
import com.mango.log.model.SysLog;
import com.mango.log.model.UserInfo;
import com.mango.log.monitor.AddrUtil;
import com.mango.log.properties.AuditLogProperties;
import com.mango.log.properties.TraceProperties;
import com.mango.log.service.IAuditService;
import com.mango.log.service.IUserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.TtlMDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日志切面
 **/
@Slf4j
@Aspect
@ConditionalOnClass({HttpServletRequest.class, RequestContextHolder.class})
public class SysLogAspect {
    @Value("${spring.application.name}")
    private String applicationName;

    private AuditLogProperties auditLogProperties;
    private IAuditService auditService;
    @Autowired
    private TraceProperties traceProperties;
    @Autowired
    private IUserInfoService iUserInfoService;

    public SysLogAspect(AuditLogProperties auditLogProperties, IAuditService auditService) {
        this.auditLogProperties = auditLogProperties;
        this.auditService = auditService;
    }

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();


    @Pointcut("@within(com.mango.log.annotation.AuditLog)||@annotation(com.mango.log.annotation.AuditLog)")
    public void auditLog() {

    }

    @Around("auditLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //判断功能是否开启
        if (auditLogProperties.getEnabled()) {
            if (auditService == null) {
                log.warn("AuditLogAspect - auditService is null");
                return null;
            }
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

            // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
            Object[] objs = joinPoint.getArgs();
            // 参数名
            String[] argNames = methodSignature.getParameterNames();
            Map<String, Object> paramMap = new HashMap<>(2);
            for (int i = 0; i < objs.length; i++) {
                if (!(objs[i] instanceof ExtendedServletRequestDataBinder) && !(objs[i] instanceof HttpServletResponseWrapper) && !(objs[i] instanceof HttpServletResponse) && !(objs[i] instanceof HttpServletRequest)
                        && !(objs[i] instanceof BeanPropertyBindingResult)
                        && !(objs[i] instanceof ServletRequest)) {
                    paramMap.put(argNames[i], objs[i]);
                }
            }

            Method method = methodSignature.getMethod();
            SysLogAnno annotation = method.getAnnotation(SysLogAnno.class);
            String classMethod = joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName();

            SysLog sysLog = new SysLog();
            sysLog.setApplicationName(applicationName);
            if (traceProperties.getEnable()) {
                String traceId = TtlMDCAdapter.getInstance().get(SecurityConstants.LOG_TRACE_ID);
                sysLog.setTraceId(traceId);
            }
            sysLog.setClassMethod(classMethod);
            sysLog.setCreateTime(new Date());
            sysLog.setIp(AddrUtil.getRemoteAddr(request));
            String operation = annotation.operation();
            if (operation.contains("#")) {
                /* 获取方法参数值 */
                Object[] args = joinPoint.getArgs();
                /* @AuditLog(operation = "'更改租户类型:'+#id")-> 更改租户类型:11111 */
                operation = getValBySpEL(operation, methodSignature, args);
            }
            sysLog.setOperation(operation);
            sysLog.setParams(JSONObject.toJSONString(paramMap));
            sysLog.setRequestMethod(request.getMethod());
            sysLog.setRequestUrl(request.getRequestURI());
            if (traceProperties.getTransferUser()) {
                UserInfo userInfo = iUserInfoService.getUserInfo(request);
                if (null != userInfo) {
                    sysLog.setUserId(userInfo.getUserId());
                    sysLog.setUserName(userInfo.getUserName());
                    sysLog.setCustomerId(userInfo.getCustomerId());
                }
            }
            long start = System.currentTimeMillis();
            Object o;
            try {
                o = joinPoint.proceed();
            } catch (Exception e) {
                long end = System.currentTimeMillis();
                sysLog.setCostTime(end - start);
                sysLog.setIsException(1);
                sysLog.setException(e.toString());
                e.printStackTrace();
                auditService.save(sysLog);
                return e;
            }
            long end = System.currentTimeMillis();
            sysLog.setCostTime(end - start);
            sysLog.setIsException(0);
            auditService.save(sysLog);
            return o;
        } else {
            return joinPoint.proceed();
        }
    }

    /**
     * 解析spEL表达式
     */
    private String getValBySpEL(String spEL, MethodSignature methodSignature, Object[] args) {
        //获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
        if (paramNames != null && paramNames.length > 0) {
            Expression expression = spelExpressionParser.parseExpression(spEL);
            // spring的表达式上下文对象
            EvaluationContext context = new StandardEvaluationContext();
            // 给上下文赋值
            for (int i = 0; i < args.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
            return expression.getValue(context).toString();
        }
        return null;
    }

}
