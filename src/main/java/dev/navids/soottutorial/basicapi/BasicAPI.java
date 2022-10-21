package dev.navids.soottutorial.basicapi;

import dev.navids.soottutorial.visual.Visualizer;
import soot.*;
import soot.jimple.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.Options;
import soot.toolkits.scalar.Pair;

import java.io.File;
import java.util.*;

public class BasicAPI {

    // source Directory Location
    public static String sourceDirectory = System.getProperty("user.dir") + File.separator + "demo" + File.separator + "BasicAPI";
    // Class to analysis
    public static String circleClassName = "Circle";

    public static void setupSoot() {
        G.reset();

// Uncomment line below to import essential Java classes 取消下面的注释以导入基本的Java类
//        Options.v().set_prepend_classpath(true);
// Comment the line below to not have phantom refs (you need to uncomment the line above)
        Options.v().set_prepend_classpath(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_soot_classpath(sourceDirectory);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_process_dir(Collections.singletonList(sourceDirectory));
        Options.v().set_whole_program(true);
        Scene.v().loadNecessaryClasses();
        PackManager.v().runPacks();
    }

    public static void main(String[] args) {
        setupSoot();

        // Access to Classes
        SootClass circleClass = reportSootClassInfo();

        // Access to Fields
        SootField radiusField = reportSootFieldInfo(circleClass);

        // Access to Methods
        SootMethod areaMethod = reportSootMethodInfo(circleClass);

        // Access to Body (units, locals)
        System.out.println("-----Body-----");
        JimpleBody body = (JimpleBody) areaMethod.getActiveBody();
        reportLocalInfo(body);

        Stmt firstNonIdentitiyStmt = body.getFirstNonIdentityStmt();
        int c = 0;
        for (Unit u : body.getUnits()) {
            c++;
            Stmt stmt = (Stmt) u;
            System.out.printf("[%d]: %s%n", c, stmt );
            if(stmt.equals(firstNonIdentitiyStmt))
                System.out.println("    This statement is the first non-identity statement!");
            if(stmt.containsFieldRef())
                reportFieldRefInfo(radiusField, stmt);
            if(doesInvokeMethod(stmt, "int area()", circleClassName)){
                System.out.println("    This statement invokes 'int area()' method");
            }
            modifyBody(body, stmt);
        }
        for(Trap trap : body.getTraps()){
            System.out.println(trap);
        }

        try {
            body.validate();
            System.out.println("Body is validated! No inconsistency found.");
        }
        catch (Exception exception){
            System.out.println("Body is not validated!");
        }

        // Call graph
        System.out.println("-----CallGraph-----");
        CallGraph callGraph = Scene.v().getCallGraph();
        for (Iterator<Edge> it = callGraph.edgesOutOf(areaMethod); it.hasNext(); ){
            Edge edge = it.next();
            System.out.printf("Method '%s' invokes method '%s' through stmt '%s%n",
                    edge.src(), edge.tgt(), edge.srcUnit());
        }
        boolean drawGraph = args.length > 0 && args[0].equals("draw");
        if (drawGraph) {
//            Visualizer.v().addCallGraph(callGraph);
            Visualizer.v().addCallGraph(callGraph,
                    edge -> edge.src().getDeclaringClass().isApplicationClass(),
                    sootMethod -> new Pair<>(
                            sootMethod.getDeclaringClass().isApplicationClass()
                                    ? "cg_node, default_color" : "cg_node, cg_lib_class"
                            , sootMethod.getDeclaringClass().isApplicationClass()
                                    ? sootMethod.getSubSignature() : sootMethod.getSignature())
                );
            Visualizer.v().draw();
        }
    }

    private static void reportLocalInfo(JimpleBody body) {
        System.out.println("-----Local variables-----");
        System.out.printf("Local variables count: %d%n", body.getLocalCount());
        Local thisLocal = body.getThisLocal();
        Type thisType = thisLocal.getType();
        Local paramLocal = body.getParameterLocal(0);
        System.out.printf("thisLocal (body.getThisLocal): %s%n", thisLocal);
        System.out.printf("thisType (thisLocal.getType): %s%n", thisType);
        System.out.printf("paramLocal (body.getParameterLocal): %s%n", paramLocal);
    }

    private static SootMethod reportSootMethodInfo(SootClass circleClass) {
        System.out.println("-----Method-----");
        System.out.printf("List of %s's methods:%n", circleClass.getName());

        for (SootMethod sootMethod : circleClass.getMethods())
            System.out.printf("- %s%n",sootMethod.getName());
        SootMethod getCircleCountMethod = circleClass.getMethod("int getCircleCount()");
        System.out.printf("Method Signature: %s%n", getCircleCountMethod.getSignature());
        System.out.printf("Method Subsignature: %s%n", getCircleCountMethod.getSubSignature());
        System.out.printf("Method Name: %s%n", getCircleCountMethod.getName());
        // what is declaring class ?
        System.out.printf("Declaring class: %s%n", getCircleCountMethod.getDeclaringClass());

        int methodModifers = getCircleCountMethod.getModifiers();
        System.out.printf("Method %s is public: %b, is static: %b, is final: %b%n", getCircleCountMethod.getName(),
                                        Modifier.isPublic(methodModifers),
                                        Modifier.isStatic(methodModifers),
                                        Modifier.isFinal(methodModifers));
        SootMethod constructorMethod = circleClass.getMethodByName("<init>");
        try{
            SootMethod areaMethod = circleClass.getMethodByName("area");
        }
        catch (Exception exception){
            System.out.println("Th method 'area' is overloaded and Soot cannot retrieve it by name");
        }
        return circleClass.getMethod("int area(boolean)");
    }

    private static SootField reportSootFieldInfo(SootClass circleClass) {
        System.out.println("-----Field-----");
        SootField radiusField = circleClass.getField("radius", IntType.v());
        SootField piField = circleClass.getField("double PI");
        System.out.printf("Field %s is final: %b%n", piField, piField.isFinal());
        System.out.printf("Field %s is pubic: %b%n", radiusField, radiusField.isPublic());
        return radiusField;
    }

    private static SootClass reportSootClassInfo() {
        System.out.println("-----Class-----");
        // load class
        SootClass circleClass = Scene.v().getSootClass(circleClassName);
        System.out.printf("The class %s is an %s class, loaded with %d methods! %n",
                circleClass.getName(),
                circleClass.isApplicationClass() ? "Application" : "Library",
                circleClass.getMethodCount());
        String wrongClassName = "Circrle";

        SootClass notExistedClass = Scene.v().getSootClassUnsafe(wrongClassName, false);
        System.out.printf("getClassUnsafe: Is the class %s null? %b%n", wrongClassName, notExistedClass==null);
        try{
            notExistedClass = Scene.v().getSootClass(wrongClassName);
            System.out.printf("getClass creates a phantom class for %s: %b%n",
                    wrongClassName,
                    notExistedClass.isPhantom());
        }catch (Exception exception){
            System.out.printf("getClass throws an exception for class %s.%n", wrongClassName);
        }

        Type circleType = circleClass.getType();
        System.out.printf("Class '%s' is same as class of type '%s': %b%n",
                circleClassName,
                circleType.toString(),
                circleClass.equals(Scene.v().getSootClass(circleType.toString())));

        assert notExistedClass != null;
        Type wrongType = notExistedClass.getType();
        System.out.printf("Class '%s' is same as class of type '%s': %b%n",
                circleClassName,
                wrongType.toString(),
                circleClass.equals(Scene.v().getSootClass(wrongClassName.toString())));
        return circleClass;
    }

    private static void modifyBody(JimpleBody body, Stmt stmt) {
        stmt.apply(new AbstractStmtSwitch() {
            @Override
            public void caseIfStmt(IfStmt stmt) {
                System.out.printf("    (Before change) if condition '%s' is true goes to stmt '%s'%n",
                        stmt.getCondition(), stmt.getTarget());
                stmt.setTarget(body.getUnits().getSuccOf(stmt));
                System.out.printf("    (After change) if condition '%s' is true goes to stmt '%s'%n",
                        stmt.getCondition(), stmt.getTarget());
            }
        });
    }

    private static boolean doesInvokeMethod(Stmt stmt, String subsignature, String declaringClass) {
        if (!stmt.containsInvokeExpr())
            return false;

        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        invokeExpr.apply(new AbstractJimpleValueSwitch() {
            @Override
            public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                System.out.printf("    StaticInvokeExpr '%s' from class '%s'%n", v, v.getType());
            }

            @Override
            public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                System.out.printf("    VirtualInvokeExpr '%s' from local '%s' with type %s%n",
                        v, v.getBase(), v.getBase().getType());
            }

            @Override
            public void defaultCase(Object v) {
                super.defaultCase(v);
            }
        });
        return invokeExpr.getMethod().getSubSignature().equals(subsignature)
                && invokeExpr.getMethod().getDeclaringClass().getName().equals(declaringClass);
    }

    private static void reportFieldRefInfo(SootField radiusField, Stmt stmt) {
        FieldRef fieldRef = stmt.getFieldRef();
        fieldRef.apply(new AbstractRefSwitch() {
            @Override
            public void caseStaticFieldRef(StaticFieldRef v) {
                // A static field reference
            }

            @Override
            public void caseInstanceFieldRef(InstanceFieldRef v) {
                if(v.getField().equals(radiusField)){
                    System.out.printf("    Field %s is used through FieldRef '%s'. " +
                            "The base local of FieldRef has type '%s'%n", radiusField, v, v.getBase().getType());
                }
            }
        });
    }

}
