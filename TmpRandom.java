import java.util.Random;
public class TmpRandom {
    private static int randomLevel(Random random) {
        int lvl = 0;
        while (random.nextDouble() < 0.5) {
            lvl++;
        }
        return lvl;
    }
    public static void main(String[] args) {
        Random rnd = new Random(0xCAFEBEEF);
        for (int i = 0; i < 5; i++) {
            System.out.println(randomLevel(rnd));
        }
    }
}
