package com.ryd.apt_processor;

import com.google.auto.service.AutoService;
import com.ryd.apt_annotation.BindAnim;
import com.ryd.apt_annotation.BindArray;
import com.ryd.apt_annotation.BindBitmap;
import com.ryd.apt_annotation.BindColor;
import com.ryd.apt_annotation.BindString;
import com.ryd.apt_annotation.BindView;
import com.ryd.apt_annotation.OnClick;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * @author : ruanyandong
 * @e-mail : ruanyandong@didiglobal.com
 * @date : 11/23/21 4:10 PM
 * @desc : com.ryd.apt_processor
 */
@AutoService(Processor.class)
public class ButterKnifeProcessor extends AbstractProcessor {
    // 节点工具
    private Elements mElementUtils;
    // 日志工具
    private Messager mMessagerUtils;
    // java文件生成工具
    private Filer mFilerUtils;
    // 类信息工具
    private Types mTypesUtils;
    // 模块名字
    private String mModuleName;
    private String mVersion;

    static final String superClassPackage = "com.ryd.apt_runtime";
    static final String unBinder = "Unbinder";

    private static final String MODULE_NAME = "MODULE_NAME";
    private static final String VERSION = "VERSION";

    private static final String COLOR_STATE_LIST_TYPE = "android.content.res.ColorStateList";
    private static final String BITMAP_TYPE = "android.graphics.Bitmap";
    private static final String ANIMATION_TYPE = "android.view.animation.Animation";
    private static final String DRAWABLE_TYPE = "android.graphics.drawable.Drawable";
    private static final String TYPED_ARRAY_TYPE = "android.content.res.TypedArray";
    private static final String TYPEFACE_TYPE = "android.graphics.Typeface";
    private static final String NULLABLE_ANNOTATION_NAME = "Nullable";
    private static final String STRING_TYPE = "java.lang.String";
    private static final String INT_TYPE = "int";
    private static final String CHAR_SEQUENCE_TYPE = "java.lang.CharSequence";

