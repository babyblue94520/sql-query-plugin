package pers.clare.sqlquery.processor;

import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;
import pers.clare.sqlquery.annotation.SQLEntityTest;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("pers.clare.sqlquery.annotation.SQLEntityTest")
public class SQLEntityProcessor extends AbstractProcessor {
    Messager messager;
    JavacTrees trees;
    TreeMaker treeMaker;
    Names names;
    Symtab symtab;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
        this.symtab = Symtab.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.test(roundEnv);

        return true;
    }

    private void test(RoundEnvironment roundEnv){
        messager.printMessage(Diagnostic.Kind.NOTE, "SQLEntityTest process begin!");
        try {
            Set<? extends Element> sqlEntityTests = roundEnv.getElementsAnnotatedWith(SQLEntityTest.class);

            for (Element element : sqlEntityTests) {
                messager.printMessage(Diagnostic.Kind.NOTE, element.getSimpleName());

                System.out.println(element.getSimpleName());
                System.out.println(element.getClass().getName());
                System.out.println(element.asType());
                JCTree jcTree = trees.getTree(element);
                jcTree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCTree.JCClassDecl jcClass) {
                        try {
                            super.visitClassDef(jcClass);
                            System.out.println(jcClass.name);
                            System.out.println(jcClass.defs.size());
                            JCTree.JCVariableDecl test = treeMaker.VarDef(
                                    treeMaker.Modifiers(Flags.PRIVATE )
                                    , names.fromString("aaa")
//                                    , treeMaker.Type(symtab.stringType)
                                    , treeMaker.TypeIdent(TypeTag.INT)
//                                    , treeMaker.Literal("11")
                                    ,null
                            );
                            jcClass.defs = jcClass.defs.append(test);
                            JCTree.JCStatement ret = treeMaker.Return(treeMaker.Ident(names.fromString("id")));
                            jcClass.defs = jcClass.defs.append(
                                    treeMaker.MethodDef(
                                            treeMaker.Modifiers(Flags.PUBLIC )
                                            , names.fromString("getId")
                                            , treeMaker.Type(symtab.longType)
                                            ,  List.nil()
                                            , List.nil()
                                            , List.nil()
                                            , treeMaker.Block(0, List.of(ret))
                                            , null
                                    )
                            );
//                            System.out.println(jcClass.defs.size());
//                            for (JCTree jcTree : jcClass.defs) {
//
//                                System.out.println(jcTree);
//                                if (jcTree instanceof JCTree.JCVariableDecl) {
//                                    System.out.println("------------------");
//                                    JCTree.JCVariableDecl variable = (JCTree.JCVariableDecl) jcTree;
//                                    System.out.println(variable.name + " " + variable.vartype);
////                                    if (variable.sym != null) {
////                                        System.out.println("A " + variable.sym.getAnnotation(Id.class));
////                                        System.out.println("A " + variable.sym.getAnnotation(Column.class));
////                                    }
//                                } else {
////                            System.out.println("not "+jcTree.getClass());
//                                }
//
//                            }
                            System.out.println(" end ");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        messager.printMessage(Diagnostic.Kind.NOTE, "SQLEntityTest process end!");
    }

    private void writeBuilderFile(
            String className, Map<String, String> setterMap)
            throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(builderClassName);

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {

            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }
            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println();
            out.print("    private ");
            out.print(simpleClassName);
            out.print(" object = new ");
            out.print(simpleClassName);
            out.println("();");
            out.println();
            out.print("    public ");
            out.print(simpleClassName);
            out.println(" build() {");
            out.println("        return object;");
            out.println("    }");
            out.println();
            setterMap.forEach((methodName, argumentType) -> {
                out.print("    public ");
                out.print(builderSimpleClassName);
                out.print(" ");
                out.print(methodName);

                out.print("(");

                out.print(argumentType);
                out.println(" value) {");
                out.print("        object.");
                out.print(methodName);
                out.println("(value);");
                out.println("        return this;");
                out.println("    }");
                out.println();
            });
            out.println("}");
        }
    }
}
