import java.util.Scanner;

class Main {

    public static void printSequences(int[][] numbers) {
        for(int[] rows : numbers ) {
            for(int elem : rows) {
                System.out.print(elem+" ");
            }
            System.out.println();
        }
    }

    public static int[][] merge(int[][] numbers) {
        int tmpArrayLength = numbers.length % 2 == 0 ? numbers.length/2 : numbers.length/2+1;
        int[][] tmpArrays = new int[tmpArrayLength][];

        while( numbers.length > 1 ) {
            for(int i = 0; i < tmpArrays.length; i++) {
                if( numbers.length == 2*i+1 ) {
                    tmpArrays[i] = numbers[2*i];
                } else {
                    tmpArrays[i] = new int[numbers[2 * i].length + numbers[2 * i + 1].length];
                    merge(tmpArrays[i], new int[][]{numbers[2 * i], numbers[2 * i + 1]});
                }
            }
            tmpArrayLength = tmpArrayLength % 2 == 0 ? tmpArrayLength/2 : tmpArrayLength/2+1;
            numbers = tmpArrays;
            //printSequences(numbers);
            tmpArrays = new int[tmpArrayLength][];
        }
        return numbers;
    }

    public static void merge(int[] tmpArray, int[][] array ) {
        int index1 = 0, index2 = 0, index3 = 0;
        while( index1 < array[0].length && index2 < array[1].length ) {
            if( array[0][index1] > array[1][index2] ) {
                tmpArray[index3] = array[0][index1];
                index1++;
            } else {
                tmpArray[index3] = array[1][index2];
                index2++;
            }
            index3++;
        }
        for(; index1 < array[0].length; index1++) {
            tmpArray[index3++] = array[0][index1];
        }
        for(; index2 < array[1].length; index2++) {
            tmpArray[index3++] = array[1][index2];
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int[][] numbers = new int[scanner.nextInt()][];
        for(int i = 0; i < numbers.length; i++) {
            numbers[i] = new int[scanner.nextInt()];
            for(int j = 0; j < numbers[i].length; j++) {
                numbers[i][j] = scanner.nextInt();
            }
        }
        numbers = merge(numbers);
        printSequences(numbers);
    }
}