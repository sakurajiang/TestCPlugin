package com.sakurajiang.test

import javassist.CannotCompileException
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall
import org.gradle.api.Project

//groovy版本的javassist
public class MyInjects {
    //初始化类池
    private final static ClassPool pool = ClassPool.getDefault();

    public static void inject(String path,Project project) {
        //将当前路径加入类池,不然找不到这个Jdk18741874
        // 类
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle");

        File dir = new File(path);
        if (dir.isDirectory()) {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                println("filePath = " + filePath)
                if (file.getName().equals("MainActivity.class")) {

                    //获取MainActivity.class
                    CtClass ctClass = pool.getCtClass("com.example.sakurajiang.testcplugin.MainActivity");
                    println("ctClass = " + ctClass)
                    //解冻
                    if (ctClass.isFrozen())
                        ctClass.defrost()

                    //获取到OnCreate方法
                    CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate")

                    println("方法名 = " + ctMethod)

                    String insetBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
                                                """
                    //在方法开头插入代码
                    ctMethod.insertBefore(insetBeforeStr);
                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }

    }

    public static void replace(String path,Project project) throws CannotCompileException{
        //将当前路径加入类池,不然找不到这个Jdk18741874
        // 类
        System.out.println("path:" +path);
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(project.android.bootClasspath[0].toString());
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.util.Log");

        File dir = new File(path);
        if (dir.isDirectory()) {
            //遍历文件夹
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                println("filePath = " + filePath)
                if (file.getName().equals("MainActivity.class")) {

                    //获取MainActivity.class
                    CtClass ctClass = pool.getCtClass("com.example.sakurajiang.testcplugin.MainActivity");
                    println("ctClass = " + ctClass)
                    //解冻
                    if (ctClass.isFrozen())
                        ctClass.defrost()

                    //获取到OnCreate方法
                    CtMethod ctMethod = ctClass.getDeclaredMethod("move")

                    println("方法名 = " + ctMethod)
//                    CtClass etype = pool.get("java.io.IOException");
//                    def f = "{ System.out.println($e);}";
//                    ctMethod.addCatch(f, etype);
//                    println("f="+f);
                    String insetBeforeStr = $/{ $1 = 0; }/$
                    println("insetBeforeStr="+insetBeforeStr);
                    ctMethod.insertBefore(insetBeforeStr);

//                    ctMethod.setBody(insetBeforeStr);
//                    ctMethod.instrument(new ExprEditor(){
//                        @Override
//                        void edit(MethodCall m) throws CannotCompileException {
//                            super.edit(m)
//                            m.replace("{ $1 = 0; $_ = $proceed($$); }")
//                            m.replace("{ System.out.println(\\\"A read operation on a field is encountered \\\"); \$_ = \$proceed(\$\$); }");
//                            m.replace($/{ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
//                                \*$\*_ = \*$\*proceed($$);
//                                }/$)
//                        }
//                    })
                    ctMethod.getMethodInfo().rebuildStackMap(pool)
                    ctClass.writeFile(path);
//                    MethodCall methodCall = new MethodCall().replace()
//                    String insetBeforeStr = """ android.widget.Toast.makeText(this,"我是被插入的Toast代码~!!",android.widget.Toast.LENGTH_SHORT).show();
//                                                """
//                    在方法开头插入代码
//                    ctMethod.insertBefore(insetBeforeStr);
//                    ctClass.writeFile(path)
                    ctClass.detach()//释放
                }
            }
        }

    }
}
