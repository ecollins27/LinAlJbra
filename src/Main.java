import lin_aljbra.Matrix;
import lin_aljbra.aljbra.Constant;
import lin_aljbra.aljbra.Expression;
import lin_aljbra.aljbra.Scalar;
import lin_aljbra.aljbra.trig.Trig;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args){
        Matrix.Symbolic matrix = new Matrix.Symbolic(new Expression[][]{
                {Trig.cos(Constant.PI.divide(new Scalar(4))),Trig.sin(Constant.PI.divide(new Scalar(4))).negate()},
                {Trig.sin(Constant.PI.divide(new Scalar(4))),Trig.cos(Constant.PI.divide(new Scalar(4)))}
        });
        ArrayList<Expression> eigenValues = matrix.exactEigenvalues();
//        for (Expression eigenValue : eigenValues) {
//            System.out.println(eigenValue.fullSimplify().toLaTeX());
//        }
    }
}