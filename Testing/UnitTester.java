package Testing;

import HAL.Interfaces.VoidFunction;
import HAL.Util;

import java.util.ArrayList;

public class UnitTester {


//    public static double[] ConvergenceTester1D(){
//    }

    ArrayList<VoidFunction> tests=new ArrayList<>();
    ArrayList<Boolean> passRecord=new ArrayList<>();
    ArrayList<String> names=new ArrayList<>();
    ArrayList<Long> runTimes=new ArrayList<>();

    public void AddTest(String name,VoidFunction Test){
        this.names.add(name);
        this.tests.add(Test);
        this.passRecord.add(true);
    }
    public void RunTests(final boolean timeit){
        Util.MultiThread(tests.size(),(i)->{
            long startTime=0;
            if(timeit){
                startTime=System.nanoTime();
            }
            try{
                tests.get(i).Execute();
                passRecord.set(i,true);
            }catch(Exception e){
                System.err.println("\nTest "+names.get(i)+" Failed");
                e.printStackTrace();
                passRecord.set(i,false);
            }
            if(timeit) {
                runTimes.add(System.nanoTime() - startTime);
            }
        });
        int failed=0;
        for (Boolean record : passRecord) {
            failed+=record?0:1;
        }
        if(timeit){
            for (int i = 0; i < tests.size(); i++) {
                System.out.println("Test "+names.get(i)+"\t Run Time: "+runTimes.get(i));
            }
        }
        System.out.println("\n"+(passRecord.size()-failed)+" tests passed, "+failed+" tests failed ");
    }

    public static void AssertEqual(String assertID,double correctValue,double experimentalValue, double errorTol){
        if(Math.abs(correctValue-experimentalValue)>errorTol){
            throw new IllegalStateException("Assertion "+assertID+" failed! experimental value: "+experimentalValue+" not within tolerance: "+errorTol+" of correct value: "+correctValue);
        }
    }
    public static void AssertEqual(String assertID,double correctValue,double experimentalValue){
        if(correctValue!=experimentalValue){
            throw new IllegalStateException("Assertion "+assertID+" failed! experimental value: "+experimentalValue+" does not equal correct value: "+correctValue);
        }
    }

    public static void AssertEqual(String assertID,int correctValue,int experimentalValue){
        if(correctValue!=experimentalValue){
            throw new IllegalStateException("Assertion "+assertID+" failed! experimental value: "+experimentalValue+" does not equal correct value: "+correctValue);
        }
    }

    public static void AssertEqual(String assertID,long correctValue,long experimentalValue){
        if(correctValue!=experimentalValue){
            throw new IllegalStateException("Assertion "+assertID+" failed! experimental value: "+experimentalValue+" does not equal correct value: "+correctValue);
        }
    }
    public static void AssertRelativeError(String assertID,double correctValue,double experimentalValue, double errorTol){
        double relError=Math.abs((correctValue-experimentalValue)/correctValue);
        if(relError>errorTol){
            throw new IllegalStateException("Assertion "+assertID+" failed! experimental value: "+experimentalValue+" relative error: "+relError+" not within tolerance: "+errorTol+". expected value: "+correctValue);
        }
    }

    public static void main(String[] args) {
        //runs all tests
        UnitTester tester=new UnitTester();
        MathTests.AddTests(tester);
        PDETests.AddTests(tester);
        AgentTests.AddTests(tester);
        tester.RunTests(true);
    }
}
