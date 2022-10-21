public class FizzBuzz {

    public void printFizzBuzz(int k){
        if (k%15==0)
            System.out.println("FizzBuzz");
        else if (k%5==0)
            System.out.println("Buzz");
        else if (k%3==0)
            System.out.println("Fizz");
        else
            System.out.println(k);
    }

    public void fizzBuzz(int n){
        for (int i=1; i<=n; i++)
            printFizzBuzz(i);
    }
}

/*
    r0 := @this: FizzBuzz
    i0 := @parameter0: int

    $i1 = i0 % 15
    if $i1 != 0 goto $i2 = i0 % 5
    $r4 = <java.lang.System: java.io.PrintStream out>
    virtualinvoke $r4.<java.io.PrintStream: void println(java.lang.String)>("FizzBuzz")
    goto [?= return]

    $i2 = i0 % 5
    if $i2 != 0 goto $i3 = i0 % 3
    $r3 = <java.lang.System: java.io.PrintStream out>
    virtualinvoke $r3.<java.io.PrintStream: void println(java.lang.String)>("Buzz")
    goto [?= return]

    $i3 = i0 % 3
    if $i3 != 0 goto $r1 = <java.lang.System: java.io.PrintStream out>
    $r2 = <java.lang.System: java.io.PrintStream out>
    virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.String)>("Fizz")
    goto [?= return]

    $r1 = <java.lang.System: java.io.PrintStream out>
    virtualinvoke $r1.<java.io.PrintStream: void println(int)>(i0)
    return
* */
