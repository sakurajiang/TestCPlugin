package com.sakurajiang.test

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin


//生成类的插件
public class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        def log = project.logger
        log.error "========================";
        log.error "精简的MyPlugin，开始修改Class!";
        log.error "========================";
        def android = project.extensions.getByType(AppExtension);
        android.registerTransform(new MyTransform(project));
        project.extensions.create("testCreatJavaConfig",MyPluginTestClass.class);
        if(project.plugins.hasPlugin(AppPlugin)){
            android.applicationVariants.all { variant ->
                //获取到scope,作用域
                def variantData = variant.variantData
                def scope = variantData.scope

                //拿到build.gradle中创建的Extension的值
                def config = project.extensions.getByName("testCreatJavaConfig");

                //创建一个task
                def createTaskName = scope.getTaskName("MyTestPlugin")
                def createTask = project.task(createTaskName)
                //设置task要执行的任务
                createTask.doLast {
                    //生成java类
                    createJavaTest(variant, config)
                }
                //设置task依赖于生成BuildConfig的task，然后在生成BuildConfig后生成我们的类
                String generateBuildConfigTaskName = variant.getVariantData().getScope().getGenerateBuildConfigTask().name
                def generateBuildConfigTask = project.tasks.getByName(generateBuildConfigTaskName)
                if (generateBuildConfigTask) {
                    createTask.dependsOn generateBuildConfigTask
                    generateBuildConfigTask.finalizedBy createTask
                }
            }
        }
        System.out.println("------------------结束了----------------------");
    }

    static def void createJavaTest(variant, config) {
        //要生成的内容
        def content = """package com.example.sakurajiang.testcplugin;

                        /**
                         * Created by sakurajiang on 2018/8/1.
                         */

                        public class MyPluginTestClass {
                            public static final String str = "${config.str}";
                        }
                        """;
        //获取到BuildConfig类的路径
        File outputDir = variant.getVariantData().getScope().getBuildConfigSourceOutputDir()

        def javaFile = new File(outputDir, "MyPluginTestClass.java")

        javaFile.write(content, 'UTF-8');
    }
}

class MyPluginTestClass {
    def str = "默认值";
}