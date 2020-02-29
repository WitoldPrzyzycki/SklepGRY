package com.mycompany.shop.ejb.interceptor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.InvocationContext;

public class LoggingInterceptor {

    @Resource
    private SessionContext sessionContext;

    @AroundInvoke
    public Object additionalInvokeForMethod(InvocationContext invocation) throws Exception {
        return loggingMechanism(invocation);
    }

    @AroundTimeout
    public Object additionalTimeoutForMethod(InvocationContext invocation) throws Exception {
        return loggingMechanism(invocation);
    }

    private Object loggingMechanism(InvocationContext invocation) throws Exception {
        StringBuilder sb = new StringBuilder("Wywołanie metody biznesowej "
                + invocation.getTarget().getClass().getName() + "."
                + invocation.getMethod().getName());
        sb.append(" z tożsamością: " + sessionContext.getCallerPrincipal().getName());
        try {
            Object[] parameters = invocation.getParameters();
            if (null != parameters) {
                for (Object param : parameters) {
                    if (param != null) {
                        sb.append(" z parametrem " + param.getClass().getName() + "=" + param.toString());
                    } else {
                        sb.append(" z parametrem null");
                    }
                }
            }

            Object result = invocation.proceed();

            if (result != null) {
                sb.append(" zwrócono " + result.getClass().getName() + "=" + result.toString());
            } else {
                sb.append(" zwrócono wartość null");
            }

            return result;
        } catch (Exception ex) {
            sb.append(" wystapil wyjatek " + ex);
            throw ex; 
        } finally {
            Logger.getGlobal().log(Level.INFO, sb.toString());
        }
    }
}
