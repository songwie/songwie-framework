package com.songwie.log.minitor;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.songwie.log.Logger;
import com.songwie.log.LoggerFactory;
import com.songwie.log.Request;

 
@Aspect
@Component("newInvokeAspect")
public class InvokeAspect {
    //private ThreadLocal<Transaction> invokeLocal = new ThreadLocal<Transaction>();
    
    private static Logger logger = LoggerFactory.getLogger(InvokeAspect.class);

	//定义一个日志切入点
    @Pointcut(value="execution(public * com.mogoroom..*.*(..)) and  !execution( public *  com.mogoroom.service.*.dao..*.*(..))")
	public void pointCut(){ }
		
    
    @Around("pointCut()")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
    	beforeMothod(joinPoint);
    	
    	Object returnValue = null;
    	try {
            returnValue = joinPoint.proceed(joinPoint.getArgs());
		} catch (Exception e) {
			//Cat.logError("MethodExcuteStash.Exception", e);
			logger.warn("MethodExcuteStash.Exception",e);
			throw e;
		}finally {
			afterMothod(joinPoint, returnValue);
		}

        return returnValue ;
    }
    
	private void beforeMothod(JoinPoint joinPoint) {
		try { 
			Request.initId();

			String methods = Request.getId() +"-"+ joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
			String args = Request.getId() +"-"+ Arrays.toString(joinPoint.getArgs());
			if(args!=null && args.length()>1000){
				args = args.length()+"";
			}
			if("convertPublicContentToBrief".equals(methods)){
				args = "convertPublicContentToBrief";
			}
			MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();    
			Class<?> clazz = methodSignature.getMethod().getDeclaringClass();
		    if(clazz.isAnnotationPresent(Transactional.class)){    
		        //获取方法上注解中表明的权限    
		    	Transactional transactional = (Transactional) clazz.getAnnotation(Transactional.class);   
				logger.info("MethodExcuteStash.Transactional "+ transactional.toString());
		    }
			
			try {
				logger.info("MethodExcuteStash.Detail "+ methods);
			} catch (Exception e) { }
			
			logger.info("MethodExcuteStash.Class "+ joinPoint.getSignature().getDeclaringTypeName());
			logger.info("MethodExcuteStash.Method "+ joinPoint.getSignature().getName());
			logger.info("MethodExcuteStash.Args "+ args);

		} catch(Exception e) {
			try{
				//Cat.logError(e);
			}catch(Exception e1){}
		} 
	}
	private void afterMothod(JoinPoint joinPoint,Object returnValue) {
		try { 
			if(returnValue!=null && returnValue.toString()!=null ){
				logger.info("MethodExcuteStash.Return Size "+ returnValue.toString().length());
			}
		} catch(Exception e) {
			try{
				//Cat.logError(e);
			}catch(Exception e1){}
		} 	
	}
	
}
