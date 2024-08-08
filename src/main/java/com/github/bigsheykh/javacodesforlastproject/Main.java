package com.github.bigsheykh.javacodesforlastproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
// import com.github.javaparser.ast.body.;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.MemoryTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;

// import com.github.javaparser.symbolsolver.resolution.typesolvers.;
import com.github.javaparser.ast.type.Type;

// import java.nio.file.Path;

public class Main {

    public static void printType(VariableDeclarator node) {
        System.out.println("name: " +
                node.getNameAsString());
        Type type = node.getType();
        System.out.println("Field type: " +
                node.getType().resolve().toString());

        System.out.println("type: " + node.getTypeAsString());
        System.out.println();
    }

    public static void methodCall(MethodCallExpr node) {
        NodeList<Expression> a = node.getArguments();
        ArrayList<ResolvedType> types = new ArrayList<>();
        a.forEach(ae -> types.add(ae.calculateResolvedType()));
        ResolvedMethodDeclaration methodType = node.resolve();
        // MethodCallExpr newCallExpr = new MethodCallExpr();
        System.out.println(node.toString());
        System.out.println(methodType.accessSpecifier());
        System.out.println(methodType.getReturnType().describe());
        System.out.println(methodType.getClassName());
        System.out.println(methodType.getPackageName());
        System.out.println(methodType.getQualifiedName());
        System.out.println(methodType.isStatic());
        System.out.println(methodType.isAbstract());
        // System.out.println(node.get);
        types.forEach(ae -> System.out.println(ae.describe()));
        System.out.println();
        System.out.println();
    }

    public static void construcorCall(ObjectCreationExpr node) {
        NodeList<Expression> a = node.getArguments();
        ArrayList<ResolvedType> types = new ArrayList<>();
        a.forEach(ae -> types.add(ae.calculateResolvedType()));
        // MethodCallExpr newCallExpr = new MethodCallExpr();
        System.out.println(node.toString());
        System.out.println(node.getTypeAsString());
        System.out.println(node.getType());
        System.out.println(node.resolve().getClassName());
        System.out.println(node.resolve().accessSpecifier());
        System.out.println(node.getType().getNameWithScope());
        // System.out.println(node);
        types.forEach(ae -> System.out.println(ae.describe()));
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {

        System.out.println("Hello World!");

        // File pomFile = new File(CustomModelling.getBaseDir(), "pom.xml");
        // new CustomModelling(pomFile);
        String FILE_PATH = "repos/bguerout/jongo/src/main/java/org/jongo/MongoCollection.java";
        // String FILE_PATH = "repos/bguerout/jongo/src/main/java/org/jongo/ResultHandlerFactory.java";
        String SRC_PATH = "repos/bguerout/jongo/src/main/java";
        try {
            // TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
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
            combinedSolver.add(new JarTypeSolver(base
                    + "/.m2/repository/org/mongodb/mongodb-driver-legacy/4.8.0/mongodb-driver-legacy-4.8.0-sources.jar"));
            combinedSolver.add(new JarTypeSolver(
                    base + "/.m2/repository/org/mongodb/mongodb-driver-core/4.8.0/mongodb-driver-core-4.8.0.jar"));
            combinedSolver.add(new JarTypeSolver(base
                    + "/.m2/repository/org/mongodb/mongodb-driver-core/4.8.0/mongodb-driver-core-4.8.0-sources.jar"));
            combinedSolver.add(new JarTypeSolver(base
                    + "/.m2/repository/org/mongodb/bson/4.8.0/bson-4.8.0-sources.jar"));
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
