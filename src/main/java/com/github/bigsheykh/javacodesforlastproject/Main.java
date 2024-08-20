package com.github.bigsheykh.javacodesforlastproject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;

public class Main {

    public static String getStringOfResolvedType(ResolvedType a) {
        if (a.isVoid())
            return "";
        String name = a.describe();
        return name.substring(0, name.length() - a.arrayLevel() * 2);
    }

    public static String getNameOfType(String fullName) {
        while (fullName.contains(".")) {
            fullName = fullName.substring(1);
        }
        return fullName;
    }

    public static void methodCall(MethodCallExpr node) {
        NodeList<Expression> a = node.getArguments();
        ArrayList<ResolvedType> types = new ArrayList<>();
        Set<String> importingTypes = new HashSet<>();
        a.forEach(ae -> types.add(ae.calculateResolvedType()));
        ResolvedMethodDeclaration methodType = node.resolve();
        StringBuffer testGenerated = new StringBuffer();
        if (methodType.accessSpecifier() == AccessSpecifier.PUBLIC) {
            System.out.println(node.toString());
            System.out.println();
            if (!methodType.getReturnType().isVoid())
                importingTypes.add(getStringOfResolvedType(methodType.getReturnType()));
            importingTypes.add(methodType.getQualifiedName().substring(0,
                    methodType.getQualifiedName().length() - node.getNameAsString().length() - 1));
            types.forEach(ae -> importingTypes.add(getStringOfResolvedType(ae)));
            importingTypes.forEach(impor -> testGenerated.append("import " + impor + "\n"));
            testGenerated.append("\n");
            testGenerated.append("Class Test{\n");
            testGenerated.append("\tpublic ");
            testGenerated.append(getNameOfType(methodType.getReturnType().describe()));
            testGenerated.append(" testMethod(");
            testGenerated.append(methodType.getClassName());
            testGenerated.append(" context");
            int number = 1;
            for (ResolvedType theType : types) {
                testGenerated.append(", ");
                testGenerated.append(theType.describe());
                testGenerated.append(" argNumber");
                testGenerated.append(number);
                number++;
            }
            testGenerated.append("){\n");
            testGenerated.append("\t\t");
            if (!methodType.getReturnType().isVoid())
                testGenerated.append("return ");
            testGenerated.append("context.");
            testGenerated.append(node.getNameAsString());
            testGenerated.append("(");
            number = 1;
            for (ResolvedType theType : types) {
                testGenerated.append("argNumber");
                testGenerated.append(number);
                testGenerated.append(", ");
                number++;
            }
            if (number > 1)
                testGenerated.delete(testGenerated.length() - 2, testGenerated.length());
            testGenerated.append(");\n");
            testGenerated.append("\t}\n");
            testGenerated.append("}\n");
            testGenerated.append("\n");
            System.out.println(testGenerated.toString());
        }
        System.out.println();
        System.out.println();
    }

    public static void construcorCall(ObjectCreationExpr node) {
        NodeList<Expression> a = node.getArguments();
        // ArrayList<ResolvedType> types = new ArrayList<>();
        // a.forEach(ae -> types.add(ae.calculateResolvedType()));
        // // MethodCallExpr newCallExpr = new MethodCallExpr();
        // System.out.println(node.toString());
        // System.out.println(node.getTypeAsString());
        // System.out.println(node.getType());
        // System.out.println(node.resolve().getClassName());
        // System.out.println(node.resolve().accessSpecifier());
        // System.out.println(node.getType().getNameWithScope());
        // // System.out.println(node);
        // types.forEach(ae -> System.out.println(ae.describe()));
        // System.out.println();
        // System.out.println();
    }

    public static void main(String[] args) {

        System.out.println("Hello World!");

        // File pomFile = new File(CustomModelling.getBaseDir(), "pom.xml");
        // new CustomModelling(pomFile);
        String FILE_PATH = "repos/bguerout/jongo/src/main/java/org/jongo/MongoCollection.java";
        // String FILE_PATH =
        // "repos/bguerout/jongo/src/main/java/org/jongo/ResultHandlerFactory.java";
        String SRC_PATH = "repos/bguerout/jongo/src/main/java";
        try {
            TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(
                    new File(SRC_PATH));
            TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
            TypeSolver jarSolver = new JarTypeSolver(
                    "repos/bguerout/jongo/target/jongo-1.6.0-SNAPSHOT.jar");

            CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
            combinedSolver.add(jarSolver);
            combinedSolver.add(javaParserTypeSolver);
            combinedSolver.add(reflectionTypeSolver);
            String base = System.getProperty("user.home");
            combinedSolver.add(new JarTypeSolver(
                    base + "/.m2/repository/org/mongodb/mongodb-driver-legacy/4.8.0/mongodb-driver-legacy-4.8.0.jar"));
            combinedSolver.add(new JarTypeSolver(
                    base + "/.m2/repository/org/mongodb/mongodb-driver-core/4.8.0/mongodb-driver-core-4.8.0.jar"));
            combinedSolver.add(new JarTypeSolver(base
                    + "/.m2/repository/org/mongodb/bson/4.8.0/bson-4.8.0.jar"));

            JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedSolver);
            StaticJavaParser
                    .getParserConfiguration()
                    .setSymbolResolver(symbolSolver)
                    .setStoreTokens(true);

            CompilationUnit cu = StaticJavaParser.parse(new File(FILE_PATH));

            ArrayList<Expression> expressionList = new ArrayList<>();
            cu.findAll(ObjectCreationExpr.class).forEach(ae -> expressionList.add(ae));
            cu.findAll(MethodCallExpr.class).forEach(ae -> expressionList.add(ae));
            cu.findAll(MethodCallExpr.class).forEach(ae -> methodCall(ae));
            cu.findAll(ObjectCreationExpr.class).forEach(ae -> construcorCall(ae));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
