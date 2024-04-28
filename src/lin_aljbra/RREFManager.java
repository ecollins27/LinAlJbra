package lin_aljbra;

import java.util.Arrays;

class RREFManager<M extends Matrix<M,E>,E> {

    M matrix;
    E zero;

    public RREFManager(M matrix){
        this.matrix = matrix;
        zero = matrix.valueOf(0);
    }

    public M rref(){
        matrix = ref();
        int length = Math.min(matrix.getWidth(),matrix.getHeight());
        for (int i = length - 1; i > 0;i--){
            for (int j = i - 1; j >= 0; j--){
                matrix = eliminateEntry(j,i);
            }
        }
        matrix = normalize();
        return matrix;
    }

    public M ref(){
        matrix = reorderRows();
        int length = Math.min(matrix.getWidth(),matrix.getHeight());
        for (int i = 0; i < length;i++){
            for (int j = i + 1; j < matrix.getHeight(); j++){
                matrix = eliminateEntry(j,i);
            }
        }
        matrix = normalize();
        return matrix;
    }

    private M normalize(){
        for (int i = 0; i < Math.min(matrix.getHeight(),matrix.getWidth());i++){
            E value = matrix.get(i,i);
            if (!value.equals(zero)){
                matrix = multiplyRow(i,matrix.divide(matrix.valueOf(1), value));
            }
        }
        return matrix;
    }

    private M eliminateEntry(int i, int j){
        E value = matrix.get(j,j);
        if (value.equals(zero) || matrix.get(i,j).equals(zero)){
            return matrix;
        };
        matrix = multiplyRow(i, matrix.divide(value, matrix.get(i,j)));
        matrix = subtractRow(i,j);
        return matrix;
    }


    private M subtractRow(int r1, int r2){
        E[][] newElements = matrix.newArrayInstance(matrix.height, matrix.width);
        for (int i = 0; i < matrix.height; i++) {
            for (int j = 0; j < matrix.width; j++) {
                if (i == r1){
                    newElements[i][j] = matrix.subtract(matrix.get(i,j), matrix.get(r2,j));
                } else {
                    newElements[i][j] = matrix.get(i,j);
                }
            }
        }
        return matrix.newMatrixInstance(newElements);
    }

    private M multiplyRow(int index, E value){
        E[][] newElements = matrix.newArrayInstance(matrix.height, matrix.width);
        for (int i = 0; i < matrix.height; i++) {
            for (int j = 0; j < matrix.width; j++) {
                if (i == index){
                    newElements[i][j] = matrix.multiply(matrix.get(i,j), value);
                } else {
                    newElements[i][j] = matrix.get(i,j);
                }
            }
        }
        return matrix.newMatrixInstance(newElements);
    }

    private M reorderRows(){
        int length = Math.min(matrix.getWidth(), matrix.getHeight());
        int[] ordering = new int[length];
        long maxPossible = 0;
        for (int i = 0; i < length;i++){
            ordering[i] = i;
            maxPossible += Math.pow(10,i);
        }
        long maxScore = scoreRowOrdering(ordering);
        if (maxScore == maxPossible){
            return matrix;
        }
        Arrays.fill(ordering,Integer.MAX_VALUE);
        ordering = findOptimalOrdering(ordering,0);
        E[][] newElements = matrix.newArrayInstance(matrix.height, matrix.width);
        for (int i = 0; i < matrix.getHeight(); i++) {
            for (int j = 0; j < matrix.getWidth(); j++) {
                if (i >= ordering.length){
                    newElements[i][j] = matrix.get(i,j);
                } else {
                    newElements[i][j] = matrix.get(ordering[i],j);
                }
            }
        }
        return matrix.newMatrixInstance(newElements);
    }

    private int[] findOptimalOrdering(int[] ordering, int index){
        if (index >= ordering.length) {
            return ordering;
        }
        boolean[] options = new boolean[ordering.length];
        Arrays.fill(options,true);
        int[] optimalOrdering = new int[ordering.length];
        for (int i = 0; i < ordering.length;i++){
            if (ordering[i] != Integer.MAX_VALUE) {
                options[ordering[i]] = false;
            }
            optimalOrdering[i] = i;
        }
        long maxScore = 0;
        int[] orderingClone = ordering.clone();
        for (int i = 0; i < ordering.length;i++){
            if (options[i]){
                orderingClone[index] = i;
                int[] subOptimal = findOptimalOrdering(orderingClone,index + 1);
                long score = scoreRowOrdering(subOptimal);
                if (score > maxScore){
                    maxScore = score;
                    optimalOrdering = subOptimal;
                }
            }
        }
        return optimalOrdering;
    }

    private long scoreRowOrdering(int[] order){
        long sum = 0;
        int length = Math.min(matrix.getWidth(), matrix.getHeight());
        for (int i = 0; i < length; i++) {
            if (!matrix.get(order[i],i).equals(zero)){
                sum += Math.pow(10,length - 1 - i);
            }
        }
        return sum;
    }
}