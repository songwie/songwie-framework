package com.songwie.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 功能描述：获取spring容器，以访问容器中定义的其他bean
 * @Description
 * @date 2014年10月13日 下午3:39:57 
 */
@Service
public class SpringContextUtil implements ApplicationContextAware {

	//以静态变量保存Spring ApplicationContext, 可在任何代码任何地方任何时候中取出ApplicaitonContext
	private static ApplicationContext applicationContext; //Spring应用上下文环境    
	
	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境  
	 * @Description
	 * @author shixinxin
	 * @param applicationContext
	 * @throws BeansException
	 * @date 2014年10月13日 下午3:40:23
	 */
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringContextUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取对象
	 * @Description
	 * @return
	 * @author shixinxin
	 * @date 2014年10月13日 下午3:41:31
	 */
	public static ApplicationContext getApplicationContext ()
	{
		return applicationContext;
	}
	
	/**
	 * 获取实例
	 * @Description
	 * @param name 
	 * 		一个以所给名字注册的bean的实例   
	 * @return
	 * @throws BeansException
	 * @author shixinxin
	 * @date 2014年10月13日 下午3:42:26
	 */
    public static Object getBean(String name) throws BeansException { 
            return applicationContext.getBean(name); 
    } 
    
    /**
     * 获取类型为requiredType的对象   
     * @param requiredType 返回对象类型 
     * @return 返回requiredType类型对象
     * @throws BeansException
     * @date 2016年07月14日 
     */
    public static Object getBean(Class requiredType)
	            throws BeansException { 
	    return applicationContext.getBean(requiredType); 
	}
    
    /**   
     * 获取类型为requiredType的对象   
     * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）   
     * @param name       
     * 		bean注册名   
     * @param requiredType 
     * 		返回对象类型   
     * @return Object 
     * 		返回requiredType类型对象   
     * @throws BeansException
     * @author shixinxin
     * @date 2014年10月13日 下午3:42:26  
     */ 
    public static Object getBean(String name, Class requiredType) 
                    throws BeansException { 
            return applicationContext.getBean(name, requiredType); 
    } 
    
    /**   
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true    
     * @param name   
     * @return boolean
     * @author shixinxin
     * @date 2014年10月13日 下午3:42:26   
     */ 
    public static boolean containsBean(String name) { 
            return applicationContext.containsBean(name); 
    } 
    
    /**   
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。   
     * 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）      
     * @param name   
     * @return boolean   
     * @throws NoSuchBeanDefinitionException
     * @author shixinxin
     * @date 2014年10月13日 下午3:42:26  
     */ 
    public static boolean isSingleton(String name) 
                    throws NoSuchBeanDefinitionException { 
            return applicationContext.isSingleton(name); 
    } 
    
    /**   
     * @param name   
     * @return Class 注册对象的类型   
     * @throws NoSuchBeanDefinitionException
     * @author shixinxin
     * @date 2014年10月13日 下午3:42:26   
     */ 
    public static Class getType(String name) 
                    throws NoSuchBeanDefinitionException { 
            return applicationContext.getType(name); 
    } 
    
    /**   
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名      
     * @param name   
     * @return   
     * @throws NoSuchBeanDefinitionException
     * @author shixinxin
     * @date 2014年10月13日 下午3:42:26   
     */ 
    public static String[] getAliases(String name) 
                    throws NoSuchBeanDefinitionException { 
            return applicationContext.getAliases(name); 
    } 
}