    private static final String CONSTRUCTOR_PARAM_TARGET = "target";
    private static final String CONSTRUCTOR_PARAM_SOURCE_VIEW = "sourceView";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mElementUtils = processingEnvironment.getElementUtils();
        mMessagerUtils = processingEnvironment.getMessager();
        mFilerUtils = processingEnvironment.getFiler();
        mTypesUtils = processingEnvironment.getTypeUtils();
        mModuleName = processingEnvironment.getOptions().get(MODULE_NAME);
        mVersion = processingEnvironment.getOptions().get(VERSION);
        mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "MODULE_NAME " + mModuleName + " VERSION " + mVersion);
    }

    @Override
    public Set<String> getSupportedOptions() {
        HashSet<String> set = new HashSet<>();
        set.add(MODULE_NAME);
        set.add(VERSION);
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(BindView.class.getCanonicalName());
        set.add(OnClick.class.getCanonicalName());
        set.add(BindString.class.getCanonicalName());
        set.add(BindAnim.class.getCanonicalName());
        set.add(BindColor.class.getCanonicalName());
        set.add(BindArray.class.getCanonicalName());
        set.add(BindBitmap.class.getCanonicalName());
        return Collections.unmodifiableSet(set);
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    private boolean checkNull(Set<? extends Element> set) {
        return set == null || set.isEmpty();
    }

    /**
     * // ButterKnifeProcessor
     * package com.ryd.androidapt;
     * <p>
     * import androidx.annotation.UiThread;
     * import com.ryd.apt_runtime.Unbinder;
     * import java.lang.Override;
     * <p>
     * public class MainActivity_ViewBinding implements Unbinder {
     * private MainActivity target;
     *
     * @param set
     * @param roundEnvironment
     * @return
     * @UiThread public MainActivity_ViewBinding(final MainActivity target) {
     * this.target = target;
     * target.textView = target.findViewById(2131230900);
     * }
     * @Override public void unbind() {
     * MainActivity target = this.target;
     * if (target == null) throw new IllegalStateException("Bindings already cleared.");
     * this.target = null;
     * target.textView = null;
     * }
     * }
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        if (set == null || set.isEmpty()) {
            return false;
        }
        // field BindView
        Set<? extends Element> bindViewVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
        // method OnClick
        Set<? extends Element> onClickExecutableElementSet = roundEnvironment.getElementsAnnotatedWith(OnClick.class);
        // field BindString
        Set<? extends Element> bindStringVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindString.class);
        // field BindAnim
        Set<? extends Element> bindAnimVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindAnim.class);
        // field BindColor
        Set<? extends Element> bindColorVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindColor.class);
        // field BindArray
        Set<? extends Element> bindArrayVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindArray.class);
        // field BindBitmap
        Set<? extends Element> bindBitmapVariableElementSet = roundEnvironment.getElementsAnnotatedWith(BindBitmap.class);

        if (checkNull(bindViewVariableElementSet) &&
                checkNull(onClickExecutableElementSet) &&
                checkNull(bindStringVariableElementSet) &&
                checkNull(bindAnimVariableElementSet) &&
                checkNull(bindColorVariableElementSet) &&
                checkNull(bindArrayVariableElementSet) &&
                checkNull(bindBitmapVariableElementSet)) {
            return false;
        }

        // 存储每个activity里面需要处理的元素,包括字段和方法
        Map<TypeElement, List<Element>> classFieldMap = new HashMap<>();

        // filed BindView
        if (!checkNull(bindViewVariableElementSet)) {
            // 收集每个activity里面的BindView元素
            for (Element element : bindViewVariableElementSet) {
                VariableElement variableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
                //List<Element> elements = classFieldMap.get(typeElement);
                //                if (elements == null){
                //                    elements = new ArrayList<>();
                //                    classFieldMap.put(typeElement,elements);
                //                }
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(variableElement);
            }
        }

        // method OnClick
        if (!checkNull(onClickExecutableElementSet)) {
            // 收集每个Activity里面的OnClick元素
            for (Element element : onClickExecutableElementSet) {
                ExecutableElement executableElement = (ExecutableElement) element;
                TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
                //List<Element> elements = classFieldMap.get(typeElement);
                //                if (elements == null){
                //                    elements = new ArrayList<>();
                //                    classFieldMap.put(typeElement,elements);
                //                }
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(executableElement);
            }
        }

        // field BindString
        if (!checkNull(bindStringVariableElementSet)) {
            for (Element element : bindStringVariableElementSet) {
                VariableElement bindStringVariableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) bindStringVariableElement.getEnclosingElement();
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(bindStringVariableElement);
            }
        }

        // field BindAnim
        if (!checkNull(bindAnimVariableElementSet)) {
            for (Element element : bindAnimVariableElementSet) {
                VariableElement bindAnimVariableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) bindAnimVariableElement.getEnclosingElement();
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(bindAnimVariableElement);
            }
        }

        // field BindColor
        if (!checkNull(bindColorVariableElementSet)) {
            for (Element element : bindColorVariableElementSet) {
                VariableElement bindColorVariableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) bindColorVariableElement.getEnclosingElement();
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(bindColorVariableElement);
            }
        }

        // field BindArray
        if (!checkNull(bindArrayVariableElementSet)) {
            for (Element element : bindArrayVariableElementSet) {
                VariableElement bindArrayVariableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement) bindArrayVariableElement.getEnclosingElement();
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(bindArrayVariableElement);
            }
        }

        if (!checkNull(bindBitmapVariableElementSet)){
            for (Element element:bindBitmapVariableElementSet){
                VariableElement bindBitmapVariableElement = (VariableElement) element;
                TypeElement typeElement = (TypeElement)bindBitmapVariableElement.getEnclosingElement();
                List<Element> elements = classFieldMap.computeIfAbsent(typeElement, k -> new ArrayList<>());
                elements.add(bindBitmapVariableElement);
            }
        }

        //遍历 Activity 中的节点，通过 JavaPoet 生成 Java 文件
        for (Map.Entry<TypeElement, List<Element>> entry : classFieldMap.entrySet()) {

            // 获取当前的Activity?
            TypeElement typeElement = entry.getKey();
            // 获取当前Activity下的所有需要处理的元素
            List<Element> elementList = entry.getValue();
            // 获取activity类名
            String activityName = typeElement.getSimpleName().toString();
            // 获取包名
            PackageElement packageElement = (PackageElement) typeElement.getEnclosingElement();
            String packageName = mElementUtils.getPackageOf(typeElement).toString();
            mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, " " + packageElement.getSimpleName() + " " + packageElement.getQualifiedName() + " " + packageName);
            // 父类
            ClassName superClassName = ClassName.get(superClassPackage, unBinder);
            // 当前类名
            ClassName className = ClassName.bestGuess(activityName);

            //创建类并继承UnBinder
            TypeSpec.Builder classBuilder = TypeSpec.classBuilder(activityName + "_ViewBinding")
                    .addModifiers(Modifier.PUBLIC)
                    .addSuperinterface(superClassName)
                    .addField(className, CONSTRUCTOR_PARAM_TARGET, Modifier.PRIVATE);


            //创建 unbind 方法
            MethodSpec.Builder unbindBuilder = MethodSpec.methodBuilder("unbind")
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC);
            unbindBuilder.addStatement("$T $L = this.$L", className, CONSTRUCTOR_PARAM_TARGET, CONSTRUCTOR_PARAM_TARGET);
            unbindBuilder.addStatement("if ($L == null) throw new IllegalStateException(\"Bindings already cleared.\")", CONSTRUCTOR_PARAM_TARGET);
            unbindBuilder.addStatement("this.$L = null", CONSTRUCTOR_PARAM_TARGET);

            //创建构造方法
            ClassName uiThreadClassName = ClassName.get("androidx.annotation", "UiThread");
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addAnnotation(uiThreadClassName)
                    .addParameter(className, CONSTRUCTOR_PARAM_TARGET, Modifier.FINAL)
                    .addParameter(ClassName.get("android.view", "View"), CONSTRUCTOR_PARAM_SOURCE_VIEW, Modifier.FINAL)
                    .addModifiers(Modifier.PUBLIC);
            constructorBuilder.addStatement("this.$L = $L", CONSTRUCTOR_PARAM_TARGET, CONSTRUCTOR_PARAM_TARGET);

            //便利变量集合，在构造方法中完成 findViewById 逻辑
            for (Element element : elementList) {
                if (element instanceof VariableElement && element.getAnnotation(BindView.class) != null) {
                    //通过注解拿到 id
                    int viewId = element.getAnnotation(BindView.class).value();
                    //获取变量名
                    String fieldName = element.getSimpleName().toString();
                    //$L for Literals 替换字符串
                    //$T for Types 替换类型,可以理解成对象
                    constructorBuilder.addStatement("$L.$L = $L.findViewById($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, viewId);
                    unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);

                } else if (element instanceof VariableElement && element.getAnnotation(BindString.class) != null) {
                    int stringResId = element.getAnnotation(BindString.class).stringId();
                    String fieldName = element.getSimpleName().toString();
                    // target.getString(R.string.app_name)
                    constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().getString($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, stringResId);
                    unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);

                } else if (element instanceof VariableElement && element.getAnnotation(BindAnim.class) != null) {
                    int animResId = element.getAnnotation(BindAnim.class).animResId();
                    String fieldName = element.getSimpleName().toString();
                    // import android.view.animation.AnimationUtils;
                    // AnimationUtils.loadAnimation(this,R.anim.translate_anim)
                    ClassName animationUtilsClassName = ClassName.get("android.view.animation", "AnimationUtils");
                    constructorBuilder.addStatement("$L.$L = $T.loadAnimation($L.getContext(),$L)", CONSTRUCTOR_PARAM_TARGET, fieldName, animationUtilsClassName, CONSTRUCTOR_PARAM_SOURCE_VIEW, animResId);
                    unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);

                } else if (element instanceof VariableElement && element.getAnnotation(BindColor.class) != null) {
                    int colorResId = element.getAnnotation(BindColor.class).colorResId();
                    String fieldName = element.getSimpleName().toString();
                    if (element.asType().getKind() == TypeKind.INT) {//int color = this.getResources().getColor(R.color.black);
                        constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().getColor($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, colorResId);
                    } else {
                        //ColorStateList colorStateList = AppCompatResources.getColorStateList(this,R.color.teal_200);
                        //import androidx.appcompat.content.res.AppCompatResources;
                        ClassName appCompatResourcesCLassName = ClassName.get("androidx.appcompat.content.res", "AppCompatResources");
                        constructorBuilder.addStatement("$L.$L = $T.getColorStateList($L.getContext(),$L)", CONSTRUCTOR_PARAM_TARGET, fieldName, appCompatResourcesCLassName, CONSTRUCTOR_PARAM_SOURCE_VIEW, colorResId);
                        unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);
                    }

                } else if (element instanceof VariableElement && element.getAnnotation(BindArray.class) != null) {
                    int arrayResId = element.getAnnotation(BindArray.class).arrayResId();
                    String fieldName = element.getSimpleName().toString();
                    mMessagerUtils.printMessage(Diagnostic.Kind.NOTE, "===" + element.asType().getKind() + " " + element.asType().getKind().getDeclaringClass());
                    TypeMirror typeMirror = element.asType();
                    if (TYPED_ARRAY_TYPE.equals(typeMirror.toString())) {
                        // TypedArray ta = getResources().obtainTypedArray(R.array.cs_language);
                        constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().obtainTypedArray($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, arrayResId);
                        unbindBuilder.addStatement("$L.$L.recycle()", CONSTRUCTOR_PARAM_TARGET, fieldName);
                        unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);
                    }
                    if (TypeKind.ARRAY.equals(typeMirror.getKind())) {
                        ArrayType arrayType = (ArrayType) typeMirror;
                        String componentType = arrayType.getComponentType().toString();
                        if (STRING_TYPE.equals(componentType)) {
                            //String[] cs_lang = getResources().getStringArray(R.array.cs_language);
                            constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().getStringArray($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, arrayResId);
                            unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);
                        } else if (INT_TYPE.equals(componentType)) {
                            //int[] ints = getResources().getIntArray(R.array.reminder_methods_values);
                            constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().getIntArray($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, arrayResId);
                            unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);
                        } else if (CHAR_SEQUENCE_TYPE.equals(componentType)) {
                            //CharSequence[] charSequences = getResources().getTextArray(R.array.cs_language);
                            constructorBuilder.addStatement("$L.$L = $L.getContext().getResources().getTextArray($L)", CONSTRUCTOR_PARAM_TARGET, fieldName, CONSTRUCTOR_PARAM_SOURCE_VIEW, arrayResId);
                            unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);
                        }
                    }

                }else if (element instanceof VariableElement && element.getAnnotation(BindBitmap.class) != null){
                    // import android.graphics.BitmapFactory;
                    //BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                    int bitmapResId = element.getAnnotation(BindBitmap.class).bitmapResId();
                    String fieldName = element.getSimpleName().toString();
                    ClassName bitmapFactoryClassName = ClassName.get("android.graphics","BitmapFactory");
                    constructorBuilder.addStatement("$L.$L = $L.decodeResource($L.getContext().getResources(),$L)",CONSTRUCTOR_PARAM_TARGET,fieldName,bitmapFactoryClassName,CONSTRUCTOR_PARAM_SOURCE_VIEW,bitmapResId);
                    unbindBuilder.addStatement("$L.$L = null", CONSTRUCTOR_PARAM_TARGET, fieldName);

                }else if (element instanceof ExecutableElement && element.getAnnotation(OnClick.class) != null) {
                    // 拿到所有需要设置点击监听器的view的id值
                    int[] ids = element.getAnnotation(OnClick.class).value();
                    // 方法名
                    String methodName = element.getSimpleName().toString();
                    ClassName viewName = ClassName.get("android.view", "View");
                    for (int id : ids) {
                        String fieldName = String.format("view%d", id);
                        FieldSpec.Builder builder = FieldSpec.builder(viewName, fieldName, Modifier.PRIVATE);
                        classBuilder.addField(builder.build());

                        constructorBuilder.addStatement("view$L = $L.findViewById($L)", id, CONSTRUCTOR_PARAM_SOURCE_VIEW, id);

                        ClassName clickListenerClassName = ClassName.get("android.view.View", "OnClickListener");
                        TypeSpec onClickListener = TypeSpec.anonymousClassBuilder("")
                                .addSuperinterface(clickListenerClassName)
                                .addMethod(MethodSpec.methodBuilder("onClick")
                                        .addAnnotation(Override.class)
                                        .addModifiers(Modifier.PUBLIC)
                                        .addParameter(viewName, "v")
                                        .returns(void.class)
                                        .addStatement("$L.$L()", CONSTRUCTOR_PARAM_TARGET, methodName)
                                        .build())
                                .build();

                        constructorBuilder.addStatement("view$L.setOnClickListener($L)", id, onClickListener);
                        unbindBuilder.addStatement("view$L = null", id);
                    }
                }

            }

            //添加方法
            classBuilder.addMethod(unbindBuilder.build());
            classBuilder.addMethod(constructorBuilder.build());

            //将 Java 写成 Class 文件
            try {
                JavaFile.builder(packageName, classBuilder.build())
                        .addFileComment("ButterKnifeProcessor")
                        .build()
                        .writeTo(mFilerUtils);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

}
