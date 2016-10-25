/**
 * 
 */
package com.songwie.boot;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.env.Environment;
 
/**
 * @brief
 * @details
 * @author songwie
 * @date 2016年8月27日
 */
@Configurable
public class StartUp {
	private static Object object = new Object();

    private static void printMogo() {
        try {
            System.err.println();
            System.err.println("====================================================================         ");
            System.err.println("::    songwie  startup success   (v1.0.RELEASE)   ");
            
            synchronized(object){
            	object.wait();
            }
            //System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void startUp(Class clazz, String[] args) {
        ConfigurableApplicationContext cac = SpringApplication.run(clazz, args);
        printMogo();
        cac.addApplicationListener(new ApplicationListener<ContextClosedEvent>() {
            @Override
            public void onApplicationEvent(ContextClosedEvent event) {
            	System.err.println("songwie  shutdown success");
            }
        });

    }

    public static void callBack(Environment env) {
        
    }
   
}
