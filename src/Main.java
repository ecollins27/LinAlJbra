import lin_aljbra.Matrix;

public class Main {

    public static void main(String[] args){
        Matrix.Symbolic matrix = new Matrix.Symbolic(new long[][]{
                {1,2,0},
                {-2,1,2},
                {1,3,1}
        });
        System.out.println(matrix.exactEigenvalues());
        Matrix.Numeric eigenVector = matrix.approximateDominantEigenvector(100);
        System.out.println(eigenVector);
        System.out.println(matrix.approximateEigenvalue(eigenVector));
    }
}