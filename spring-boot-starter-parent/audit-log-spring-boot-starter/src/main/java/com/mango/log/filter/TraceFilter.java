package com.mango.log.filter;

import cn.hutool.core.util.IdUtil;
import com.mango.log.constant.SecurityConstants;
import com.mango.log.model.UserInfo;
import com.mango.log.properties.TraceProperties;
import com.mango.log.service.IUserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.TtlMDCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

import static com.mango.log.constant.SecurityConstants.LOG_TRACE_ID;
import static com.mango.log.constant.SecurityConstants.TRACE_ID_HEADER;


/**
 * 生成日志链路追踪id，并传入header中
 **/
@Component
@WebFilter(filterName = "traceFilter", urlPatterns = "/*")
public class TraceFilter implements Filter {
    private static TraceFilter traceFilter;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private TraceProperties traceProperties;

    @Autowired
    private IUserInfoService iUserInfoService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("init-----------TraceFilter");
    }

    @PostConstruct
    public void init() {
        traceFilter = this;
        traceFilter.iUserInfoService = this.iUserInfoService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HeaderRequestWrapper headerRequestWrapper = new HeaderRequestWrapper(request);
        if (traceProperties.getEnable()) {
            String traceId = request.getHeader(TRACE_ID_HEADER);
            if (StringUtils.isEmpty(traceId)) {
                //链路追踪id
                traceId = IdUtil.fastSimpleUUID();
                TtlMDCAdapter.getInstance().put(LOG_TRACE_ID, traceId);
                headerRequestWrapper.addHeader(TRACE_ID_HEADER, traceId);
            }
        }
        if (traceProperties.getTransferUser()) {
            UserInfo userInfo = iUserInfoService.getUserInfo(request);
            if (null != userInfo) {
                headerRequestWrapper.addHeader(SecurityConstants.USER_ID_HEADER, userInfo.getUserId());
                headerRequestWrapper.addHeader(SecurityConstants.USER_NAME_HEADER, userInfo.getUserName());
                headerRequestWrapper.addHeader(SecurityConstants.CUSTOMER_HEADER, userInfo.getCustomerId());
            }
        }
        filterChain.doFilter(headerRequestWrapper, servletResponse);
    }

    @Override
    public void destroy() {
        TtlMDCAdapter.getInstance().clear();
        logger.info("destroy-----------TraceFilter");
    }

    public class HeaderRequestWrapper extends HttpServletRequestWrapper {

        public HeaderRequestWrapper(HttpServletRequest request) {
            super(request);
            //将参数表，赋予给当前的Map以便于持有request中的参数
            this.params.putAll(request.getParameterMap());
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String values = request.getHeader(name);
                    addHeader(name, values);
                }
            }
        }

        private Map<String, String> headerMap = new HashMap<String, String>();
        private Map<String, String[]> params = new HashMap<String, String[]>();

        public void addHeader(String name, String value) {
            headerMap.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = super.getHeader(name);
            if (headerMap.containsKey(name)) {
                headerValue = headerMap.get(name);
            }
            return headerValue;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> names = Collections.list(super.getHeaderNames());
            for (String name : headerMap.keySet()) {
                names.add(name);
            }
            return Collections.enumeration(names);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> values = Collections.list(super.getHeaders(name));
            if (headerMap.containsKey(name)) {
                values.add(headerMap.get(name));
            }
            return Collections.enumeration(values);
        }

        @Override

        public String getParameter(String name) {//重写getParameter，代表参数从当前类中的map获取

            String[] values = params.get(name);

            if (values == null || values.length == 0) {

                return null;

            }

            return values[0];

        }


        @Override
        public String[] getParameterValues(String name) {//同上

            return params.get(name);

        }
    }
}
