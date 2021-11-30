package com.ryd.apt_processor;

import com.google.auto.service.AutoService;
import com.ryd.apt_annotation.info.InfoDesc;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/19/21 11:36 AM
 * @desc : com.ryd.apt_processor
 */
@AutoService(Processor.class)
public class InfoDescProcessor extends AbstractProcessor {


    /**
     * 节点工具类（类、函数、属性都是节点）
     */
    private Elements mElementUtils;

    /**
     * 类信息工具类
     */
    private Types mTypesUtils;

    /**
     *  文件生成器
     */
    private Filer mFiler;

    /**
     * 日志信息打印器
     */
    private Messager mMessager;

    /**
     * 模块名
     */
    private String mModuleName;

    /**
     * 做一些初始化的工作
     *
     * @param processingEnvironment 这个参数提供了若干工具类，供编写生成 Java 类时所使用
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        mTypesUtils = processingEnvironment.getTypeUtils();
        //
        mFiler = processingEnvironment.getFiler();
        mMessager = processingEnvironment.getMessager();
        mModuleName = processingEnvironment.getOptions().get("MODULE_NAME");

        mMessager.printMessage(Diagnostic.Kind.NOTE,"============>init 执行");
    }

    /**
     * 编写生成 Java 类的相关逻辑
     *
     * @param set  支持处理的注解集合
     * @param roundEnvironment  通过该对象查找指定注解下的节点信息
     * @return  true: 表示注解已处理，后续注解处理器无需再处理它们；false: 表示注解未处理，可能要求后续注解处理器处理
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()){
            return false;
        }

        //获取当前注解下的节点信息
        Set<? extends Element> rootElements = roundEnvironment.getElementsAnnotatedWith(InfoDesc.class);
        if (rootElements == null || rootElements.isEmpty()){
            return false;
        }

        MethodSpec.Builder printInfoBuilder = MethodSpec.methodBuilder("printInfo")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(String.class,"param");
        printInfoBuilder.addStatement("$T.out.println($S)",System.class,"模块: "+mModuleName);
        printInfoBuilder.addStatement("$T.out.println($L)",System.class,"\"参数:= \""+"+param");

        for (Element element:rootElements) {
            //当前节点名称
            String elementName = element.getSimpleName().toString();
            //当前节点下注解的属性
            String desc = element.getAnnotation(InfoDesc.class).desc();
            // 构建方法体
            printInfoBuilder.addStatement("$T.out.println($S)", System.class, "节点: " + elementName + "  " + "描述: " + desc);

        }

        MethodSpec methodSpec = printInfoBuilder.build();

        // 构建输出文件类ClassInfoDesc
        TypeSpec typeSpec = TypeSpec.classBuilder("ClassInfoDesc")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build();

        //指定包名，构建文件体
        JavaFile javaFile = JavaFile.builder("com.ryd.androidapt",typeSpec).build();
        try {
            // 创建文件
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMessager.printMessage(Diagnostic.Kind.NOTE,"============>process 执行");
        return true;
    }

    /**
     * 接收外来传入的参数，最常用的形式就是在 build.gradle 脚本文件里的 javaCompileOptions 的配置
     *
     * @return 属性的 Key 集合
     */
    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add("MODULE_NAME");
        mMessager.printMessage(Diagnostic.Kind.NOTE,"============>getSupportedOptions 执行");
        return Collections.unmodifiableSet(hashSet);
    }

    /**
     * 当前注解处理器支持的注解集合，如果支持，就会调用 process 方法
     *
     * @return 支持的注解集合
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> hashSet = new HashSet<>();
        hashSet.add(InfoDesc.class.getCanonicalName());
        mMessager.printMessage(Diagnostic.Kind.NOTE,"============>getSupportedAnnotationTypes 执行");
        return Collections.unmodifiableSet(hashSet);
    }

    /**
     * 编译当前注解处理器的 JDK 版本
     *
     * @return JDK 版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        mMessager.printMessage(Diagnostic.Kind.NOTE,"============>ggetSupportedSourceVersion 执行");
        return SourceVersion.RELEASE_8;
    }


}
