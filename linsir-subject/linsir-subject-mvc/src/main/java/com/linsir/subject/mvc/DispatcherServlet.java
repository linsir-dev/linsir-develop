package com.linsir.subject.mvc;

import com.linsir.subject.mvc.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ Author     ：linsir
 * @ Date       ：Created in 2018/9/1 23:50
 * @ Description：description
 * @ Modified By：
 * @ Version: 1.0.0
 */
public class DispatcherServlet extends HttpServlet{


    List<String> classNames=new ArrayList<String>();

    Map<String,Object> beans=new HashMap<String,Object>();

    Map<String,Object> handlerMap=new HashMap<String,Object>();



    public  void  init(ServletConfig config)
    {
        scanPackage("com.linsir.subject.mvc");
        doInstance();
        doIOC();
        buildUrlMapping();
    }

    // 扫描 com.linsr
    private  void  scanPackage(String basePackage)
    {

        String p="/"+basePackage.replace(".","/");

        URL url=this.getClass().getClassLoader().getResource("/"+basePackage.replace(".","/"));

        String fileStr=url.getFile();

        File file= new File(fileStr);

        String[] filesStr=file.list();

        for (String path:filesStr)
        {
          File filePath= new File(fileStr+path);

            System.out.println("================="+filePath+filePath.isDirectory());

            if (filePath.isDirectory())
          {
              scanPackage(basePackage+"."+path);
          }
          else
          {
                //           加入到List
              classNames.add(basePackage+"."+path);
          }
        }
    }

    private void doInstance()
    {
        if (classNames.size()<=0)
        {
            System.out.println("扫描失败");
            return;
        }

//        对类进行处理
        for(String className:classNames)
        {
            String cn=className.replace(".class","");

            try {
                Class<?> clazz=Class.forName(cn);

                if (clazz.isAnnotationPresent(LinsirController.class))
                {
                    Object instace=clazz.newInstance(); //创造好实例化对象
                    LinsirRquestMapping rquestMapping  = clazz.getAnnotation(LinsirRquestMapping.class);  //获取注解
                    String rmvalue=rquestMapping.value();
                    beans.put(rmvalue,instace);
                }
                if (clazz.isAnnotationPresent(LinsirService.class))
                {
                    Object instace=clazz.newInstance();
                    LinsirService service=clazz.getAnnotation(LinsirService.class);
                    beans.put(service.value(),instace);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }

 //    注入
    public void doIOC()
    {
        if(beans.entrySet().size()<=0)
        {
            System.out.print(" 没有实例化类");
        }

        for (Map.Entry<String,Object> entry :beans.entrySet() )
        {
            Object instance= entry.getValue();
            Class<?> clazz=instance.getClass();

            if (clazz.isAnnotationPresent(LinsirController.class))
            {
                Field[] fields=clazz.getDeclaredFields();

                for (Field field :fields)
                {
                    if (field.isAnnotationPresent(LinsirAutowired.class))
                    {
                        LinsirAutowired autowired=field.getAnnotation(LinsirAutowired.class);
                        String key= autowired.value();
                        field.setAccessible(true);
                        try {
                            field.set(instance,beans.get(key));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else
                    {
                        continue;
                    }
                }
            }else
            {
                continue;
            }
        }
    }

    private void buildUrlMapping()
    {
        if (beans.size()<=0)
        {
            System.out.println(" 没有实例化对象========");
            return;
        }
        for (Map.Entry<String,Object> entry : beans.entrySet())
        {
            Object instance= entry.getValue();
            Class<?>  clazz =instance.getClass();
            if (clazz.isAnnotationPresent(LinsirController.class))
            {
                LinsirRquestMapping linsirRquestMapping=clazz.getAnnotation(LinsirRquestMapping.class);
                String classPath=linsirRquestMapping.value();
                Method[] methods=clazz.getMethods();

                for (Method method : methods)
                {
                    if(method.isAnnotationPresent(LinsirRquestMapping.class))
                    {
                        LinsirRquestMapping methodRquestMapping=method.getAnnotation(LinsirRquestMapping.class);
                        String methodPath=methodRquestMapping.value();
                        handlerMap.put(classPath+methodPath,method);
                    }
                    else
                    {
                        continue;
                    }
                }

            }else
            {
                continue;
            }
        }

    }



        /**
         *  处理参数
         * create by: linsir
         * create time: 10:57 2018/9/2
         * params:
         * @return
         */
    private static Object[] hand(HttpServletRequest req,HttpServletResponse resp,Method method)
    {
        Class<?>[] paramClazzs=method.getParameterTypes();

        Object[] args=new Object[paramClazzs.length];
        int arg_i=0;
        int index=0;
        for (Class<?> paramClazz : paramClazzs)
        {
            if (ServletRequest.class.isAssignableFrom(paramClazz))
            {
                args[arg_i++]=req;
            }
            if (ServletResponse.class.isAssignableFrom(paramClazz))
            {
                args[arg_i++]=resp;
            }

            Annotation[] paramAns=method.getParameterAnnotations()[index];

            if (paramAns.length>0)
            {
                for (Annotation paramAn : paramAns)
                {
                    if (LinsirRquestParam.class.isAssignableFrom(paramAn.getClass()))
                    {
                        LinsirRquestParam rp=(LinsirRquestParam) paramAn;
                        args[arg_i++]=req.getParameter(rp.value());
                    }
                }
            }
            index++;
        }
        return args;
    }



    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        String uri=req.getRequestURI();
        String context=req.getContextPath();
        String path=uri.replace(context,"");

        Method method=(Method) handlerMap.get(path);

        Object instance= beans.get("/"+path.split("/")[1]);

        Object arg[]=hand(req,resp,method);

        try {
            method.invoke(instance,arg);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

}
