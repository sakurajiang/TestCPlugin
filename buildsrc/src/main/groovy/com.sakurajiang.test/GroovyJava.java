package com.sakurajiang.test;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.BadBytecode;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

//java版本的javassist
public class GroovyJava {

    private final static ClassPool pool = ClassPool.getDefault();
    static List<File> list = new ArrayList<File>();
    static HashSet<String> set = new HashSet();

    public static int t(){
        return 5;
    }

    public static void replace(String path,String classPath) throws CannotCompileException, NotFoundException, BadBytecode, IOException {
        System.out.println("path:" +path);
        pool.appendClassPath(path);
        //project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        pool.appendClassPath(classPath);
        //引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.util.Log");

        File dir = new File(path);
        if (dir.isDirectory()) {
            List<File> fileList = getAllFile(path);
            System.out.println("fileList:" +fileList);
            for (int i = 0; i < fileList.size(); i++) {
                if (fileList.get(i).getName().equals("MainActivity.class")) {
                    System.out.println("fileListi:" +fileList.get(i));
                    //获取MainActivity.class
                    CtClass ctClass = pool.getCtClass("com.example.sakurajiang.testcplugin.MainActivity");
                    //解冻
                    if (ctClass.isFrozen()) {
                        ctClass.defrost();
                    }
                    //获取到OnCreate方法
                    CtMethod ctMethod = ctClass.getDeclaredMethod("move");
                    CtMethod[] ctMethods = ctClass.getMethods();
                    System.out.println(""+ctMethods.length);
                    System.out.println(ctMethod.getMethodInfo());
                    System.out.println(ctMethod.getMethodInfo2());
                    System.out.println(""+ctClass.getDeclaredMethods());
                    System.out.println(""+ctClass.getDeclaredMethods().length);
                    System.out.println(ctClass.getFields().length);
//                    System.out.println(ctClass.getField(""));
                    System.out.println(ctClass.getField("x").getFieldInfo2().getConstantValue());
                    System.out.println(ctClass.getField("y").getFieldInfo2().getConstantValue());
//                    String insetBeforeStr = "{ $1 = 0;}";
//                    ctMethod.insertBefore(insetBeforeStr);
//                    ctMethod.instrument(new ExprEditor(){
//                        @Override
//                        public void edit(MethodCall m) throws CannotCompileException {
//                            super.edit(m);
//                            System.out.println("fileListi:s2s2wdw");
//                            m.replace("{ $1 = 0; $_ = $proceed($$); }");
//                        }
//                    });
//                    CtClass etype = pool.get("java.io.IOException");
//                    ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
//                    String insetBeforeStr = "{ $1 = 0;}";
//                    ctMethod.setBody(insetBeforeStr);
                    ctMethod.insertBefore("{ System.out.println(\"jiji\"); }");
                    ctMethod.getMethodInfo().rebuildStackMap(pool);
                    ctClass.writeFile(path);
                    ctClass.detach();//释放
                }
            }
        }

    }

    public static void replaceClassFromJar(String path,String classPath) throws NotFoundException, CannotCompileException, BadBytecode, IOException {
        System.out.println("path:" +path);
        pool.appendClassPath(path);
        pool.appendClassPath(classPath);
        getFileFromJar(path);
        for(String s:set){
            System.out.println("s:" +s);
            if(s.equals("android.arch.lifecycle.LifecycleRegistry")){
                System.out.println("666666:" +s);
                CtClass ctClass = pool.getCtClass(s);
                //解冻
                if (ctClass.isFrozen()) {
                    ctClass.defrost();
                }
                CtMethod ctMethod = ctClass.getDeclaredMethod("getCurrentState");
                System.out.println(ctMethod.getLongName());
//                ctMethod.insertBefore("{ System.out.println(\"jiji\"); }");
                CtClass etype = pool.get("java.io.IOException");
                ctMethod.addCatch("{ System.out.println($e); throw $e; }", etype);
                System.out.println("path111:" +path);
//                ctMethod.getMethodInfo().rebuildStackMap(pool);
//                ctClass.writeFile(path);
//                ctClass.detach();//释放
            }
        }
    }

    public static List<File> getAllFile(String path){
        File dir = new File(path);
        File[] tempList = dir.listFiles();
        for(int i=0;i<tempList.length;i++){
            if(tempList[i].isFile()){
                list.add(tempList[i]);
            }
            if(tempList[i].isDirectory()){
                getAllFile(tempList[i].getAbsolutePath());
            }
        }
        System.out.println("list:" +list);
        return list;
    }

    public static void getFileFromJar(String path){
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(path);
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            while (jarEntryEnumeration.hasMoreElements()){
                JarEntry jarEntry = jarEntryEnumeration.nextElement();
                if(!jarEntry.isDirectory()){
                    /*
                     * 这里是为了方便，先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug
                     * (FIXME: 先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug)
                     */
                    String entryName = jarEntry.getName().replace("/", ".");
                    if (entryName.endsWith(".class")) {
                        entryName = entryName.replace(".class", "");
                        System.out.println("class:" +entryName);
                        set.add(entryName);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
