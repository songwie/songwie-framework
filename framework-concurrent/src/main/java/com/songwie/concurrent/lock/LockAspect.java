package com.songwie.concurrent.lock;
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LockAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LockAspect.class);

	 
	@Around("within(com.mogoroom..*) && @annotation(myLock)")  
	public Object addLock(ProceedingJoinPoint point, DistributedLockAnnotation myLock) throws Throwable {
		Object object = null;
		try {
			 String key = myLock.key();
			 int timeout = myLock.timeout()==0?3:myLock.timeout();
			 //拦截的实体类
			 Object target = point.getTarget();
			 //拦截的方法名称
			 String methodName = point.getSignature().getName();
			 //拦截的方法参数
			 Object[] args = point.getArgs();
			 //拦截的放参数类型
			 Class[] parameterTypes = ((MethodSignature)point.getSignature()).getMethod().getParameterTypes();

			 Method m = null;
			 try {
				 //通过反射获得拦截的method
				 m = target.getClass().getMethod(methodName, parameterTypes);
				 //如果是桥则要获得实际拦截的method
				 if(m.isBridge()){
					 for(int i = 0; i < args.length; i++){
						 //获得泛型类型
						 Class genClazz = GenericsUtils.getSuperClassGenricType(target.getClass());
						 //根据实际参数类型替换parameterType中的类型
						 if(args[i].getClass().isAssignableFrom(genClazz)){
							 parameterTypes[i] = genClazz;
						 }
					 }
					 //获得parameterType参数类型的方法
					 m = target.getClass().getMethod(methodName, parameterTypes);
				 }
			 } catch (SecurityException e) {
				 throw new Exception(e);
			 } catch (NoSuchMethodException e) {
				 throw new Exception(e);
			 }
			 int k=-1;
			 for (int i = 0; i < m.getParameterTypes().length; i++) {
	        	if(m.getParameterAnnotations()[i].length>0){
	        		Object param = m.getParameterAnnotations()[i][0];
	        		if(param instanceof LockKey){
	        			k=i;
	        			break;
	        		}
	        	}
		     }
             if(k!=-1){
            	 key = key + "-" + point.getArgs()[k]==null?"":point.getArgs()[k].toString();
             }
			 DistributedLock.acquire(key, timeout);

			 object = point.proceed(point.getArgs());

			 DistributedLock.release(key);
		} catch (Exception e) {
			LOGGER.warn("addLock error ",e);
			throw new Exception(e);
		}


		 return object;
	}

}
