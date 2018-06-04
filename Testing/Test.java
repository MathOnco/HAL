package Testing;

public class Test {
    public static int GetWoundsPerStep(int killsPerWound,int killsPerStep){
        if(killsPerWound<killsPerStep){
            int ret1=killsPerStep/killsPerWound;
            int ret2=ret1+1;
            return Math.abs(ret1*killsPerWound-killsPerStep)<Math.abs(ret2*killsPerWound-killsPerStep)?-ret1:-ret2;
        }
        else{
            int ret1=killsPerWound/killsPerStep;
            int ret2=ret1+1;
            return Math.abs(killsPerWound*1.0/ret1-killsPerStep)<Math.abs(killsPerWound*1.0/ret2-killsPerStep)?ret1:ret2;
        }

    }

    public static void main(String[] args) {
        System.out.println(GetWoundsPerStep(10,92));
    }
}
